package cristianorocchi.graffitiplatform.repositories;

import cristianorocchi.graffitiplatform.entities.Graffito;
import cristianorocchi.graffitiplatform.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GraffitoRepository extends JpaRepository<Graffito, UUID> {

    // Filtra per nome dell'artista
    @Query("SELECT g FROM Graffito g WHERE LOWER(g.artista) LIKE LOWER(CONCAT('%', :artista, '%'))")
    List<Graffito> findByArtista(@Param("artista") String artista);

    // Filtra per anno di creazione come int
    @Query("SELECT g FROM Graffito g WHERE g.annoCreazione = :annoCreazione")
    List<Graffito> findByAnnoCreazione(@Param("annoCreazione") int annoCreazione);

    // Filtra i graffiti per utente
    List<Graffito> findByUser(User user);

    // Conta i graffiti caricati dall'utente
    @Query("SELECT COUNT(g) FROM Graffito g WHERE g.user = :user")
    long countByUser(@Param("user") User user);

}
