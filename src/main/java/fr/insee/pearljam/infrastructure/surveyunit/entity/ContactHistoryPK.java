package fr.insee.pearljam.infrastructure.surveyunit.entity;

import fr.insee.pearljam.domain.surveyunit.model.contacthistory.ContactHistoryType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class ContactHistoryPK implements Serializable {

    @Column(name = "survey_unit_id", length = 50, nullable = false)
    private String surveyUnitId;

    @Enumerated(EnumType.STRING)
    @Column(name = "contact_history_type", length = 10, nullable = false)
    private ContactHistoryType contactHistoryType;

}