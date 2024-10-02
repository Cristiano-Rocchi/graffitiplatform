package cristianorocchi.graffitiplatform.entities;


import cristianorocchi.graffitiplatform.enums.StatoOpera;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "street_art")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StreetArt {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column
    private String artista;

    @Column(nullable = false)
    private String luogo;

    @Column(nullable = false)
    private String immagineUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatoOpera stato;

    @Column
    private String annoCreazione;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
