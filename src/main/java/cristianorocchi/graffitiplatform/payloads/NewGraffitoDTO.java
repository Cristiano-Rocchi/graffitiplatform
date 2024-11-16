package cristianorocchi.graffitiplatform.payloads;

import cristianorocchi.graffitiplatform.enums.StatoOpera;
import jakarta.validation.constraints.*;

import java.time.Year;

public record NewGraffitoDTO(

        @NotEmpty(message = "Il luogo è obbligatorio")
        @Size(max = 15, message = "Il luogo non può superare i 15 caratteri")
        String luogo,

        @NotEmpty(message = "L'URL dell'immagine è obbligatorio")
        String immagineUrl,

        @NotNull(message = "Lo stato dell'opera è obbligatorio")
        StatoOpera stato,

        @Size(max = 15, message = "Il nome dell'artista non può superare i 15 caratteri")
        String artista,

        @Min(value = 1975, message = "L'anno di creazione non può essere inferiore al 1975")
        @Max(value = Year.MAX_VALUE, message = "L'anno di creazione non può essere successivo all'anno corrente")
        int annoCreazione
) {}


