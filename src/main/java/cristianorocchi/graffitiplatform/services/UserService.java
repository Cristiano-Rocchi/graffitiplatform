package cristianorocchi.graffitiplatform.services;

import cristianorocchi.graffitiplatform.entities.User;
import cristianorocchi.graffitiplatform.exceptions.BadRequestException;
import cristianorocchi.graffitiplatform.exceptions.NotFoundException;
import cristianorocchi.graffitiplatform.payloads.UserStatsDTO;
import cristianorocchi.graffitiplatform.repositories.GraffitoRepository;
import cristianorocchi.graffitiplatform.repositories.StreetArtRepository;
import cristianorocchi.graffitiplatform.repositories.TagRepository;
import cristianorocchi.graffitiplatform.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private GraffitoRepository graffitoRepository;
    @Autowired
    private StreetArtRepository streetArtRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Trova tutti gli utenti
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // Trova tutti gli utenti con paginazione
    public Page<User> findAllPageable(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    // Salva un nuovo utente
    public User save(User user) {
        userRepository.findByUsernameOrEmail(user.getUsername(), user.getEmail()).ifPresent(
                u -> {
                    throw new BadRequestException("Lo username o l'email è già in uso!");
                }
        );

        // Crittografa la password prima di salvarla
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Salva l'utente nel database
        return userRepository.save(user);
    }

    // Trova un utente per ID
    public User findById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utente " + userId + " non trovato"));
    }

    // Cancella un utente per ID
    public void delete(UUID userId) {
        User user = findById(userId);
        userRepository.delete(user);
    }

    // Aggiorna un utente esistente
    public User updateUser(UUID userId, User updatedData) {
        User user = findById(userId);

        // Aggiorna i campi dell'utente
        user.setUsername(updatedData.getUsername());
        user.setEmail(updatedData.getEmail());

        if (updatedData.getPassword() != null && !updatedData.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updatedData.getPassword()));
        }

        return userRepository.save(user);
    }

    // Trova un utente per username o email
    public User findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new NotFoundException("Utente con username/email " + usernameOrEmail + " non trovato"));
    }

    // Metodo per ottenere l'utente attualmente autenticato
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    // Aggiorna l'utente attualmente autenticato
    public User updateCurrentUser(User updatedData) {
        User currentUser = getCurrentUser();

        // Aggiorna i campi dell'utente
        currentUser.setUsername(updatedData.getUsername());
        currentUser.setEmail(updatedData.getEmail());

        if (updatedData.getPassword() != null && !updatedData.getPassword().isEmpty()) {
            currentUser.setPassword(passwordEncoder.encode(updatedData.getPassword()));
        }

        return userRepository.save(currentUser);
    }

    // Ottieni statistiche di immagini per tutti gli utenti
    public List<UserStatsDTO> findAllWithStats() {
        return userRepository.findAll().stream()
                .map(user -> {
                    long graffitiCount = graffitoRepository.countByUser(user);
                    long streetArtCount = streetArtRepository.countByUser(user);
                    long tagCount = tagRepository.countByUser(user);

                    return new UserStatsDTO(
                            user.getId(),
                            user.getUsername(),
                            user.getEmail(),
                            graffitiCount,
                            streetArtCount,
                            tagCount,
                            graffitiCount + streetArtCount + tagCount
                    );
                })
                .collect(Collectors.toList());
    }





    public Map<String, Long> getUserImageStats(User user) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("graffitiCount", graffitoRepository.countByUser(user));
        stats.put("streetArtCount", streetArtRepository.countByUser(user));
        stats.put("tagCount", tagRepository.countByUser(user));
        return stats;
    }
}
