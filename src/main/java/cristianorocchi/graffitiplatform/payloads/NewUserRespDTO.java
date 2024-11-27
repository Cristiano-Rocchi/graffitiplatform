package cristianorocchi.graffitiplatform.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class NewUserRespDTO {

    private UUID id;
}
