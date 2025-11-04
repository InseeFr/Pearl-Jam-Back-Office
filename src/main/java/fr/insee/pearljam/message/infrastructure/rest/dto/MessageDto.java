package fr.insee.pearljam.message.infrastructure.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageDto {
	private Long id;
	private String text;
	private String sender;
	private List<String> recipients;
	private List<VerifyNameResponseDto> typedRecipients;
	private Long date;
	private String status;

	// don't remove : called from SQL
	public MessageDto(Long id, String text, String sender, Long date) {
		super();
		this.id = id;
		this.text = text;
		this.sender = sender;
		this.date = date;
	}
  
	public MessageDto(String text, List<String> recipients) {
		super();
		this.text = text;
		this.recipients = recipients;
	}

}
