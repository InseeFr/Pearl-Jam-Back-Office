package fr.insee.pearljam.api.dto.phonenumber;



import fr.insee.pearljam.api.domain.PhoneNumber;
import fr.insee.pearljam.api.domain.Source;


public class PhoneNumberDto {
	
	private Source source;
	private boolean favorite;
	private String number;
	
	/**
	 * Default constructor
	 */
	public PhoneNumberDto() {
		super();
	}
	

	public PhoneNumberDto(PhoneNumber pn) {
		super();
		this.source = pn.getSource();
		this.favorite = pn.isFavorite();
		this.number = pn.getNumber();
	}
	
	/**
	 * Constructor with all fields
	 * @param id
	 * @param source
	 * @param favorite
	 * @param number
	 * @param person
	 */
	public PhoneNumberDto(Source source, boolean favorite, String number) {
		super();
		this.source = source;
		this.favorite = favorite;
		this.number = number;
	}
	
	/**
	 * @return the source
	 */
	public Source getSource() {
		return source;
	}

	/**
	 * @return the favorite
	 */
	public boolean isFavorite() {
		return favorite;
	}

	/**
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(Source source) {
		this.source = source;
	}

	/**
	 * @param favorite the favorite to set
	 */
	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(String number) {
		this.number = number;
	}
}
