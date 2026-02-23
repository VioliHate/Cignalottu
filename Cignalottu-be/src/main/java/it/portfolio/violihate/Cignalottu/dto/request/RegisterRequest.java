package it.portfolio.violihate.cignalottu.dto.request;

import jakarta.validation.constraints.*;

public record RegisterRequest(

        @NotBlank(message = "L'email è obbligatoria")
        @Email(message = "Formato email non valido")
        @Size(max = 255, message = "L'email non può superare i 255 caratteri")
        String email,

        @NotBlank(message = "La password è obbligatoria")
        @Size(min = 8, max = 100, message = "La password deve avere tra 8 e 100 caratteri")
        String password,

        @NotBlank(message = "Il nome è obbligatorio")
        @Size(min = 2, max = 100, message = "Il nome deve avere tra 2 e 100 caratteri")
        String firstName,

        @NotBlank(message = "Il cognome è obbligatorio")
        @Size(min = 2, max = 100, message = "Il cognome deve avere tra 2 e 100 caratteri")
        String lastName

) {}
