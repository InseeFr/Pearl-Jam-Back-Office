package fr.insee.pearljam.api.dto.closingcause;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.api.constants.Constants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClosingCauseCountDto {
	private Long npiCount;
	private Long npaCount;
	private Long npxCount;
	private Long rowCount;
	private Long total;

	public ClosingCauseCountDto() {
		super();
	}

	public ClosingCauseCountDto(Map<String, BigInteger> obj) {
		boolean nullOrEmpty = Optional.ofNullable(obj.isEmpty()).orElse(true);

		for (String str : Constants.CLOSING_CAUSE_FIELDS) {
			if (nullOrEmpty) {
				setLongField(str, 0L);
			} else {
				setLongField(str, Optional.ofNullable(obj.get(str)).orElse(BigInteger.ZERO).longValue());
			}
		}
	}

	public void setLongField(String fieldName, Long value) {
		try {
			Field field = getClass().getDeclaredField(fieldName);
			field.set(this, value);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			log.warn("Couldn't set field {} with value", fieldName, value);
		}
	}

}