package cristianorocchi.graffitiplatform.services;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import cristianorocchi.graffitiplatform.entities.Graffito;
import cristianorocchi.graffitiplatform.entities.User;
import cristianorocchi.graffitiplatform.exceptions.BadRequestException;
import cristianorocchi.graffitiplatform.exceptions.NotFoundException;
import cristianorocchi.graffitiplatform.repositories.GraffitoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class GraffitoService {

    @Autowired
    private GraffitoRepository graffitoRepository;

    @Autowired
    private Cloudinary cloudinaryUploader;
    @Autowired UserService userService;

    public List<Graffito> findAll() {
        return graffitoRepository.findAll();
    }

    public Graffito findById(UUID id) {
        return graffitoRepository.findById(id).orElseThrow(() -> new NotFoundException("Graffito non trovato"));
    }


    //se vuoto è sconosciuto(Artista,annocreazione)
    public Graffito save(Graffito graffito, UUID userId) {
        // Recupera l'utente autenticato corrente
        User currentUser = userService.findById(userId);
        graffito.setUser(currentUser);

        // Gestione del valore "Sconosciuto" per artista e anno di creazione
        if (graffito.getArtista() == null || graffito.getArtista().trim().isEmpty()) {
            graffito.setArtista("Sconosciuto");
        }
        if (graffito.getAnnoCreazione() == null || graffito.getAnnoCreazione().trim().isEmpty()) {
            graffito.setAnnoCreazione("Sconosciuto");
        }

        return graffitoRepository.save(graffito);
    }



    public Graffito update(UUID id, Graffito updatedGraffito) {
        Graffito existingGraffito = graffitoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Graffito non trovato"));

        // Aggiorna i campi
        existingGraffito.setArtista(updatedGraffito.getArtista());
        existingGraffito.setLuogo(updatedGraffito.getLuogo());
        existingGraffito.setImmagineUrl(updatedGraffito.getImmagineUrl());
        existingGraffito.setStato(updatedGraffito.getStato());
        existingGraffito.setAnnoCreazione(updatedGraffito.getAnnoCreazione());

        return graffitoRepository.save(existingGraffito);
    }

    // Metodo per verificare se l'utente è il proprietario del graffito
    public boolean isGraffitoOwner(UUID graffitoId, UUID userId) {
        Graffito graffito = graffitoRepository.findById(graffitoId)
                .orElseThrow(() -> new NotFoundException("Graffito non trovato"));

        return graffito.getUser().getId().equals(userId);
    }

    public void deleteById(UUID id) {
        graffitoRepository.deleteById(id);
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
}
