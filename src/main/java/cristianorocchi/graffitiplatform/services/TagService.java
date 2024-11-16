package cristianorocchi.graffitiplatform.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import cristianorocchi.graffitiplatform.entities.Tag;
import cristianorocchi.graffitiplatform.entities.User;
import cristianorocchi.graffitiplatform.exceptions.BadRequestException;
import cristianorocchi.graffitiplatform.exceptions.NotFoundException;
import cristianorocchi.graffitiplatform.payloads.StreetArtRespDTO;
import cristianorocchi.graffitiplatform.payloads.TagRespDTO;
import cristianorocchi.graffitiplatform.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Year;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private Cloudinary cloudinaryUploader;

    @Autowired
    private UserService userService;


    private List<Tag> cachedRandomImages;
    private LocalDate lastUpdate;


    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    public Tag findById(UUID id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tag non trovato"));
    }

    // Metodo per salvare un nuovo tag
    public Tag save(Tag tag, UUID userId) {
        // Recupera l'utente autenticato corrente
        User currentUser = userService.findById(userId);
        tag.setUser(currentUser);

        // Verifica l'anno di creazione
        validateAnnoCreazione(tag.getAnnoCreazione());

        // Gestione del valore "Sconosciuto" per artista
        if (tag.getArtista() == null || tag.getArtista().trim().isEmpty()) {
            tag.setArtista("Sconosciuto");
        }

        // Se l'anno di creazione non è specificato, impostalo come "Sconosciuto"
        if (tag.getAnnoCreazione() == 0) {
            tag.setAnnoCreazione(0); // Imposta 0 come "Sconosciuto" per un anno non specificato
        }

        // Salva e restituisce l'oggetto tag
        return tagRepository.save(tag);
    }

    // Metodo per aggiornare un tag esistente
    public Tag update(UUID id, Tag updatedTag) {
        Tag existingTag = tagRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tag non trovato"));

        // Verifica l'anno di creazione
        validateAnnoCreazione(updatedTag.getAnnoCreazione());

        // Aggiorna i campi
        existingTag.setArtista(updatedTag.getArtista());
        existingTag.setLuogo(updatedTag.getLuogo());
        existingTag.setImmagineUrl(updatedTag.getImmagineUrl());
        existingTag.setStato(updatedTag.getStato());
        existingTag.setAnnoCreazione(updatedTag.getAnnoCreazione());

        return tagRepository.save(existingTag);
    }

    public List<TagRespDTO> findAllGraffitiWithUserDetails() {
        return tagRepository.findAll().stream()
                .map(Tag -> new TagRespDTO(
                        Tag.getId(),
                        Tag.getArtista(),
                        Tag.getLuogo(),
                        Tag.getImmagineUrl(),
                        Tag.getStato(),
                        Tag.getAnnoCreazione(),
                        Tag.getUser().getUsername() // Aggiunge il nome utente
                ))
                .collect(Collectors.toList());
    }
    // Metodo per verificare se l'utente è il proprietario del tag
    public boolean isTagOwner(UUID tagId, UUID userId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new NotFoundException("Tag non trovato"));

        return tag.getUser().getId().equals(userId);
    }

    // Metodo per eliminare un tag per ID
    public void deleteById(UUID id) {
        tagRepository.deleteById(id);
    }

    // Upload immagine per un tag
    public Tag uploadImage(UUID tagId, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new BadRequestException("Immagine obbligatoria.");
        }

        String url;
        try {
            url = (String) cloudinaryUploader.uploader().upload(file.getBytes(), ObjectUtils.emptyMap()).get("url");
        } catch (IOException e) {
            throw new IOException("Errore durante il caricamento dell'immagine", e);
        }

        Tag tag = findById(tagId);
        tag.setImmagineUrl(url);

        return tagRepository.save(tag);
    }

    // Ricerca per nome dell'artista
    public List<Tag> searchByArtista(String artista) {
        return tagRepository.findByArtista(artista);
    }

    // Ricerca per anno di creazione
    public List<Tag> searchByAnnoCreazione(int annoCreazione) {
        return tagRepository.findByAnnoCreazione(annoCreazione);
    }

    // Metodo per validare l'anno di creazione
    private void validateAnnoCreazione(int annoCreazione) {
        int currentYear = Year.now().getValue();
        if (annoCreazione > currentYear) {
            throw new BadRequestException("L'anno di creazione non può essere successivo all'anno corrente.");
        }
    }
    public List<Tag> getImagesByUser(User user) {
        return tagRepository.findByUser(user);
    }

    public List<Tag> findRandomTags(int limit) {
        if (cachedRandomImages == null || shouldUpdateCache()) {
            List<Tag> allTags = tagRepository.findAll();
            Collections.shuffle(allTags);
            cachedRandomImages = allTags.stream().limit(limit).collect(Collectors.toList());
            lastUpdate = LocalDate.now();
        }
        return cachedRandomImages;
    }

    private boolean shouldUpdateCache() {
        return lastUpdate == null || lastUpdate.plusMonths(1).isBefore(LocalDate.now());
    }
}
