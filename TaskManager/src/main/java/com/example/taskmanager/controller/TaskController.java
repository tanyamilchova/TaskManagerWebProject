package com.example.taskmanager.controller;

import com.example.taskmanager.model.DTOs.TaskAddDTO;
import com.example.taskmanager.model.DTOs.TaskEditDTO;
import com.example.taskmanager.model.DTOs.TaskInfoDTO;
import com.example.taskmanager.service.TaskService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.File;
import java.util.List;
import org.springframework.core.io.Resource;

    @RestController
    public class TaskController extends AbstractController{

    @Autowired
    private TaskService taskService;

    @PostMapping("/tasks")
    public TaskInfoDTO add(@RequestBody final TaskAddDTO addData, final HttpSession session){
        final long idLogged=loggedId(session);
        return  taskService.add(addData, idLogged);
    }
    @PutMapping("/tasks/{id}")
    public TaskInfoDTO edit(@RequestBody final TaskEditDTO editData, @PathVariable final long id, HttpSession session){
        final long userId=loggedId(session);
        return taskService.edit(editData,id,userId);
    }
    @GetMapping("/tasks/{id}")
    public TaskInfoDTO getById(@PathVariable final long id,final HttpSession session){
        final long userId=loggedId(session);
        return taskService.getTaskById(id,userId);
    }
    @DeleteMapping("/tasks/{id}/delete")
    public TaskInfoDTO delete(@PathVariable final long id,final HttpSession session){
        final long userId=loggedId(session);
        return  taskService.delete(id,userId);
    }
    @PostMapping("tasks/unfinished")
    public List<TaskInfoDTO> filter( final HttpSession session){
        final long userId=loggedId(session);
        List<TaskInfoDTO>taskInfoDTOList=taskService.filterUnfinishdTasks(userId);
        return taskInfoDTOList;
    }
    @GetMapping("/users/tasks/csv")
    public ResponseEntity<Resource> getUnfinishedTasksCsv(final HttpSession session) {

        final long userId = loggedId(session);
       File csvFile = taskService.getUnfinishedTasks(userId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/csv");
        headers.add("Content-Disposition", "attachment; filename=" + csvFile.getName());

        Resource resource=Util.getResaurce(csvFile );
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(csvFile.length())
                .body(resource);
    }

}

