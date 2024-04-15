package fr.insee.pearljam.api.dto.phonenumber;

import fr.insee.pearljam.api.domain.PhoneNumber;
import fr.insee.pearljam.api.domain.Source;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PhoneNumberDto {

	private Source source;
	private boolean favorite;
	private String number;

	public PhoneNumberDto(PhoneNumber pn) {
		super();
		this.source = pn.getSource();
		this.favorite = pn.isFavorite();
		this.number = pn.getNumber();
	}

}
