package com.example.taskmanager.model.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserWithoutPasswordDTO {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
}
