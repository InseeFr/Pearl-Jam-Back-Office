package fr.insee.pearljam.api.surveyunit.dto;

import fr.insee.pearljam.domain.closingcause.model.ClosingCauseType;
import fr.insee.pearljam.domain.state.model.StateType;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class SurveyUnitDtoMappers {
    private SurveyUnitDtoMappers() {}

    public static String computeLocation(String l6) {
        if (l6 == null || !l6.trim().contains(" ")) {
            return null;
        }

        String[] parts = l6.split(" ");
        return parts.length > 0 ? parts[0] : null;
    }

    public static String computeCity(String l6) {
        if (l6 == null || !l6.trim().contains(" ")) {
            return null;
        }

        String[] parts = l6.split(" ");
        return Arrays.stream(parts)
                .skip(1)
                .collect(Collectors.joining(" "));
    }


    public static ClosingCauseType computeClosingCause(ClosingCauseType closingCause, StateType currentState) {
        if (closingCause != null && currentState != StateType.CLO) return closingCause;
        return null;
    }
}
