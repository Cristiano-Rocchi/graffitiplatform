package cristianorocchi.graffitiplatform.services;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import cristianorocchi.graffitiplatform.entities.StreetArt;
import cristianorocchi.graffitiplatform.entities.Tag;
import cristianorocchi.graffitiplatform.entities.User;
import cristianorocchi.graffitiplatform.exceptions.BadRequestException;
import cristianorocchi.graffitiplatform.exceptions.NotFoundException;
import cristianorocchi.graffitiplatform.repositories.StreetArtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class StreetArtService {

    @Autowired
    private StreetArtRepository streetArtRepository;

    @Autowired
    private Cloudinary cloudinaryUploader;
    @Autowired UserService userService;

    public List<StreetArt> findAll() {
        return streetArtRepository.findAll();
    }

    public StreetArt findById(UUID id) {
        return streetArtRepository.findById(id).orElseThrow(() -> new NotFoundException("Opera di street art non trovata"));
    }


    //se vuoto è sconosciuto(Artista,annocreazione)
    public StreetArt save(StreetArt streetArt, UUID userId) {
        // Recupera l'utente autenticato corrente
        User currentUser = userService.findById(userId);
        streetArt.setUser(currentUser);

        // Gestione del valore "Sconosciuto" per artista e anno di creazione
        if (streetArt.getArtista() == null || streetArt.getArtista().trim().isEmpty()) {
            streetArt.setArtista("Sconosciuto");
        }
        if (streetArt.getAnnoCreazione() == null || streetArt.getAnnoCreazione().trim().isEmpty()) {
            streetArt.setAnnoCreazione("Sconosciuto");
        }

        // Salva e restituisce l'oggetto street art
        return streetArtRepository.save(streetArt);
    }



    public boolean isStreetArtOwner(UUID streetArtId, UUID userId) {
        StreetArt streetArt = streetArtRepository.findById(streetArtId)
                .orElseThrow(() -> new NotFoundException("Street Art non trovata"));

        return streetArt.getUser().getId().equals(userId);
    }



    public StreetArt update(UUID id, StreetArt updatedStreetArt) {
        StreetArt existingStreetArt = streetArtRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Opera di street art non trovata"));

        // Aggiorna i campi
        existingStreetArt.setArtista(updatedStreetArt.getArtista());
        existingStreetArt.setLuogo(updatedStreetArt.getLuogo());
        existingStreetArt.setImmagineUrl(updatedStreetArt.getImmagineUrl());
        existingStreetArt.setStato(updatedStreetArt.getStato());
        existingStreetArt.setAnnoCreazione(updatedStreetArt.getAnnoCreazione());

        return streetArtRepository.save(existingStreetArt);
    }



    public void deleteById(UUID id) {
        streetArtRepository.deleteById(id);
    }

    // Upload immagine per street art
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
}
