package fr.insee.pearljam.api.dto.referent;

import fr.insee.pearljam.api.domain.Referent;
import lombok.Data;

/**
 * ReferentDto
 */
@Data
public class ReferentDto {

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String role;

    public ReferentDto() {
    }

    public ReferentDto(String firstName, String lastName, String phoneNumber, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public ReferentDto(Referent ref) {
        this.firstName = ref.getFirstName();
        this.lastName = ref.getLastName();
        this.phoneNumber = ref.getPhoneNumber();
        this.role = ref.getRole();
    }
}