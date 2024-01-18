package fr.insee.pearljam.api.domain;

import com.fasterxml.jackson.databind.JsonNode;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Entity surveyUnitTempZone
 * 
 * @author Laurent Caouissin
 * 
 */
@Entity
@Table(name = "survey_unit_temp_zone")
@Getter
@Setter
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
	@Column(name = "interviewer_id")
	private String interviewerId;

	/**
	 * The date of save
	 */
	@Column
	private Long date;
	/**
	 * The value of surveyUnit (jsonb format)
	 */
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "survey_unit", columnDefinition = "jsonb")
	private JsonNode surveyUnit;

	public SurveyUnitTempZone() {
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

}
