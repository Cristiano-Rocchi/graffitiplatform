package cristianorocchi.graffitiplatform.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRespDTO {

    private String token;
    private String username;
    private String email;
    private String ruolo;
}
