package cristianorocchi.graffitiplatform.controller;

import cristianorocchi.graffitiplatform.entities.User;
import cristianorocchi.graffitiplatform.payloads.UserStatsDTO;
import cristianorocchi.graffitiplatform.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Visualizza le informazioni dell'utente corrente
    @GetMapping("/me")
    public User getCurrentUser(@AuthenticationPrincipal User currentUser) {
        return currentUser;
    }

    // Aggiorna il profilo dell'utente corrente
    @PutMapping("/me")
    public User updateCurrentUser(@AuthenticationPrincipal User currentUser, @RequestBody User updatedData) {
        return userService.updateUser(currentUser.getId(), updatedData);
    }

    // Elimina il proprio account
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCurrentUser(@AuthenticationPrincipal User currentUser) {
        userService.delete(currentUser.getId());
    }

    // Solo l'admin può visualizzare tutti gli utenti
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers(Authentication authentication) {

        return userService.findAll();
    }

    // Solo l'admin può visualizzare un singolo utente
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public User getUserById(@PathVariable UUID userId) {
        return userService.findById(userId);
    }

    // Solo l'admin può cancellare un altro utente
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID userId) {
        userService.delete(userId);
    }

    // Solo l'admin può aggiornare un altro utente
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public User updateUser(@PathVariable UUID userId, @RequestBody User updatedData) {
        return userService.updateUser(userId, updatedData);
    }
    //  conteggio delle immagini caricate dall'utente
    @GetMapping("/me/stats")
    public Map<String, Long> getUserImageStats(@AuthenticationPrincipal User currentUser) {
        return userService.getUserImageStats(currentUser);
    }

    @GetMapping("/admin-stats")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserStatsDTO> getAllUsersWithStats() {
        return userService.findAllWithStats();
    }
}
