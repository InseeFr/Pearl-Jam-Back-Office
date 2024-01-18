package fr.insee.pearljam.api.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import fr.insee.pearljam.api.domain.IdentificationQuestions.AccessQuestionValue;
import fr.insee.pearljam.api.domain.IdentificationQuestions.CategoryQuestionValue;
import fr.insee.pearljam.api.domain.IdentificationQuestions.IdentificationQuestionValue;
import fr.insee.pearljam.api.domain.IdentificationQuestions.OccupantQuestionValue;
import fr.insee.pearljam.api.domain.IdentificationQuestions.SituationQuestionValue;
import fr.insee.pearljam.api.dto.identification.IdentificationDto;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Identification implements Serializable {

    private static final long serialVersionUID = 1987l;

    /**
     * Identification id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Enumerated(EnumType.STRING)
    private IdentificationQuestionValue identification;

    @Column
    @Enumerated(EnumType.STRING)
    private AccessQuestionValue access;

    @Column
    @Enumerated(EnumType.STRING)
    private SituationQuestionValue situation;

    @Column
    @Enumerated(EnumType.STRING)
    private CategoryQuestionValue category;

    @Column
    @Enumerated(EnumType.STRING)
    private OccupantQuestionValue occupant;

    /**
     * The SurveyUnit associated to Identification
     */
    @OneToOne
    private SurveyUnit surveyUnit;

    public Identification(IdentificationDto identificationDto, SurveyUnit su) {
        this.surveyUnit = su;
        if (identificationDto != null) {
            this.identification = identificationDto.getIdentification();
            this.access = identificationDto.getAccess();
            this.situation = identificationDto.getSituation();
            this.category = identificationDto.getCategory();
            this.occupant = identificationDto.getOccupant();
        }
    }

}
