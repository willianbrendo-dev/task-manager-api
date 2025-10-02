package com.wb.task_manager_api.controller;

import com.wb.task_manager_api.domain.user.User;
import com.wb.task_manager_api.domain.user.UserRepository;
import com.wb.task_manager_api.dto.auth.AuthenticationDTO;
import com.wb.task_manager_api.dto.auth.LoginResponseDTO;
import com.wb.task_manager_api.dto.auth.RegisterDTO;
import com.wb.task_manager_api.security.jwt.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    // Injeção de Dependências
    @Autowired // Embora o construtor seja preferido, @Autowired é comum em Controllers
    private AuthenticationManager authenticationManager; // Do SecurityConfigurations

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Do SecurityConfigurations (BCrypt)

    @Autowired
    private TokenService tokenService; // Nosso serviço de geração de token

    // **************** R E G I S T R O (POST /auth/register) ****************
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterDTO dto) {
        // 1. Verifica se o usuário já existe
        if(this.userRepository.findByEmail(dto.getEmail()) != null) {
            return ResponseEntity.badRequest().build(); // Retorna 400 Bad Request
        }

        // 2. Criptografa a senha
        String encryptedPassword = passwordEncoder.encode(dto.getPassword());

        // 3. Cria e salva o novo usuário no banco
        User newUser = new User(dto.getEmail(), encryptedPassword, dto.getRole());
        this.userRepository.save(newUser);

        // 4. Retorna 200 OK (ou 201 Created, dependendo da sua preferência)
        return ResponseEntity.ok().build();
    }

    // **************** L O G I N (POST /auth/login) ****************
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO dto) {
        // 1. Cria o token de autenticação que será processado pelo Spring Security
        var usernamePassword = new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());

        // 2. Tenta autenticar o usuário (chama o AuthenticationManager)
        // Se a autenticação falhar, uma exceção é lançada automaticamente pelo Spring
        var auth = this.authenticationManager.authenticate(usernamePassword);

        // 3. Se a autenticação for bem-sucedida, gera o token JWT
        var token = tokenService.generateToken((User) auth.getPrincipal());

        // 4. Retorna 200 OK com o token no corpo da resposta
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }
}
