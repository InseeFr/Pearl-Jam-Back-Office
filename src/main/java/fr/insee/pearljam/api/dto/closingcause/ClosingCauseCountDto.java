package fr.insee.pearljam.api.dto.closingcause;

import java.math.BigInteger;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import static fr.insee.pearljam.api.constants.Constants.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClosingCauseCountDto {
	private Long npiCount;
	private Long npaCount;
	private Long npxCount;
	private Long rowCount;
	private Long total;

	private void initializeFields() {
		for (String fieldName : CLOSING_CAUSE_FIELDS) {
			setFieldValue(fieldName, 0L);
		}
	}

	private void setFieldValue(String fieldName, Long value) {
		switch (fieldName) {
			case NPI_COUNT:
				npiCount = value;
				break;
			case NPA_COUNT:
				npaCount = value;
				break;
			case NPX_COUNT:
				npxCount = value;
				break;
			case ROW_COUNT:
				rowCount = value;
				break;
			case TOTAL_COUNT:
				total = value;
				break;
			// Add more cases for other fields if needed
			default:
				// Handle unknown field name
				break;
		}
	}

	public ClosingCauseCountDto() {
		super();
	}

	public ClosingCauseCountDto(Map<String, BigInteger> obj) {
		initializeFields();
		if (obj != null) {
			for (Map.Entry<String, BigInteger> entry : obj.entrySet()) {
				String fieldName = entry.getKey();
				Long value = entry.getValue().longValue();
				setFieldValue(fieldName, value);
			}
		}

	}

}
