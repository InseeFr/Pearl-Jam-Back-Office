package fr.insee.pearljam.infrastructure.persistence.campaign.entity;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "referent", schema = "public")
@Getter
@Setter
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

    /**
     * The Campaign of Referent
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private CampaignDB campaign;

}
