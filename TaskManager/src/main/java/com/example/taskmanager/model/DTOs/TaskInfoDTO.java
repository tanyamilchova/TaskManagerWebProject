package com.example.taskmanager.model.DTOs;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskInfoDTO {
    private long id;
    private String title;
    private String description;
    private LocalDateTime dateTimeCreation;
    private LocalDateTime  dueDate;
    private String status;
    private String priority;
    private long userId;
}
