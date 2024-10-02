package cristianorocchi.graffitiplatform.services;



import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import cristianorocchi.graffitiplatform.entities.Tag;
import cristianorocchi.graffitiplatform.exceptions.BadRequestException;
import cristianorocchi.graffitiplatform.exceptions.NotFoundException;
import cristianorocchi.graffitiplatform.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private Cloudinary cloudinaryUploader;

    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    public Tag findById(UUID id) {
        return tagRepository.findById(id).orElseThrow(() -> new NotFoundException("Tag non trovato"));
    }


    //se vuoto è sconosciuto(Artista,annocreazione)
    public Tag save(Tag tag) {

        if (tag.getArtista() == null || tag.getArtista().trim().isEmpty()) {
            tag.setArtista("Sconosciuto");
        }


        if (tag.getAnnoCreazione() == null || tag.getAnnoCreazione().trim().isEmpty()) {
            tag.setAnnoCreazione("Sconosciuto");
        }

        return tagRepository.save(tag);
    }

    public void deleteById(UUID id) {
        tagRepository.deleteById(id);
    }

    // Upload immagine per il tag
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
}

