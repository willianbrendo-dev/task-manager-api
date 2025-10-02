package com.wb.task_manager_api.security.config;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import com.wb.task_manager_api.domain.user.UserRepository;
import com.wb.task_manager_api.security.jwt.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    private final UserRepository userRepository;

    public SecurityFilter(TokenService tokenService, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Obtém o token da requisição
        var token = this.recoverToken(request);

        // 2. Se um token foi encontrado:
        if (token != null) {
            // 3. Valida o token e obtém o login (email) do usuário
            var login = tokenService.validateToken(token);

            // Se a validação foi bem-sucedida (login não é vazio):
            if (!login.isEmpty()) {
                // 4. Busca o UserDetails no banco pelo login/email
                UserDetails user = userRepository.findByEmail(login);

                // 5. Cria o objeto de autenticação para o Spring Security
                var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

                // 6. Define o usuário como autenticado no contexto da aplicação
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 7. Continua o fluxo da requisição (para o Controller ou próximo filtro)
        filterChain.doFilter(request, response);
    }

    /**
     * Tenta recuperar o token do cabeçalho "Authorization".
     */
    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        // O token JWT vem no formato "Bearer [TOKEN]", então removemos o prefixo.
        return authHeader.replace("Bearer ", "");
    }
}
