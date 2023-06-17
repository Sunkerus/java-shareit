package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class UserDto {


    private Long id;

    @NotBlank(message = "Name can't be empty. Please enter name")
    private String name;

    @Email(message = "Email not correct. Please enter correct email.")
    @NotBlank(message = "Email can't be empty. Please enter correct email.")
    private String email;

}
