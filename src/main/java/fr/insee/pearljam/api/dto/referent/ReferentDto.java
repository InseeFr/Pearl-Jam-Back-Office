package fr.insee.pearljam.api.dto.referent;

import fr.insee.pearljam.api.domain.Referent;

/**
 * ReferentDto
 */

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

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}