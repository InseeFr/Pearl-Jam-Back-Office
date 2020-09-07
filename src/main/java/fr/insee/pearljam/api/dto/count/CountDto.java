package fr.insee.pearljam.api.dto.count;

public class CountDto {
	private int count;

	
	public CountDto(int count) {
		super();
		this.count = count;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}
	
	
	
}
