package cristianorocchi.graffitiplatform.controller;


import cristianorocchi.graffitiplatform.entities.StreetArt;
import cristianorocchi.graffitiplatform.entities.User;
import cristianorocchi.graffitiplatform.exceptions.BadRequestException;
import cristianorocchi.graffitiplatform.services.StreetArtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public StreetArt createStreetArt(@RequestBody StreetArt streetArt, @AuthenticationPrincipal User user) {
        return streetArtService.save(streetArt, user.getId());
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @streetArtService.isStreetArtOwner(#id, authentication.principal.id)")
    public StreetArt updateStreetArt(@PathVariable UUID id, @RequestBody StreetArt updatedStreetArt) {
        return streetArtService.update(id, updatedStreetArt);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStreetArt(@PathVariable UUID id) {
        streetArtService.deleteById(id);
    }

    // Carica immagine per street art
    @PostMapping("/{id}/img")
    @ResponseStatus(HttpStatus.OK)
    public StreetArt uploadStreetArtImage(@PathVariable UUID id, @RequestParam("img") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new BadRequestException("Il file dell'immagine è obbligatorio.");
        }
        return streetArtService.uploadImage(id, file);
    }
}
