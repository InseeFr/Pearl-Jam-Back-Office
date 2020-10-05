package fr.insee.pearljam.api.dto.message;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageDto {
	private Long id;
	private String text;
	private String sender;
	private List<String> recipients;
	private List<VerifyNameResponseDto> typedRecipients;
	private Long date;
	private Integer status;

	
	public MessageDto(Long id, String text, String sender, List<String> recipients, Long date) {
		super();
		this.id = id;
		this.text = text;
		this.sender = sender;
		this.recipients = recipients;
		this.date = date;
	}

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
	
	public MessageDto() {
		super();
	}

	/**
	 * @return the id of the Message
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id of the Message
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the text of the Message
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text of the Message
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the last name of the Message
	 */
	public String getSender() {
		return sender;
	}

	/**
	 * @param the last name of the Message
	 */
	public void setSender(String sender) {
		this.sender = sender;
	}

	/**
	 * @return the email of the Message
	 */
	public List<String> getRecipients() {
		return recipients;
	}

	/**
	 * @param the email of the Message
	 */
	public void setRecipients(List<String> recipients) {
		this.recipients = recipients;
	}

	/**
	 * @return the phone number of the Message
	 */
	public Long getDate() {
		return date;
	}

	/**
	 * @param the phone number of the Message
	 */
	public void setDate(Long date) {
		this.date = date;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public List<VerifyNameResponseDto> getTypedRecipients() {
		return typedRecipients;
	}

	public void setTypedRecipients(List<VerifyNameResponseDto> typedRecipients) {
		this.typedRecipients = typedRecipients;
	}	
	
}
