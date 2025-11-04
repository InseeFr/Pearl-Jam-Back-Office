package fr.insee.pearljam.message.infrastructure.rest.dto;

public class VerifyNameResponseDto {
	private String id;
	private String type;
	private String label;


	public VerifyNameResponseDto() {
		super();
	}
  
 	public VerifyNameResponseDto(String id, String type, String label) {
	    super();
	    this.id = id;
	    this.type = type;
	    this.label = label;
 	}

	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
}
