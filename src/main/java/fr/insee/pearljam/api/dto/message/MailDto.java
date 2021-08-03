package fr.insee.pearljam.api.dto.message;

public class MailDto {
	private String content;
	private String subject;
	
	
	public MailDto() {
		super();
	}
	
	public MailDto(String content, String subject) {
		super();
		this.content = content;
		this.subject = subject;
	}
	
	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}
	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}
	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	
}
