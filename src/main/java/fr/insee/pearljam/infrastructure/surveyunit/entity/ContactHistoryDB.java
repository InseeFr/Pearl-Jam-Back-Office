package fr.insee.pearljam.infrastructure.surveyunit.entity;

import fr.insee.pearljam.api.domain.ContactOutcomeType;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.domain.surveyunit.model.person.ContactHistory;
import fr.insee.pearljam.domain.surveyunit.model.person.ContactHistoryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Entity(name = "contact_history")
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContactHistoryDB implements Serializable {

    @Serial
    private static final long serialVersionUID = 6363481673399032153L;

    @ManyToOne
    private SurveyUnit surveyUnit;

    @Enumerated(EnumType.STRING)
    @Column(length = 11)
    private ContactHistoryType contactHistoryType;

    @Column(length = 999)
    private String comment;

    @Enumerated(EnumType.STRING)
    private ContactOutcomeType contactOutcomeValue;

    public static ContactHistoryDB fromModel(ContactHistory contactHistory, SurveyUnit surveyUnit) {
        return new ContactHistoryDB(surveyUnit, contactHistory.historyType(), contactHistory.comment(), contactHistory.contactOutcomeValue());
    }

    public static ContactHistory toModel(ContactHistoryDB contactHistoryDB) {
        return new ContactHistory(contactHistoryDB.getContactHistoryType(), contactHistoryDB.getComment(), contactHistoryDB.getContactOutcomeValue(), contactHistoryDB.getSurveyUnit().getId());
    }


}
