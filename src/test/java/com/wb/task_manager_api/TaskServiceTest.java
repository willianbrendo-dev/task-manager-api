package com.wb.task_manager_api;

import com.wb.task_manager_api.domain.task.Task;
import com.wb.task_manager_api.domain.task.TaskRepository;
import com.wb.task_manager_api.domain.task.TaskStatus;
import com.wb.task_manager_api.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    // 3. Injeta as dependências mockadas (TaskRepository) na classe TaskService
    @InjectMocks
    private TaskService taskService;

    // Objeto Task de exemplo que será usado nos testes
    private Task mockTask;
    private final Long MOCK_ID = 1L;

    @BeforeEach
    void setup() {
        // Inicializa o objeto mockado antes de cada teste
        mockTask = new Task(
                MOCK_ID,
                "Testar Endpoint",
                "Garantir que o CRUD está OK",
                TaskStatus.PENDING,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now(),
                null
        );
    }

    // **************** TESTES FIND ALL ****************

    @Test
    @DisplayName("Deve retornar uma lista de tarefas quando houver dados")
    void findAll_ShouldReturnTaskList_WhenTasksExist() {
        // ARRANGE (Preparação):
        List<Task> taskList = List.of(mockTask, new Task());
        // Define o comportamento: Quando o repository for chamado, retorne a lista mockada
        when(taskRepository.findAll()).thenReturn(taskList);

        // ACT (Ação):
        List<Task> result = taskService.findAll();

        // ASSERT (Verificação):
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        // Verifica se o método do mock FOI REALMENTE CHAMADO (garante que não testamos um método vazio)
        verify(taskRepository, times(1)).findAll();
    }

    // **************** TESTES FIND BY ID ****************

    @Test
    @DisplayName("Deve retornar a tarefa quando o ID for encontrado")
    void findById_ShouldReturnTask_WhenIdExists() {
        // ARRANGE:
        // Define o comportamento: Quando findById(1L) for chamado, retorne a Task dentro de um Optional
        when(taskRepository.findById(MOCK_ID)).thenReturn(Optional.of(mockTask));

        // ACT:
        Optional<Task> result = taskService.findById(MOCK_ID);

        // ASSERT:
        assertTrue(result.isPresent());
        assertEquals(MOCK_ID, result.get().getId());
        verify(taskRepository, times(1)).findById(MOCK_ID);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando o ID não for encontrado")
    void findById_ShouldReturnEmptyOptional_WhenIdDoesNotExist() {
        // ARRANGE:
        // Define o comportamento: Para qualquer outro ID, retorne um Optional vazio
        when(taskRepository.findById(2L)).thenReturn(Optional.empty());

        // ACT:
        Optional<Task> result = taskService.findById(2L);

        // ASSERT:
        assertTrue(result.isEmpty());
        verify(taskRepository, times(1)).findById(2L);
    }

    // **************** TESTES SAVE (CREATE/UPDATE) ****************

    @Test
    @DisplayName("Deve salvar e retornar a tarefa com o ID gerado")
    void save_ShouldReturnSavedTask_WhenSuccessful() {
        // ARRANGE: Simula o save do repository, que retorna o mesmo objeto
        when(taskRepository.save(mockTask)).thenReturn(mockTask);

        // ACT:
        Task savedTask = taskService.save(mockTask);

        // ASSERT:
        assertNotNull(savedTask);
        assertEquals(MOCK_ID, savedTask.getId());
        verify(taskRepository, times(1)).save(mockTask);
    }

    // **************** TESTES DELETE ****************

    @Test
    @DisplayName("Deve chamar o método deleteById do repository")
    void deleteById_ShouldCallRepositoryDeleteById() {
        // ARRANGE: Não precisamos de mock, apenas de um ID

        // ACT:
        taskService.deleteById(MOCK_ID);

        // ASSERT:
        // Verifica se o método deleteById foi chamado EXATAMENTE 1 vez com o ID correto
        verify(taskRepository, times(1)).deleteById(MOCK_ID);
    }
}
