package com.wb.task_manager_api.dto.task;

import com.wb.task_manager_api.domain.task.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TaskRequestDTO {
    @NotBlank(message = "O título é obrigatório.")
    @Size(max = 100, message = "O título não pode ter mais de 100 caracteres.")
    private String title;

    private String description;

    @NotNull(message = "O status da tarefa é obrigatório.")
    private TaskStatus status;

    private LocalDateTime deadline;
}
