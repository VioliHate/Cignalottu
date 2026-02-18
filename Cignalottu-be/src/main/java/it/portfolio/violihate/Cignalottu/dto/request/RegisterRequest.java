package it.portfolio.violihate.cignalottu.dto.request;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "Formato email non valido")
    @Size(max = 255)
    private String email;

    @NotBlank(message = "La password è obbligatoria")
    @Size(min = 8, max = 100, message = "La password deve avere almeno 8 caratteri")
    private String password;

    @NotBlank(message = "Il nome è obbligatorio")
    @Size(min = 2, max = 100)
    private String firstName;

    @NotBlank(message = "Il cognome è obbligatorio")
    @Size(min = 2, max = 100)
    private String lastName;
}
