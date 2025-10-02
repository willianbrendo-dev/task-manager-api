package com.wb.task_manager_api.dto.auth;

import com.wb.task_manager_api.domain.user.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDTO {
    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "O formato do email é inválido.")
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    private String password;

    @NotNull(message = "A role (permissão) é obrigatória.")
    private UserRole role;
}
