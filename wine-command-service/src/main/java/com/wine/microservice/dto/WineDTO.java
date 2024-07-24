package com.wine.microservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WineDTO {

    private Long id;

    @NotBlank(message = "Il nome non può essere lasciato vuoto.")
    @Size(min =  3, max = 15, message = "Il nome deve avere un numero di caratteri compreso tra 3 e 15.")
    private String wineName;

    @NotBlank(message = "Il tipo di vino deve essere specificato.")
    @Size(min =  3, max = 15, message = "Il tipo di vino deve avere un numero di caratteri compreso tra 3 e 15.")
    private String wineType;

    @NotBlank(message = "Il tipo di uva utilizzato deve essere specificato.")
    @Size(min =  3, max = 30, message = "Il tipo di uva utilizzato deve avere un numero di caratteri compreso tra 3 e 30.")
    private String grape;

    @NotBlank(message = "La regione di provenienza deve essere specificata.")
    @Size(min =  3, max = 15, message = "La regione deve avere un numero di caratteri compreso tra 3 e 15.")
    private String region;

    @NotBlank(message = "La deneminazione deve essere specificata.")
    @Size(min =  3, max = 4, message = "La denominazione deve avere un numero di caratteri compreso tra 3 e 4.")
    private String denomination;

    //@NotNull(message = "L'annata deve essere specificata.")
    @Min(1900)
    @Max(2024)
    private int year;

    //@NotBlank(message = "La gradazione alcolica deve essere specificata.")
    @DecimalMin("7.0")
    @DecimalMax("18.0")
    private double alcoholPercentage;

    @NotBlank(message = "La descrizione del vino deve essere inserita.")
    @Size(min =  10, max = 200, message = "La descrizione del vino deve avere un numero di caratteri compreso tra 10 e 200.")
    private String wineDescription;

    private List<String> purchaseLinks = new ArrayList<>();

}
