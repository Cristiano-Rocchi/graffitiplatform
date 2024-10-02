package cristianorocchi.graffitiplatform.controller;


import cristianorocchi.graffitiplatform.entities.Graffito;
import cristianorocchi.graffitiplatform.entities.User;
import cristianorocchi.graffitiplatform.exceptions.BadRequestException;
import cristianorocchi.graffitiplatform.services.GraffitoService;
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
@RequestMapping("/api/graffiti")
public class GraffitoController {

    @Autowired
    private GraffitoService graffitoService;

    @GetMapping
    public List<Graffito> getAllGraffiti() {
        return graffitoService.findAll();
    }

    @GetMapping("/{id}")
    public Graffito getGraffitoById(@PathVariable UUID id) {
        return graffitoService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Graffito createGraffito(@RequestBody Graffito graffito, @AuthenticationPrincipal User user) {
        return graffitoService.save(graffito, user.getId());
    }



    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @graffitoService.isGraffitoOwner(#id, authentication.principal.id)")
    public Graffito updateGraffito(@PathVariable UUID id, @RequestBody Graffito updatedGraffito) {
        return graffitoService.update(id, updatedGraffito);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGraffito(@PathVariable UUID id) {
        graffitoService.deleteById(id);
    }

    // Carica immagine per il graffito
    @PostMapping("/{id}/img")
    @ResponseStatus(HttpStatus.OK)
    public Graffito uploadGraffitoImage(@PathVariable UUID id, @RequestParam("img") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new BadRequestException("Il file dell'immagine è obbligatorio.");
        }
        return graffitoService.uploadImage(id, file);
    }
}

