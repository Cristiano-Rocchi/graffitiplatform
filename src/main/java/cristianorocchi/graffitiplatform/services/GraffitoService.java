package cristianorocchi.graffitiplatform.services;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import cristianorocchi.graffitiplatform.entities.Graffito;
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

    public List<Graffito> findAll() {
        return graffitoRepository.findAll();
    }

    public Graffito findById(UUID id) {
        return graffitoRepository.findById(id).orElseThrow(() -> new NotFoundException("Graffito non trovato"));
    }

    public Graffito save(Graffito graffito) {
        return graffitoRepository.save(graffito);
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
