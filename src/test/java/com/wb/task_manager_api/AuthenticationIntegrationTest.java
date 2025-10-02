package com.wb.task_manager_api;

import com.fasterxml.jackson.databind.ObjectMapper; // Para converter objetos Java em JSON

import com.wb.task_manager_api.domain.user.UserRepository;
import com.wb.task_manager_api.domain.user.UserRole;
import com.wb.task_manager_api.dto.auth.RegisterDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


// 1. Inicia o contexto completo da aplicação Spring Boot
@SpringBootTest
// 2. Habilita a simulação de chamadas HTTP (MockMvc)
@AutoConfigureMockMvc
// 3. Usa um perfil de teste (opcional, mas boa prática para usar um DB em memória, como H2)
@ActiveProfiles("test")
public class AuthenticationIntegrationTest {
    // 4. Objeto para simular requisições HTTP para a API
    @Autowired
    private MockMvc mockMvc;

    // Objeto para serializar DTOs em JSON
    @Autowired
    private ObjectMapper objectMapper;

    // Injetamos o repositório para limpar o DB entre os testes
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Deve permitir o registro, login e acesso a rota protegida com token válido")
    void registerLoginAndAccessProtectedRoute_ShouldBeSuccessful() throws Exception {
        // --- 1. PREPARAÇÃO: DTO de Registro ---
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("teste.integracao@email.com");
        registerDTO.setPassword("senha123");
        registerDTO.setRole(UserRole.USER);

        // --- 2. REGISTRO (POST /auth/register) ---
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk()); // Espera o código 200 OK

        // --- 3. LOGIN (POST /auth/login) ---
        // O DTO de Login é idêntico ao de Registro, mas para fins de clareza,
        // usamos as propriedades do objeto registerDTO
        String loginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk()) // Espera o código 200 OK
                .andReturn().getResponse().getContentAsString();

        // Extrai o token da resposta JSON (usando ObjectMapper para parsear a resposta)
        String token = objectMapper.readTree(loginResponse).get("token").asText();

        // --- 4. ACESSO À ROTA PROTEGIDA (GET /api/tasks) ---
        // Tenta acessar a rota protegida com o JWT no cabeçalho Authorization
        mockMvc.perform(get("/api/tasks")
                        .header("Authorization", "Bearer " + token) // Insere o token aqui
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()); // Espera o código 200 OK
    }

    // **************** TESTES DE ACESSO NEGADO ****************

    @Test
    @DisplayName("Deve retornar 403 Forbidden ao tentar acessar rota protegida sem token")
    void accessProtectedRoute_ShouldReturnForbidden_WhenNoTokenIsPresent() throws Exception {
        // Tenta acessar a rota GET /api/tasks sem enviar o token JWT
        mockMvc.perform(get("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()); // Espera o código 403 Forbidden
    }
}
