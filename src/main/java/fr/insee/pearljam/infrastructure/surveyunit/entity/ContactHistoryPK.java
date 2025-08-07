package fr.insee.pearljam.infrastructure.surveyunit.entity;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.domain.surveyunit.model.person.ContactHistoryType;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ContactHistoryPK implements Serializable {

    private SurveyUnit surveyUnit;
    private ContactHistoryType contactHistoryType;

    // Override equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactHistoryPK that = (ContactHistoryPK) o;
        return Objects.equals(surveyUnit, that.surveyUnit) &&
                contactHistoryType == that.contactHistoryType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(surveyUnit, contactHistoryType);
    }
}