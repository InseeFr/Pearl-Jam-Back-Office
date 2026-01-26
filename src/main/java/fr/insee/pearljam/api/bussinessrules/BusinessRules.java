package fr.insee.pearljam.api.bussinessrules;

import fr.insee.pearljam.api.domain.StateType;
import fr.insee.pearljam.api.dto.state.StateDto;

import java.util.*;
import java.util.stream.Collectors;

public class BusinessRules {
	private BusinessRules() {
		throw new IllegalStateException("BusinessRules static class");
	}

	private static final EnumSet<StateType> STATES_VISIBLE_TO_INTERVIEWER =
			EnumSet.of(
					StateType.VIN,
					StateType.VIC,
					StateType.PRC,
					StateType.AOC,
					StateType.APS,
					StateType.INS,
					StateType.WFT,
					StateType.WFS,
					StateType.TBR,
					StateType.FIN,
					StateType.CLO
			);

	public static Set<StateType> statesVisibleToInterviewer() {
		return STATES_VISIBLE_TO_INTERVIEWER;
	}
	/*
	 * Checks if a survey unit is allowed to pass from a state to another
	 * via a manager action
	 */
	public static Boolean stateCanBeModifiedByManager(StateType currentState, StateType targetState) {
        return switch (targetState) {
            case NVA -> currentState != StateType.NVA;
            case ANV -> currentState == StateType.NNS;
            case VIN -> List.of(StateType.NNS, StateType.ANV).contains(currentState);
            case FIN -> currentState == StateType.TBR;
            case WFT -> List.of(StateType.FIN, StateType.TBR).contains(currentState);
            case CLO -> true;
            default -> false;
        };
	}

	/*
	 * Checks if a survey unit can be seen by the interviewer
	 * via an automatic business rule
	 */
	public static Boolean stateCanBeSeenByInterviewerBussinessRules(StateType currentState) {
		return STATES_VISIBLE_TO_INTERVIEWER.contains(currentState);
	}

	public static Boolean shouldFallBackToTbrOrFin(List<StateDto> states) {
		// If survey-unit has NVA 'Not Visible to All' => no fall-back
		Set<StateType> presentTypes = states.stream().map(StateDto::type).collect(Collectors.toSet());
		if (presentTypes.contains(StateType.NVA))
			return false;
		// Survey-unit should not already be in TBR or FIN state
		Optional<StateDto> optCurrentState = states.stream().max(Comparator.comparingLong(StateDto::date));
		if (optCurrentState.isEmpty()) {
			return false;
		}
		StateType currentState = optCurrentState.get().type();
		StateType[] fallBackTypes = { StateType.FIN, StateType.TBR };
		return Arrays.stream(fallBackTypes).noneMatch(currentState::equals);
	}

}