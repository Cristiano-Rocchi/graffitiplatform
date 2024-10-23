package cristianorocchi.graffitiplatform.controller;

import cristianorocchi.graffitiplatform.entities.StreetArt;
import cristianorocchi.graffitiplatform.entities.User;
import cristianorocchi.graffitiplatform.exceptions.BadRequestException;
import cristianorocchi.graffitiplatform.services.StreetArtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/streetart")
public class StreetArtController {

    @Autowired
    private StreetArtService streetArtService;

    @GetMapping
    public List<StreetArt> getAllStreetArt() {
        return streetArtService.findAll();
    }

    @GetMapping("/{id}")
    public StreetArt getStreetArtById(@PathVariable UUID id) {
        return streetArtService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StreetArt createStreetArt(@RequestBody StreetArt streetArt) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // Associa l'utente corrente all'opera di street art
        streetArt.setUser(currentUser);

        // Passa anche l'ID dell'utente
        return streetArtService.save(streetArt, currentUser.getId());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @streetArtService.isStreetArtOwner(#id, authentication.principal.id)")
    public StreetArt updateStreetArt(@PathVariable UUID id, @RequestBody StreetArt updatedStreetArt) {
        return streetArtService.update(id, updatedStreetArt);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @streetArtService.isStreetArtOwner(#id, authentication.principal.id)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStreetArt(@PathVariable UUID id) {
        streetArtService.deleteById(id);
    }

    // Carica immagine per street art
    @PostMapping("/{id}/img")
    @PreAuthorize("hasRole('ADMIN') or @streetArtService.isStreetArtOwner(#id, authentication.principal.id)")
    @ResponseStatus(HttpStatus.OK)
    public StreetArt uploadStreetArtImage(@PathVariable UUID id, @RequestParam("img") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new BadRequestException("Il file dell'immagine è obbligatorio.");
        }
        return streetArtService.uploadImage(id, file);
    }

    // Ricerca per artista
    @GetMapping("/search/artista")
    public List<StreetArt> searchByArtista(@RequestParam("artista") String artista) {
        return streetArtService.searchByArtista(artista);
    }

    // Ricerca per anno di creazione
    @GetMapping("/search/anno")
    public List<StreetArt> searchByAnnoCreazione(@RequestParam("annoCreazione") String annoCreazione) {
        try {
            int anno = Integer.parseInt(annoCreazione);
            return streetArtService.searchByAnnoCreazione(anno);
        } catch (NumberFormatException e) {
            throw new BadRequestException("L'anno di creazione deve essere un numero valido.");
        }
    }
    @GetMapping("/user-images")
    public List<StreetArt> getUserStreetArtImages(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return streetArtService.getImagesByUser(currentUser); // Usa il servizio specifico per StreetArt
    }

    @GetMapping("/random")
    public List<StreetArt> getRandomStreetArt() {
        return streetArtService.findRandomStreetArt(12);
    }
}
