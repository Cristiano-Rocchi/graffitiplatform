package cristianorocchi.graffitiplatform.controller;

import cristianorocchi.graffitiplatform.entities.Tag;
import cristianorocchi.graffitiplatform.exceptions.BadRequestException;
import cristianorocchi.graffitiplatform.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public List<Tag> getAllTags() {
        return tagService.findAll();
    }

    @GetMapping("/{id}")
    public Tag getTagById(@PathVariable UUID id) {
        return tagService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Tag createTag(@RequestBody Tag tag) {
        return tagService.save(tag);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTag(@PathVariable UUID id) {
        tagService.deleteById(id);
    }

    // Carica immagine per il tag
    @PostMapping("/{id}/img")
    @ResponseStatus(HttpStatus.OK)
    public Tag uploadTagImage(@PathVariable UUID id, @RequestParam("img") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new BadRequestException("Il file dell'immagine è obbligatorio.");
        }
        return tagService.uploadImage(id, file);
    }
}
