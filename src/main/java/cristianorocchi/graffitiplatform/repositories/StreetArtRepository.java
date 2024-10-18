package cristianorocchi.graffitiplatform.repositories;

import cristianorocchi.graffitiplatform.entities.StreetArt;
import cristianorocchi.graffitiplatform.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StreetArtRepository extends JpaRepository<StreetArt, UUID> {

    // Filtra per nome dell'artista
    @Query("SELECT s FROM StreetArt s WHERE LOWER(s.artista) LIKE LOWER(CONCAT('%', :artista, '%'))")
    List<StreetArt> findByArtista(@Param("artista") String artista);

    // Filtra per anno di creazione come int
    @Query("SELECT s FROM StreetArt s WHERE s.annoCreazione = :annoCreazione")
    List<StreetArt> findByAnnoCreazione(@Param("annoCreazione") int annoCreazione);
    // Filtra  street art per utente
    List<StreetArt> findByUser(User user);
    // Conta le opere di street art caricate dall'utente
    @Query("SELECT COUNT(s) FROM StreetArt s WHERE s.user = :user")
    long countByUser(@Param("user") User user);

}
