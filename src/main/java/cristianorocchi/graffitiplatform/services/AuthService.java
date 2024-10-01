package cristianorocchi.graffitiplatform.services;


import cristianorocchi.graffitiplatform.Security.JWTTools;
import cristianorocchi.graffitiplatform.entities.User;
import cristianorocchi.graffitiplatform.exceptions.UnauthorizedException;
import cristianorocchi.graffitiplatform.payloads.UserLoginDTO;
import cristianorocchi.graffitiplatform.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTTools jwtTools;

    @Autowired
    private PasswordEncoder bcrypt;

    public String checkCredentialsAndGenerateToken(UserLoginDTO body) {
        // 1. Trova l'utente tramite email
        User found = userRepository.findByUsernameOrEmail(body.email(), body.email())
                .orElseThrow(() -> new UnauthorizedException("Credenziali errate!"));

        // 1.1 Verifico se la password combacia con quella salvata nel database
        if (bcrypt.matches(body.password(), found.getPassword())) {
            // 2. Se le credenziali sono corrette, genero un token JWT
            return jwtTools.createToken(found);
        } else {
            // 3. Se le credenziali sono errate, restituisco un 401 (Unauthorized)
            throw new UnauthorizedException("Credenziali errate!");
        }
    }
}

