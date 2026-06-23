package fr.insee.pearljam.contracts.campaign.dto;

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
}
