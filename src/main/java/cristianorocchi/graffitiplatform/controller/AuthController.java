package cristianorocchi.graffitiplatform.controller;


import cristianorocchi.graffitiplatform.entities.User;
import cristianorocchi.graffitiplatform.enums.Ruolo;
import cristianorocchi.graffitiplatform.exceptions.BadRequestException;
import cristianorocchi.graffitiplatform.payloads.NewUserDTO;
import cristianorocchi.graffitiplatform.payloads.NewUserRespDTO;
import cristianorocchi.graffitiplatform.payloads.UserLoginDTO;
import cristianorocchi.graffitiplatform.payloads.UserLoginRespDTO;
import cristianorocchi.graffitiplatform.services.AuthService;
import cristianorocchi.graffitiplatform.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    // Endpoint per il login
    @PostMapping("/login")
    public UserLoginRespDTO login(@RequestBody UserLoginDTO payload) {
        return new UserLoginRespDTO(this.authService.checkCredentialsAndGenerateToken(payload));
    }

    // Endpoint per la registrazione
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public NewUserRespDTO register(@RequestBody @Validated NewUserDTO body, BindingResult validationResult) {
        // Verifica errori di validazione
        if (validationResult.hasErrors()) {
            String messages = validationResult.getAllErrors().stream()
                    .map(objectError -> objectError.getDefaultMessage())
                    .collect(Collectors.joining(". "));
            throw new BadRequestException("Ci sono stati errori nel payload. " + messages);
        } else {
            // Creazione del nuovo utente
            User user = new User();
            user.setUsername(body.username());
            user.setEmail(body.email());
            user.setPassword(body.password());
            user.setRuolo(Ruolo.USER);  // Imposta il ruolo come USER di default

            // Salva il nuovo utente
            User savedUser = this.userService.save(user);

            return new NewUserRespDTO(savedUser.getId());
        }
    }
}
