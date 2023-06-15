package com.example.taskmanager;


import com.example.taskmanager.controller.Util;
import com.example.taskmanager.model.DTOs.TaskAddDTO;
import com.example.taskmanager.model.DTOs.TaskInfoDTO;
import com.example.taskmanager.model.entities.Task;
import com.example.taskmanager.model.entities.User;
import com.example.taskmanager.model.exceptions.NotFoundException;
import com.example.taskmanager.model.exceptions.UnauthorizedException;
import com.example.taskmanager.model.repositories.TaskRepository;
import com.example.taskmanager.model.repositories.UserRepository;
import com.example.taskmanager.service.AbstractService;
import com.example.taskmanager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class TaskServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private TaskRepository taskRepository;
    @InjectMocks
    private TaskService taskService;
    @Mock
    private ModelMapper mapper;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void addValidDataTaskAddedSuccessfully() {

        long userId = 1;
        long taskId = 1;
        LocalDateTime now = LocalDateTime.now();

        TaskAddDTO taskAddDTO = new TaskAddDTO();
        taskAddDTO.setTitle("Title");
        taskAddDTO.setDescription("Descr");
        taskAddDTO.setPriority("LOW");
        taskAddDTO.setDateTimeCreation(LocalDateTime.now());
        taskAddDTO.setDueDate(LocalDateTime.now().plusDays(5));
        taskAddDTO.setStatus("ToDo");

        User user = new User();
        user.setId(userId);
        user.setEnable(true);

        Task task = new Task();
        task.setId(taskId);
        task.setUser(user);
        task.setDateTimeCreation(now);

        TaskInfoDTO expected = new TaskInfoDTO();
        expected.setId(taskId);
        expected.setUserId(userId);
        expected.setDateTimeCreation(taskAddDTO.getDateTimeCreation());
        expected.setPriority(taskAddDTO.getPriority());
        expected.setTitle(taskAddDTO.getTitle());
        expected.setDescription(taskAddDTO.getDescription());
        expected.setDueDate(taskAddDTO.getDueDate());
        expected.setStatus(taskAddDTO.getStatus());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mapper.map(taskAddDTO, Task.class)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(mapper.map(task, TaskInfoDTO.class)).thenReturn(expected);

        TaskInfoDTO result = taskService.add(taskAddDTO, userId);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void addInvalidUserIdThrowsException() {
        long userId = 1;
        TaskAddDTO taskAddDTO = new TaskAddDTO();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> taskService.add(taskAddDTO, userId));
    }
    @Test
    void getByIdValidIdAndOwnerReturnsTaskInfoDTO() {

        long taskId = 1;
        long userId = 3;
        User user=new User();
        user.setId(userId);
        Task task = new Task();
        task.setId(taskId);
        task.setUser(user);


        TaskInfoDTO expected = new TaskInfoDTO();
        expected.setId(taskId);
        expected.setUserId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(mapper.map(task, TaskInfoDTO.class)).thenReturn(expected);
        TaskInfoDTO result = taskService.getTaskById(taskId, userId);

        assertDoesNotThrow(()->taskService.ifOwner(userId,task));
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void getByIdValidIdAndNotOwnerReturnsTaskInfoDTO() {

        long taskId = 1;
        long userId = 2;

        User user=new User();
        user.setId(1);

        Task task = new Task();
        task.setId(taskId);
        task.setUser(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        assertThrows(UnauthorizedException.class,()-> taskService.getTaskById(taskId, userId));
    }

    @Test
    void getByIdInvalidIdThrowsException() {

        long taskId = 1;
        long userId = 1;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> taskService.getTaskById(taskId, userId));
    }
    @Test
    void delete_ValidIdAndOwnerDeletesTaskAndReturnsTaskInfoDTO() {

    long taskId = 1;
    long userId = 5;

    User user = new User();
    user.setId(userId);

    Task task = new Task();
    task.setId(taskId);
    task.setUser(user);

    TaskInfoDTO expected = new TaskInfoDTO();
    expected.setId(taskId);
    expected.setUserId(userId);

    when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(mapper.map(task, TaskInfoDTO.class)).thenReturn(expected);

    TaskInfoDTO result = taskService.getTaskById(taskId, userId);

    assertDoesNotThrow(()->taskService.ifOwner(userId,task));
    assertNotNull(result);
    assertEquals(expected, result);
}
    @Test
    void filterUnfinishedTasksReturnsFilteredTaskInfoDTOList() {

        long userId = 1;
        int page = 1;
        int size = 10;
        Task task1 = new Task();
        task1.setId(1);
        task1.setStatus(Util.ToDo);
        task1.setUser(new User());
        task1.getUser().setId(userId);

        Task task2 = new Task();
        task2.setId(2);
        task2.setStatus(Util.IN_PROGRESS);
        task2.setUser(new User());
        task2.getUser().setId(userId);

        List<Task> tasks = Arrays.asList(task1, task2);

        List<TaskInfoDTO> expected = Arrays.asList(
                mapper.map(task1, TaskInfoDTO.class),
                mapper.map(task2, TaskInfoDTO.class)
        );
        Pageable pageable= PageRequest.of(page,size);
        Page<Task>taskPage=new PageImpl<>(tasks,pageable,tasks.size());
        when(taskRepository.findByStatus(Util.ToDo, Util.IN_PROGRESS, userId,pageable)).thenReturn(taskPage);

        Page<TaskInfoDTO> result = taskService.filterUnfinishdTasks(userId,page,size);

        assertNotNull(result);
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), result.toList().get(i));
        }
    }
    @Test
    void getUnfinishedTasksReturnsTempFileWithCSVContent() throws IOException {

        long userId = 1;
        User user = new User();
        user.setId(userId);
        LocalDateTime localDateTime=LocalDateTime.now();

        Task task1 = new Task();
        task1.setId(1);
        task1.setTitle("Task 1");
        task1.setDescription("Description 1");
        task1.setDueDate(localDateTime.plusDays(1));
        task1.setUser(user);

        Task task2 = new Task();
        task2.setId(2);
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setDueDate(localDateTime.plusDays(2));
        task2.setUser(user);

        List<Task> tasks = Arrays.asList(task1, task2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.findByStatus(Util.ToDo, Util.IN_PROGRESS, userId)).thenReturn(tasks);

        File resultFile = taskService.getUnfinishedTasks(userId);

        assertNotNull(resultFile);
        assertTrue(resultFile.exists());

        List<String> lines = Files.readAllLines(resultFile.toPath());
        assertEquals(3, lines.size());

        String headerLine = lines.get(0);
        assertEquals("TaskId;Title;Description;IsFinished", headerLine);

        String task1Line = lines.get(1);
        assertEquals("1;Task 1;Description 1;"+ localDateTime.plusDays(1), task1Line);

        String task2Line = lines.get(2);
        assertEquals("2;Task 2;Description 2;"+localDateTime.plusDays(2), task2Line);
        resultFile.delete();
    }


}
