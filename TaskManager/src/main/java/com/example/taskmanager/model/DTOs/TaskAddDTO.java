package com.example.taskmanager.model.DTOs;

import com.example.taskmanager.model.entities.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskAddDTO {

    private long id;
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @DateTimeFormat
    private LocalDateTime dateTimeCreation;
    @DateTimeFormat
    @Future
    private LocalDateTime  dueDate;
    @NotBlank
    private String status;
    @NotBlank
    private String priority;
    private long userId;


}
