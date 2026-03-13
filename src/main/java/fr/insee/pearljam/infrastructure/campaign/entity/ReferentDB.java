package fr.insee.pearljam.infrastructure.campaign.entity;

import java.io.Serial;
import java.io.Serializable;

import fr.insee.pearljam.domain.campaign.model.Referent;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entity for the referent table in DB.
 */
@Entity(name = "Referent")
@Table(name = "referent")
@Getter
@Setter
@NoArgsConstructor
public class ReferentDB implements Serializable {

    @Serial
    private static final long serialVersionUID = 1987L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String firstName;

    @Column(length = 255)
    private String lastName;

    @Column(length = 255)
    private String phoneNumber;

    @Column(length = 50)
    private String role;

    @ManyToOne(fetch = FetchType.LAZY)
    private CampaignDB campaign;

    public Referent toModel() {
        return new Referent(firstName, lastName, phoneNumber, role);
    }

    public static ReferentDB fromModel(Referent referent, CampaignDB campaign) {
        ReferentDB db = new ReferentDB();
        db.setFirstName(referent.firstName());
        db.setLastName(referent.lastName());
        db.setPhoneNumber(referent.phoneNumber());
        db.setRole(referent.role());
        db.setCampaign(campaign);
        return db;
    }
}
