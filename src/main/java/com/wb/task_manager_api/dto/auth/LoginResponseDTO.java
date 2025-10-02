package com.wb.task_manager_api.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDTO {
    // DTO simples para retornar o token JWT ao cliente.
    private String token;

    public LoginResponseDTO(String token) {
        this.token = token;
    }
}
