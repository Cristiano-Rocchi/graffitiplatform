package cristianorocchi.graffitiplatform.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import cristianorocchi.graffitiplatform.entities.Graffito;
import cristianorocchi.graffitiplatform.entities.User;
import cristianorocchi.graffitiplatform.enums.Ruolo;
import cristianorocchi.graffitiplatform.exceptions.BadRequestException;
import cristianorocchi.graffitiplatform.exceptions.NotFoundException;
import cristianorocchi.graffitiplatform.payloads.GraffitoRespDTO;
import cristianorocchi.graffitiplatform.repositories.GraffitoRepository;
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
public class GraffitoService {

    @Autowired
    private GraffitoRepository graffitoRepository;

    @Autowired
    private Cloudinary cloudinaryUploader;

    @Autowired
    private UserService userService;

    // Cache delle immagini random
    private List<Graffito> cachedRandomImages;

    // Data dell'ultimo aggiornamento della cache
    private LocalDate lastUpdate;

    public List<Graffito> findAll() {
        return graffitoRepository.findAll();
    }

    public Graffito findById(UUID id) {
        return graffitoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Graffito non trovato"));
    }

    // Metodo per salvare un nuovo graffito
    public Graffito save(Graffito graffito, UUID userId) {
        // Recupera l'utente autenticato corrente
        User currentUser = userService.findById(userId);
        graffito.setUser(currentUser);

        // Verifica l'anno di creazione
        validateAnnoCreazione(graffito.getAnnoCreazione());

        // Gestione del valore "Sconosciuto" per artista
        if (graffito.getArtista() == null || graffito.getArtista().trim().isEmpty()) {
            graffito.setArtista("Sconosciuto");
        }

        // Se l'anno di creazione non è specificato, impostalo come "Sconosciuto"
        if (graffito.getAnnoCreazione() == 0) {
            graffito.setAnnoCreazione(0); // Imposta 0 come "Sconosciuto" per un anno non specificato
        }

        // Salva e restituisce l'oggetto graffito
        return graffitoRepository.save(graffito);
    }

    // Metodo per aggiornare un graffito esistente
    public Graffito update(UUID id, Graffito updatedGraffito) {
        Graffito existingGraffito = graffitoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Graffito non trovato"));

        // Verifica l'anno di creazione
        validateAnnoCreazione(updatedGraffito.getAnnoCreazione());

        // Aggiorna i campi
        existingGraffito.setArtista(updatedGraffito.getArtista());
        existingGraffito.setLuogo(updatedGraffito.getLuogo());
        existingGraffito.setImmagineUrl(updatedGraffito.getImmagineUrl());
        existingGraffito.setStato(updatedGraffito.getStato());
        existingGraffito.setAnnoCreazione(updatedGraffito.getAnnoCreazione());

        return graffitoRepository.save(existingGraffito);
    }



    public List<GraffitoRespDTO> findAllWithUserDetails() {
        return graffitoRepository.findAll().stream()
                .map(graffito -> new GraffitoRespDTO(
                        graffito.getId(),
                        graffito.getArtista(),
                        graffito.getLuogo(),
                        graffito.getImmagineUrl(),
                        graffito.getStato(),
                        graffito.getAnnoCreazione(),
                        graffito.getUser().getUsername() // Nome dell'utente associato
                ))
                .collect(Collectors.toList());
    }


    // Metodo per verificare se l'utente è il proprietario del graffito
    public boolean isGraffitoOwner(UUID graffitoId, UUID userId) {
        Graffito graffito = graffitoRepository.findById(graffitoId)
                .orElseThrow(() -> new NotFoundException("Graffito non trovato"));

        return graffito.getUser().getId().equals(userId);
    }

    // Metodo per eliminare un graffito per ID
    public void deleteById(UUID id, UUID requesterId) {
        Graffito graffito = graffitoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Graffito non trovato"));

        User requester = userService.findById(requesterId);

        // Controlla se il richiedente è un admin o il proprietario del graffito
        if (requester.getRuolo() == Ruolo.ADMIN || isGraffitoOwner(id, requesterId)) {
            graffitoRepository.deleteById(id);
        } else {
            throw new BadRequestException("Non hai il permesso per eliminare questo graffito.");
        }


}


    // Upload immagine per un graffito
    public Graffito uploadImage(UUID graffitoId, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new BadRequestException("Immagine obbligatoria.");
        }

        String url;
        try {
            url = (String) cloudinaryUploader.uploader().upload(file.getBytes(), ObjectUtils.emptyMap()).get("url");
        } catch (IOException e) {
            throw new IOException("Errore durante il caricamento dell'immagine", e);
        }

        Graffito graffito = findById(graffitoId);
        graffito.setImmagineUrl(url);

        return graffitoRepository.save(graffito);
    }

    // Ricerca per nome dell'artista
    public List<Graffito> searchByArtista(String artista) {
        return graffitoRepository.findByArtista(artista);
    }

    // Ricerca per anno di creazione
    public List<Graffito> searchByAnnoCreazione(int annoCreazione) {
        return graffitoRepository.findByAnnoCreazione(annoCreazione);
    }

    // Metodo per validare l'anno di creazione
    private void validateAnnoCreazione(int annoCreazione) {
        int currentYear = Year.now().getValue();
        if (annoCreazione > currentYear) {
            throw new BadRequestException("L'anno di creazione non può essere successivo all'anno corrente.");
        }
    }
    public List<Graffito> getImagesByUser(User user) {
        return graffitoRepository.findByUser(user);
    }


    // Metodo per generare 12 immagini casuali che cambiano mensilmente
    public List<Graffito> findRandomGraffiti(int limit) {
        if (cachedRandomImages == null || shouldUpdateCache()) {
            // Se la cache è vuota o se è passato un mese dall'ultimo aggiornamento, ricarica
            List<Graffito> allGraffiti = graffitoRepository.findAll();
            Collections.shuffle(allGraffiti);  // Mescola le immagini
            cachedRandomImages = allGraffiti.stream().limit(limit).collect(Collectors.toList());
            lastUpdate = LocalDate.now();  // Aggiorna la data di aggiornamento
        }
        return cachedRandomImages;
    }

    // Metodo per verificare se è passato un mese dall'ultimo aggiornamento della cache
    private boolean shouldUpdateCache() {
        return lastUpdate == null || lastUpdate.plusMonths(1).isBefore(LocalDate.now());
    }
}
