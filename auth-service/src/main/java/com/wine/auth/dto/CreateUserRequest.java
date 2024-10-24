package com.wine.auth.dto;

import com.wine.auth.validation.ValidEmail;
import com.wine.auth.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class CreateUserRequest {

    @NotBlank(message = "L'username non può essere lasciato vuoto.")
    @Size(min =  3, max = 15, message = "L'username deve avere un numero di caratteri compreso tra 3 e 15.")
    String username;

    @ValidPassword
    String password;

    @NotBlank(message = "L'email non può essere lasciata vuota.")
    @Email
    @ValidEmail
    String email;

    @NotBlank(message = "Il nome non può essere lasciato vuoto.")
    @Size(min =  3, max = 15, message = "Il nome deve avere un numero di caratteri compreso tra 3 e 15.")
    String firstName;

    @NotBlank(message = "Il cognome non può essere lasciato vuoto.")
    @Size(min =  3, max = 15, message = "Il cognome deve avere un numero di caratteri compreso tra 3 e 15.")
    String lastName;


}
