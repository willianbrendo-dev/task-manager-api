package com.wb.task_manager_api.controller;

import com.wb.task_manager_api.domain.task.Task;
import com.wb.task_manager_api.dto.task.TaskRequestDTO;
import com.wb.task_manager_api.dto.task.TaskResponseDTO;
import com.wb.task_manager_api.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController // 1. Marca como um Controller que retorna JSON (REST)
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Retorna código HTTP 201
    public TaskResponseDTO create(@RequestBody @Valid TaskRequestDTO dto) {
        // @RequestBody: Mapeia o JSON para o DTO.
        // @Valid: Ativa as validações do DTO (@NotBlank, @Size).

        // 1. Converte DTO para Entidade
        Task taskToSave = new TaskResponseDTO().toEntity(dto);

        // 2. Salva no Service
        Task savedTask = taskService.save(taskToSave);

        // 3. Retorna a Entidade salva como DTO de Resposta
        return TaskResponseDTO.fromEntity(savedTask);
    }

    @GetMapping
    public List<TaskResponseDTO> findAll() {
        return taskService.findAll().stream()
                // Mapeia cada Entidade Task para TaskResponseDTO
                .map(TaskResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> findById(@PathVariable Long id) {
        // @PathVariable: Captura o ID da URL.

        return taskService.findById(id)
                // Se Optional tiver valor: retorna 200 OK com o DTO
                .map(task -> ResponseEntity.ok(TaskResponseDTO.fromEntity(task)))
                // Se Optional estiver vazio: retorna 404 Not Found
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> update(@PathVariable Long id, @RequestBody @Valid TaskRequestDTO dto) {
        return taskService.findById(id)
                .map(existingTask -> {
                    // Atualiza os campos na Entidade existente
                    existingTask.setTitle(dto.getTitle());
                    existingTask.setDescription(dto.getDescription());
                    existingTask.setStatus(dto.getStatus());
                    existingTask.setDeadline(dto.getDeadline());

                    Task updatedTask = taskService.save(existingTask); // O @PreUpdate é acionado aqui
                    return ResponseEntity.ok(TaskResponseDTO.fromEntity(updatedTask));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Retorna código HTTP 204 (Sucesso sem conteúdo)
    public void delete(@PathVariable Long id) {
        // Na vida real, você faria uma checagem se o recurso existe antes de deletar,
        // mas o JpaRepository.deleteById cuida do caso mais simples.
        taskService.deleteById(id);
    }
}
