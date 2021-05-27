package fr.insee.pearljam.api.dto.statedata;

public class StateDataDto {
	private String state;
	private Long date;
	private String currentPage;
	
	public StateDataDto() {
		super();
	}
	
	public StateDataDto(String state, Long date, String currentPage) {
		super();
		this.state = state;
		this.date = date;
		this.currentPage = currentPage;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public Long getDate() {
		return date;
	}
	public void setDate(Long date) {
		this.date = date;
	}
	public String getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(String currentPage) {
		this.currentPage = currentPage;
	}
}
