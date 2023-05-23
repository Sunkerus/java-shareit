package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class User {

        private Integer id;

        private String name;

        @Email(message = "Email not correct. Please enter correct email.")
        @NotBlank(message = "Email can't be empty. Please enter correct email.")
        private String email;
}
