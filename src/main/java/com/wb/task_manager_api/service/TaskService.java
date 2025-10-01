package com.wb.task_manager_api.service;
import com.wb.task_manager_api.domain.task.Task;
import org.springframework.transaction.annotation.Transactional;
import com.wb.task_manager_api.domain.task.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    // Injeção de Dependência via Construtor
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // Busca: Otimização para leitura (readOnly = true)
    @Transactional(readOnly = true)
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    // Busca por ID
    @Transactional(readOnly = true)
    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    // Criação/Atualização: Deve ser transacional
    @Transactional
    public Task save(Task task) {
        return taskRepository.save(task);
    }

    // Deleção: Deve ser transacional
    @Transactional
    public void deleteById(Long id) {
        taskRepository.deleteById(id);
    }

}
