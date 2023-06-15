package com.example.taskmanager.service;

import com.example.taskmanager.controller.Util;
import com.example.taskmanager.model.DTOs.TaskAddDTO;
import com.example.taskmanager.model.DTOs.TaskEditDTO;
import com.example.taskmanager.model.DTOs.TaskInfoDTO;
import com.example.taskmanager.model.entities.Task;
import com.example.taskmanager.model.entities.User;
import com.example.taskmanager.model.exceptions.UnauthorizedException;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class TaskService extends AbstractService {


    public TaskInfoDTO add(final TaskAddDTO taskAddDTO, final long idLogged) {
        final User u = ifPresent(userRepository.findById(idLogged));
        Util.checkPriority(taskAddDTO.getPriority());
        final Task task = mapper.map(taskAddDTO, Task.class);
        task.setUser(u);
        task.setDateTimeCreation(LocalDateTime.now());
        taskRepository.save(task);

        return mapper.map(task, TaskInfoDTO.class);
    }

    public TaskInfoDTO edit(final TaskEditDTO editData, final long id, final long userId) {
        final Task task = ifPresent(taskRepository.findById(id));
        final User u = ifPresent(userRepository.findById(userId));
        if (task.getUser().getId() != userId) {
            throw new UnauthorizedException("Unauthorized role");
        }
        task.setTitle(editData.getTitle());
        task.setDescription(editData.getDescription());
        task.setStatus(editData.getStatus());
        task.setPriority(editData.getPriority());
        task.setDueDate(editData.getDueDate());
        taskRepository.save(task);
        return mapper.map(task, TaskInfoDTO.class);
    }


    public TaskInfoDTO getTaskById(final long id, final long userId) {
        final Task task = ifPresent(taskRepository.findById(id));
        ifOwner(userId,task);
        return mapper.map(task, TaskInfoDTO.class);
    }

    public TaskInfoDTO delete(final long id, final long userId) {
        final Task task = ifPresent(taskRepository.findById(id));
        if (ifOwner(userId, task)) {
            taskRepository.delete(task);
        }
        return mapper.map(task, TaskInfoDTO.class);
    }

    public Page<TaskInfoDTO> filterUnfinishdTasks(final long userId, final int page, final int size) {
        final Pageable pageable = PageRequest.of(page,size);
        return taskRepository.findByStatus(Util.ToDo, Util.IN_PROGRESS,  userId, pageable)
                .map(task -> mapper.map(task, TaskInfoDTO.class));
    }

    @SneakyThrows
    public File getUnfinishedTasks(final long userId) {
        User user = ifPresent(userRepository.findById(userId));
        List<Task> tasks = taskRepository.findByStatus(Util.ToDo, Util.IN_PROGRESS, userId);
        final StringBuilder csvContent = new StringBuilder("TaskId;Title;Description;IsFinished\n");
        for (Task task : tasks) {
            csvContent.append(task.getId()).append(";");
            csvContent.append(task.getTitle()).append(";");
            csvContent.append(task.getDescription()).append(";");
            csvContent.append(task.getDueDate()).append("\n");
        }
        File tempFile = File.createTempFile("unfinished_tasks", ".csv");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(csvContent.toString());
        }
        return tempFile;
    }

}



