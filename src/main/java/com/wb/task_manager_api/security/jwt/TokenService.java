package com.wb.task_manager_api.security.jwt;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.wb.task_manager_api.domain.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.auth0.jwt.JWT;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;

    /**
     * Gera o Token JWT para um usuário autenticado.
     * @param user O usuário para quem o token será gerado.
     * @return O token JWT como String.
     */
    public String generateToken(User user) {
        try {
            // 2. Cria o algoritmo de criptografia, usando a chave secreta
            Algorithm algorithm = Algorithm.HMAC256(secret);

            // 3. Constrói o token com as informações
            String token = JWT.create()
                    .withIssuer("task-manager-api") // Quem emitiu o token (nosso app)
                    .withSubject(user.getEmail())   // O "assunto" do token (quem ele representa)
                    .withExpiresAt(genExpirationDate()) // Tempo de expiração (1 hora)
                    .sign(algorithm); // Assina o token com o algoritmo e a chave

            return token;

        } catch (JWTCreationException exception) {
            // Se houver algum problema na geração do token
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    /**
     * Valida um Token JWT e retorna o "assunto" (email do usuário).
     * @param token O token JWT que veio na requisição.
     * @return O email do usuário contido no token.
     */
    public String validateToken(String token) {
        try {
            // Usa o mesmo algoritmo e chave secreta para verificar a assinatura
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.require(algorithm)
                    .withIssuer("task-manager-api") // Verifica se foi emitido por nós
                    .build() // Constrói o verificador
                    .verify(token) // Valida o token
                    .getSubject(); // Pega o "assunto" (o email)

        } catch (JWTVerificationException exception) {
            // Se o token for inválido, expirado ou a assinatura não coincidir
            return ""; // Retorna string vazia, indicando falha na validação
        }
    }

    /**
     * Gera o carimbo de tempo para a expiração do token (Exemplo: 1 hora a partir de agora).
     */
    private Instant genExpirationDate() {
        return LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.of("-03:00"));
        // O token expira 1 hora após a criação, no fuso horário de São Paulo (-03:00)
    }
}
