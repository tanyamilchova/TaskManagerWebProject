package com.example.taskmanager.model.DTOs;

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
@NoArgsConstructor
@AllArgsConstructor

public class TaskEditDTO {
    private int id;
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotBlank
    @DateTimeFormat
    @Future
    private LocalDateTime  dueDate;
    @NotBlank
    private String status;
    @NotBlank
    private String priority;
    private long userId;

}
