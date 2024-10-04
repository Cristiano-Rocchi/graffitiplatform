package cristianorocchi.graffitiplatform.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import cristianorocchi.graffitiplatform.entities.StreetArt;
import cristianorocchi.graffitiplatform.entities.User;
import cristianorocchi.graffitiplatform.exceptions.BadRequestException;
import cristianorocchi.graffitiplatform.exceptions.NotFoundException;
import cristianorocchi.graffitiplatform.repositories.StreetArtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Year;
import java.util.List;
import java.util.UUID;

@Service
public class StreetArtService {

    @Autowired
    private StreetArtRepository streetArtRepository;

    @Autowired
    private Cloudinary cloudinaryUploader;

    @Autowired
    private UserService userService;

    public List<StreetArt> findAll() {
        return streetArtRepository.findAll();
    }

    public StreetArt findById(UUID id) {
        return streetArtRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Opera di street art non trovata"));
    }

    // Metodo per salvare una nuova opera di street art
    public StreetArt save(StreetArt streetArt, UUID userId) {
        // Recupera l'utente autenticato corrente
        User currentUser = userService.findById(userId);
        streetArt.setUser(currentUser);

        // Verifica l'anno di creazione
        validateAnnoCreazione(streetArt.getAnnoCreazione());

        // Gestione del valore "Sconosciuto" per artista
        if (streetArt.getArtista() == null || streetArt.getArtista().trim().isEmpty()) {
            streetArt.setArtista("Sconosciuto");
        }

        // Se l'anno di creazione non è specificato, impostalo come "Sconosciuto"
        if (streetArt.getAnnoCreazione() == 0) {
            streetArt.setAnnoCreazione(0); // Imposta 0 come "Sconosciuto" per un anno non specificato
        }

        // Salva e restituisce l'oggetto street art
        return streetArtRepository.save(streetArt);
    }

    // Metodo per aggiornare una street art esistente
    public StreetArt update(UUID id, StreetArt updatedStreetArt) {
        StreetArt existingStreetArt = streetArtRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Opera di street art non trovata"));

        // Verifica l'anno di creazione
        validateAnnoCreazione(updatedStreetArt.getAnnoCreazione());

        // Aggiorna i campi
        existingStreetArt.setArtista(updatedStreetArt.getArtista());
        existingStreetArt.setLuogo(updatedStreetArt.getLuogo());
        existingStreetArt.setImmagineUrl(updatedStreetArt.getImmagineUrl());
        existingStreetArt.setStato(updatedStreetArt.getStato());
        existingStreetArt.setAnnoCreazione(updatedStreetArt.getAnnoCreazione());

        return streetArtRepository.save(existingStreetArt);
    }

    // Metodo per verificare se l'utente è il proprietario della street art
    public boolean isStreetArtOwner(UUID streetArtId, UUID userId) {
        StreetArt streetArt = streetArtRepository.findById(streetArtId)
                .orElseThrow(() -> new NotFoundException("Street Art non trovata"));

        return streetArt.getUser().getId().equals(userId);
    }

    // Metodo per eliminare una street art per ID
    public void deleteById(UUID id) {
        streetArtRepository.deleteById(id);
    }

    // Upload immagine per una street art
    public StreetArt uploadImage(UUID streetArtId, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new BadRequestException("Immagine obbligatoria.");
        }

        String url;
        try {
            url = (String) cloudinaryUploader.uploader().upload(file.getBytes(), ObjectUtils.emptyMap()).get("url");
        } catch (IOException e) {
            throw new IOException("Errore durante il caricamento dell'immagine", e);
        }

        StreetArt streetArt = findById(streetArtId);
        streetArt.setImmagineUrl(url);

        return streetArtRepository.save(streetArt);
    }

    // Ricerca per nome dell'artista
    public List<StreetArt> searchByArtista(String artista) {
        return streetArtRepository.findByArtista(artista);
    }

    // Ricerca per anno di creazione
    public List<StreetArt> searchByAnnoCreazione(int annoCreazione) {
        return streetArtRepository.findByAnnoCreazione(annoCreazione);
    }

    // Metodo per validare l'anno di creazione
    private void validateAnnoCreazione(int annoCreazione) {
        int currentYear = Year.now().getValue();
        if (annoCreazione > currentYear) {
            throw new BadRequestException("L'anno di creazione non può essere successivo all'anno corrente.");
        }
    }
}
