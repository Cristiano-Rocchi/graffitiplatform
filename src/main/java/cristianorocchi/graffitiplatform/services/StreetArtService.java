package cristianorocchi.graffitiplatform.services;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import cristianorocchi.graffitiplatform.entities.StreetArt;
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

    public List<StreetArt> findAll() {
        return streetArtRepository.findAll();
    }

    public StreetArt findById(UUID id) {
        return streetArtRepository.findById(id).orElseThrow(() -> new NotFoundException("Opera di street art non trovata"));
    }

    public StreetArt save(StreetArt streetArt) {
        return streetArtRepository.save(streetArt);
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
