package cristianorocchi.graffitiplatform.payloads;

import cristianorocchi.graffitiplatform.enums.StatoOpera;

import java.util.UUID;

public record TagRespDTO(
        UUID id,
        String artista,
        String luogo,
        String immagineUrl,
        StatoOpera stato,
        int annoCreazione,
        String username // Nome utente di chi ha caricato il graffito
) {}

