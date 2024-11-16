package cristianorocchi.graffitiplatform.payloads;

import java.util.UUID;

public record UserStatsDTO(
        UUID id,
        String username,
        String email,
        long graffitiCount,
        long streetArtCount,
        long tagCount,
        long totalImageCount
) {}
