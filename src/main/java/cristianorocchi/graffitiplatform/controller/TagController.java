package cristianorocchi.graffitiplatform.controller;

import cristianorocchi.graffitiplatform.entities.Tag;
import cristianorocchi.graffitiplatform.entities.User;
import cristianorocchi.graffitiplatform.exceptions.BadRequestException;
import cristianorocchi.graffitiplatform.payloads.GraffitoRespDTO;
import cristianorocchi.graffitiplatform.payloads.NewTagDTO;
import cristianorocchi.graffitiplatform.payloads.TagRespDTO;
import cristianorocchi.graffitiplatform.services.TagService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping
    public List<TagRespDTO> getAllGraffiti() {
        return tagService.findAllGraffitiWithUserDetails();
    }

    @GetMapping("/{id}")
    public Tag getTagById(@PathVariable UUID id) {
        return tagService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Tag createTag(@Valid  @RequestBody NewTagDTO newTagDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Tag tag = new Tag();
        tag.setLuogo(newTagDTO.getLuogo());
        tag.setImmagineUrl(newTagDTO.getImmagineUrl());
        tag.setStato(newTagDTO.getStato());
        tag.setArtista(newTagDTO.getArtista());
        tag.setAnnoCreazione(newTagDTO.getAnnoCreazione());
        tag.setUser(currentUser);

        // Passa anche l'ID dell'utente
        return tagService.save(tag, currentUser.getId());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @tagService.isTagOwner(#id, authentication.principal.id)")
    public Tag updateTag(@PathVariable UUID id, @RequestBody Tag updatedTag) {
        return tagService.update(id, updatedTag);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @tagService.isTagOwner(#id, authentication.principal.id)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTag(@PathVariable UUID id) {
        tagService.deleteById(id);
    }

    // Carica immagine per il tag
    @PostMapping("/{id}/img")
    @PreAuthorize("hasRole('ADMIN') or @tagService.isTagOwner(#id, authentication.principal.id)")
    @ResponseStatus(HttpStatus.OK)
    public Tag uploadTagImage(@PathVariable UUID id, @RequestParam("img") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new BadRequestException("Il file dell'immagine è obbligatorio.");
        }
        return tagService.uploadImage(id, file);
    }

    // Ricerca per artista
    @GetMapping("/search/artista")
    public List<Tag> searchByArtista(@RequestParam("artista") String artista) {
        return tagService.searchByArtista(artista);
    }

    // Ricerca per anno di creazione
    @GetMapping("/search/anno")
    public List<Tag> searchByAnnoCreazione(@RequestParam("annoCreazione") String annoCreazione) {
        try {
            int anno = Integer.parseInt(annoCreazione);
            return tagService.searchByAnnoCreazione(anno);
        } catch (NumberFormatException e) {
            throw new BadRequestException("L'anno di creazione deve essere un numero valido.");
        }
    }
    @GetMapping("/user-images")
    public List<Tag> getUserTagImages(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return tagService.getImagesByUser(currentUser); // Usa il servizio specifico per Tag
    }

    @GetMapping("/random")
    public List<Tag> getRandomTags() {
        return tagService.findRandomTags(12);
    }
}
