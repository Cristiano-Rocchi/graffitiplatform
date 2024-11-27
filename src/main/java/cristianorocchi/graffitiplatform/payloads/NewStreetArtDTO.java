package cristianorocchi.graffitiplatform.payloads;

import cristianorocchi.graffitiplatform.enums.StatoOpera;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewStreetArtDTO {

        @NotEmpty(message = "Il luogo è obbligatorio")
        @Size(max = 15, message = "Il luogo non può superare i 15 caratteri")
        private String luogo;

        @NotEmpty(message = "L'URL dell'immagine è obbligatorio")
        private String immagineUrl;

        @NotNull(message = "Lo stato dell'opera è obbligatorio")
        private StatoOpera stato;

        @Size(max = 20, message = "Il nome dell'artista non può superare i 20 caratteri")
        private String artista;

        @Min(value = 1975, message = "L'anno di creazione non può essere inferiore al 1975")
        @Max(value = Year.MAX_VALUE, message = "L'anno di creazione non può essere successivo all'anno corrente")
        private int annoCreazione;
}
