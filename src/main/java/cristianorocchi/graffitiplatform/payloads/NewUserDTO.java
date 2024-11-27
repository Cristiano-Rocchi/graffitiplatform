package cristianorocchi.graffitiplatform.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewUserDTO {

        @NotBlank(message = "Lo username è obbligatorio.")
        @Size(min = 3, max = 12, message = "Lo username deve essere tra 3 e 12 caratteri.")
        private String username;

        @NotBlank(message = "L'email è obbligatoria.")
        @Email(message = "Inserisci un'email valida.")
        private String email;

        @NotBlank(message = "La password è obbligatoria.")
        private String password;
}
