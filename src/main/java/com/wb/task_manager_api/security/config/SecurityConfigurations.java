package com.wb.task_manager_api.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // 1. Marca como classe de configuração
@EnableWebSecurity // 2. Habilita o módulo de segurança do Spring Boot
public class SecurityConfigurations {
    /**
     * Define o SecurityFilterChain: O "Filtro de Segurança"
     * É o coração da segurança, onde definimos as regras de acesso.
     * @param http Objeto para configurar a segurança HTTP
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // 3. Desabilita CSRF: APIs RESTful usam JWT e não dependem de sessão
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 4. Define como STATELESS
                .authorizeHttpRequests(authorize -> authorize
                        // 5. Permite acesso PÚBLICO a rotas de AUTENTICAÇÃO e REGISTRO
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()

                        // 6. Protege as rotas de TAREFAS: Somente quem tem a ROLE_USER pode acessar
                        .requestMatchers("/api/tasks/**").hasRole("USER")

                        // 7. Qualquer outra requisição, por enquanto, exige autenticação
                        .anyRequest().authenticated()
                )
                .build();
    }

    /**
     * 8. Define o AuthenticationManager
     * É o gerenciador que será usado para processar a autenticação (login).
     * O Spring o gerencia, apenas o expomos como um Bean.
     * @param authenticationConfiguration Configuração de autenticação
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 9. Define o PasswordEncoder (Codificador de Senhas)
     * Usamos BCrypt, o padrão recomendado para criptografar senhas.
     * @return Implementação do PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt é um algoritmo seguro de hashing de senha
        return new BCryptPasswordEncoder();
    }
}
