package it.portfolio.violihate.cignalottu.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest (
        @NotBlank(message = "L'email non può essere vuota")
        @Email(message = "Formato email non valido")
        String email,

        @NotBlank(message = "La password non può essere vuota")
        String password)
{
}
