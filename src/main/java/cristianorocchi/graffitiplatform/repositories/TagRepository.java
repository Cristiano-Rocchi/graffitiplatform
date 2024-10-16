package cristianorocchi.graffitiplatform.repositories;

import cristianorocchi.graffitiplatform.entities.Tag;
import cristianorocchi.graffitiplatform.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {

    // Filtra per nome dell'artista
    @Query("SELECT t FROM Tag t WHERE LOWER(t.artista) LIKE LOWER(CONCAT('%', :artista, '%'))")
    List<Tag> findByArtista(@Param("artista") String artista);

    // Filtra per anno di creazione come int
    @Query("SELECT t FROM Tag t WHERE t.annoCreazione = :annoCreazione")
    List<Tag> findByAnnoCreazione(@Param("annoCreazione") int annoCreazione);

    List<Tag> findByUser(User user); // Filtra i tag per utente
}
