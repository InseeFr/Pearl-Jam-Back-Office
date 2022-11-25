package fr.insee.pearljam.api.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import fr.insee.pearljam.api.domain.IdentificationQuestions.AccessQuestionValue;
import fr.insee.pearljam.api.domain.IdentificationQuestions.CategoryQuestionValue;
import fr.insee.pearljam.api.domain.IdentificationQuestions.IdentificationQuestionValue;
import fr.insee.pearljam.api.domain.IdentificationQuestions.OccupantQuestionValue;
import fr.insee.pearljam.api.domain.IdentificationQuestions.SituationQuestionValue;
import fr.insee.pearljam.api.dto.identification.IdentificationDto;


@Entity
@Table
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

    public Identification() {
    }

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

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IdentificationQuestionValue getIdentification() {
        return this.identification;
    }

    public void setIdentification(IdentificationQuestionValue identification) {
        this.identification = identification;
    }

    public AccessQuestionValue getAccess() {
        return this.access;
    }

    public void setAccess(AccessQuestionValue access) {
        this.access = access;
    }

    public SituationQuestionValue getSituation() {
        return this.situation;
    }

    public void setSituation(SituationQuestionValue situation) {
        this.situation = situation;
    }

    public CategoryQuestionValue getCategory() {
        return this.category;
    }

    public void setCategory(CategoryQuestionValue category) {
        this.category = category;
    }

    public OccupantQuestionValue getOccupant() {
        return this.occupant;
    }

    public void setOccupant(OccupantQuestionValue occupant) {
        this.occupant = occupant;
    }

    public SurveyUnit getSurveyUnit() {
        return this.surveyUnit;
    }

    public void setSurveyUnit(SurveyUnit surveyUnit) {
        this.surveyUnit = surveyUnit;
    }
}
