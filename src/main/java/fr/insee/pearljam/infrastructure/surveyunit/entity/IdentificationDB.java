package fr.insee.pearljam.infrastructure.surveyunit.entity;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.domain.surveyunit.model.Identification;
import fr.insee.pearljam.domain.surveyunit.model.question.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "identification")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class IdentificationDB implements Serializable {

    private static final long serialVersionUID = 1987L;

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

    @Column
    @Enumerated(EnumType.STRING)
    IndividualStatusQuestionValue individualStatus;

    @Column
    @Enumerated(EnumType.STRING)
    InterviewerCanProcessQuestionValue interviewerCanProcess;

    @Column
    @Enumerated(EnumType.STRING)
    NumberOfRespondentsQuestionValue numberOfRespondents;

    @Column
    @Enumerated(EnumType.STRING)
    PresentInPreviousHomeQuestionValue presentInPreviousHome;

    @Column
    @Enumerated(EnumType.STRING)
    HouseholdCompositionQuestionValue householdComposition;


    /**
     * The SurveyUnit associated to Identification
     */
    @OneToOne
    private SurveyUnit surveyUnit;

    public IdentificationDB(Identification identification, SurveyUnit su) {
        this.surveyUnit = su;
        if (identification != null) {
            this.identification = identification.identification();
            this.access = identification.access();
            this.situation = identification.situation();
            this.category = identification.category();
            this.occupant = identification.occupant();
            this.individualStatus = identification.individualStatus();
            this.interviewerCanProcess = identification.interviewerCanProcess();
            this.numberOfRespondents = identification.numberOfRespondents();
            this.presentInPreviousHome = identification.presentInPreviousHome();
            this.householdComposition = identification.householdComposition();
        }
    }

    /**
     * return model of the db entity
     *
     * @param identificationDB identification DB entity
     * @return the model object of the db entity
     */
    public static Identification toModel(IdentificationDB identificationDB) {
        if (identificationDB == null) {
            return null;
        }
        return new Identification(
                identificationDB.getIdentification(),
                identificationDB.getAccess(),
                identificationDB.getSituation(),
                identificationDB.getCategory(),
                identificationDB.getOccupant(),
                identificationDB.getIndividualStatus(),
                identificationDB.getInterviewerCanProcess(),
                identificationDB.getNumberOfRespondents(),
                identificationDB.getPresentInPreviousHome(),
                identificationDB.getHouseholdComposition()
        );
    }


    /**
     * update the db entity from the model object
     *
     * @param identification model object
     */
    public void update(Identification identification) {
        if (identification == null) {
            return;
        }

        this.setIdentification(identification.identification());
        this.setAccess(identification.access());
        this.setSituation(identification.situation());
        this.setCategory(identification.category());
        this.setOccupant(identification.occupant());
        this.setIndividualStatus(identification.individualStatus());
        this.setInterviewerCanProcess(identification.interviewerCanProcess());
        this.setNumberOfRespondents(identification.numberOfRespondents());
        this.setPresentInPreviousHome(identification.presentInPreviousHome());
        this.setHouseholdComposition(identification.householdComposition());
    }
}
