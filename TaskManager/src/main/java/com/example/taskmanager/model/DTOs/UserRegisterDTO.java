package com.example.taskmanager.model.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDTO {

    private long id;
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Password must contain at least one letter, one number, and one special character")
    private String password;
    @NotBlank(message = "Please confirm password")
    private String confirmPassword;
    @NotBlank
    @Email(message = "Invalid email")
    private String email;
    @NotBlank(message = "Enter a last name")
    @Pattern(regexp ="^[A-Za-z]{2,20}$",message = "Last name must be between 2 and 20 letters")
    private String lastName;
    @NotBlank(message = "Enter a first name")
    @Pattern(regexp ="^[A-Za-z]{2,20}$",message = "First name must be between 2 and 20 letters")
    private String firstName;

    @NotBlank
    private String roleName;
}
