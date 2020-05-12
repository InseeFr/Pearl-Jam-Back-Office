package fr.insee.pearljam.api.dto.comment;

import fr.insee.pearljam.api.domain.Comment;
import fr.insee.pearljam.api.domain.CommentType;

public class CommentDto {
	
	/**
	 * Type of the CommentDto
	 */
	private CommentType type;
	
	/**
	 * Value of the CommentDto
	 */
	private String value;
	
	public CommentDto(CommentType type, String value) {
		super();
		this.type = type;
		this.value = value;
	}
	
	public CommentDto(Comment comment) {
		super();
		this.type = comment.getType();
		this.value = comment.getValue();
	}

	public CommentDto() {
	}

	/**
	 * @return the type
	 */
	public CommentType getType() {
		return type;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(CommentType type) {
		this.type = type;
	}
	
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "CommentDto [type=" + type + ", value=" + value + "]";
	}

}
