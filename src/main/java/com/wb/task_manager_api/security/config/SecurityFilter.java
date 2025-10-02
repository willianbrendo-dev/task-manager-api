package com.wb.task_manager_api.security.config;


import com.wb.task_manager_api.domain.user.UserRepository;
import com.wb.task_manager_api.security.jwt.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Este filtro é responsável por interceptar todas as requisições HTTP
 * para verificar a presença e validade de um Token JWT no cabeçalho Authorization.
 * Se o token for válido, ele autentica o usuário DENTRO DO CONTEXTO,
 * sem chamar o AuthenticationManager, evitando a recursão infinita (StackOverflowError).
 */
@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    // Usamos o UserRepository (que implementa UserDetailsService implicitamente ou explicitamente)
    // para carregar os detalhes do usuário pelo login (subject do token).
    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Obter o token do cabeçalho
        var token = this.recoverToken(request);

        if (token != null) {
            // 2. Validar o token e extrair o login do usuário (subject)
            var login = tokenService.validateToken(token);

            // 3. Carregar os detalhes do usuário
            // É CRUCIAL NÃO USAR AuthenticationManager AQUI.
            // Apenas carregamos o UserDetails do banco de dados (via UserRepository)
            UserDetails user = userRepository.findByEmail(login);

            if (user != null) {
                // 4. Criar o objeto de Autenticação e setar no SecurityContextHolder
                // Isso informa ao Spring Security que este usuário está autenticado para esta requisição.
                var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 5. Continua a cadeia de filtros
        filterChain.doFilter(request, response);
    }

    /**
     * Extrai o Token JWT do cabeçalho Authorization.
     */
    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        // Espera-se o formato "Bearer <TOKEN>"
        return authHeader.replace("Bearer ", "");
    }
}
