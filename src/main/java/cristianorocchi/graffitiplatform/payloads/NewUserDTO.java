package cristianorocchi.graffitiplatform.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NewUserDTO(
        @NotBlank(message = "Lo username è obbligatorio.")
        @Size(min = 3, max = 12, message = "Lo username deve essere tra 3 e 12 caratteri.")
        String username,

        @NotBlank(message = "L'email è obbligatoria.")
        @Email(message = "Inserisci un'email valida.")
        String email,

        @NotBlank(message = "La password è obbligatoria.")
        String password
) {}
