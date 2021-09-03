package fr.insee.pearljam.api.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

/**
* Entity surveyUnitTempZone
* 
* @author Laurent Caouissin
* 
*/
@Entity
@Table(name="survey_unit_temp_zone")
@TypeDef(
	    name = "jsonb",
	    typeClass = JsonBinaryType.class
	)
public class SurveyUnitTempZone {

	/**
	* The unique id of surveyUnitTempZone
	*/
	@Id
	@org.springframework.data.annotation.Id
    protected UUID id;

	/**
	 * The id of surveyUnit
	 */
	@Column(name = "survey_unit_id")
	private String surveyUnitId;

	/**
	 * The id of user
	 */
	@Column(name="interviewer_id")
	private String interviewerId;

	/**
	 * The date of save
	 */
	@Column
	private Long date;
	/**
	* The value of surveyUnit (jsonb format)
	*/
	@Type(type = "jsonb")
    @Column(name = "survey_unit", columnDefinition = "jsonb")
	private JsonNode surveyUnit;

	public SurveyUnitTempZone(){
		super();
		this.id = UUID.randomUUID();
	}

	public SurveyUnitTempZone(String surveyUnitId, String interviewerId, Long date, JsonNode surveyUnit) {
		super();
		this.id = UUID.randomUUID();
		this.surveyUnitId = surveyUnitId;
		this.interviewerId = interviewerId;
		this.date = date;
		this.surveyUnit = surveyUnit;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getSurveyUnitId() {
		return surveyUnitId;
	}

	public void setSurveyUnitId(String surveyUnitId) {
		this.surveyUnitId = surveyUnitId;
	}

	public String getInterviewerId() {
		return interviewerId;
	}

	public void setInterviewerId(String userId) {
		this.interviewerId = userId;
	}

	public Long getDate() {
		return date;
	}

	public void setDate(Long date) {
		this.date = date;
	}

	public JsonNode getSurveyUnit() {
		return surveyUnit;
	}

	public void setSurveyUnit(JsonNode surveyUnit) {
		this.surveyUnit = surveyUnit;
	}
}
