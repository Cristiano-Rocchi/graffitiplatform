package cristianorocchi.graffitiplatform.repositories;


import cristianorocchi.graffitiplatform.entities.Graffito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface GraffitoRepository extends JpaRepository<Graffito, UUID> {
}

