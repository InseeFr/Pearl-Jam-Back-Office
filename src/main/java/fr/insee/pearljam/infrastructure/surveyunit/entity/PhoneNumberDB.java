package fr.insee.pearljam.infrastructure.surveyunit.entity;

import fr.insee.pearljam.api.domain.Source;
import fr.insee.pearljam.domain.surveyunit.model.person.PhoneNumber;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Entity PhoneNumber : represent the entity table in DB
 *
 * @author Corcaud Samuel
 */
@Entity
@Table(name = "phone_number")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhoneNumberDB implements Serializable {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Source source;
    private boolean favorite;
    private String number;

    /**
     * Person associated to the phone number
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private PersonDB person;


    public static Set<PhoneNumber> toModel(Set<PhoneNumberDB> phoneNumbers) {
        return phoneNumbers.stream().map(phoneNumber -> new PhoneNumber(phoneNumber.source, phoneNumber.isFavorite(), phoneNumber.getNumber())).collect(Collectors.toSet());

    }

    public static PhoneNumber toModel(PhoneNumberDB phoneNumber) {
        return  new PhoneNumber(phoneNumber.source, phoneNumber.isFavorite(), phoneNumber.getNumber());
    }

    public static List<PhoneNumberDB> fromModel(List<PhoneNumber> phoneNumbers, PersonDB person) {
        return phoneNumbers.stream().map(phoneNumber -> new PhoneNumberDB(null,phoneNumber.source(),phoneNumber.favorite(), phoneNumber.number(),person)).toList();
    }

    public static PhoneNumberDB fromModel(PhoneNumber phoneNumber, PersonDB person) {
        return  new PhoneNumberDB(null,phoneNumber.source(),phoneNumber.favorite(), phoneNumber.number(),person);
    }

}
