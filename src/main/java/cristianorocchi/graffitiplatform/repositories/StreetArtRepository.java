package cristianorocchi.graffitiplatform.repositories;


import cristianorocchi.graffitiplatform.entities.StreetArt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface StreetArtRepository extends JpaRepository<StreetArt, UUID> {
}
