package fr.insee.pearljam.api.dto.state;

import static fr.insee.pearljam.api.constants.Constants.STATECOUNT_CLOSED_CLOSING_CAUSE_FIELDS;
import static fr.insee.pearljam.api.constants.Constants.STATE_COUNT_FIELDS;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerContextDto;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter(onMethod = @__(@JsonProperty))
@Setter
@Slf4j
@NoArgsConstructor
public class StateCountDto {

	private String idDem;
	private String labelDem;
	private InterviewerContextDto interviewer;
	private CampaignDto campaign;
	private Long nvmCount;
	private Long nnsCount;
	private Long anvCount;
	private Long vinCount;
	private Long vicCount;
	private Long prcCount;
	private Long aocCount;
	private Long apsCount;
	private Long insCount;
	private Long wftCount;
	private Long wfsCount;
	private Long tbrCount;
	private Long finCount;
	private Long cloCount;
	private Long nvaCount;
	private Long npaCount;
	private Long npiCount;
	private Long npxCount;
	private Long rowCount;
	private Long noticeCount;
	private Long reminderCount;
	private Long total;

	public StateCountDto(Map<String, Long> obj) {
		dispatchAttributeValues(obj, STATE_COUNT_FIELDS);
	}

	public StateCountDto(String idDem, String labelDem, Map<String, Long> obj) {
		this(obj);
		this.idDem = idDem;
		this.labelDem = labelDem;
	}

	public void addClosingCauseCount(Map<String, Long> obj) {
		dispatchAttributeValues(obj, STATECOUNT_CLOSED_CLOSING_CAUSE_FIELDS);
	}

	@SuppressWarnings("null") // to refactor with typed input
	private void dispatchAttributeValues(Map<String, Long> obj, List<String> fieldKeys) {
		boolean nullOrEmpty = (obj == null || obj.isEmpty());

		for (String str : fieldKeys) {
			if (nullOrEmpty) {
				setLongField(str, 0L);
			} else {
				// Recherche insensible à la casse
				Long value = obj.entrySet().stream()
						.filter(entry -> entry.getKey().equalsIgnoreCase(str))
						.map(Map.Entry::getValue)
						.filter(Objects::nonNull)
						.findFirst()
						.orElse(0L);

				setLongField(str, value);
			}
		}
	}


	private void setLongField(String fieldName, Long value) {
		try {
			Field field = getClass().getDeclaredField(fieldName);
			field.set(this, value);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			log.warn("Couldn't set field {} with value {}", fieldName, value);
		}
	}

	@Override
	public String toString() {
		return "StateCountDto [anvCount=" + anvCount + ", prcCount=" + prcCount + "]";
	}

}
