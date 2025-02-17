package fr.insee.pearljam.api.bussinessrules;

import fr.insee.pearljam.api.domain.StateType;
import fr.insee.pearljam.api.dto.state.StateDto;

import java.util.*;
import java.util.stream.Collectors;

public class BusinessRules {
	private BusinessRules() {
		throw new IllegalStateException("BusinessRules static class");
	}

	/*
	 * Checks if a survey unit is allowed to pass from a state to another
	 * via a manager action
	 */
	public static Boolean stateCanBeModifiedByManager(StateType currentState, StateType targetState) {
		switch (targetState) {
			case NVA:
				return currentState != StateType.NVA;
			case ANV:
				return currentState == StateType.NNS;
			case VIN:
				return currentState == StateType.NNS || currentState == StateType.ANV;
			case FIN:
				return currentState == StateType.TBR;
			case WFT:
				return currentState == StateType.FIN || currentState == StateType.TBR;
			case CLO:
				return true;
			default:
				return false;
		}
	}




	/*
	 * Checks if a survey unit can be seen by the interviewer
	 * via an automatic business rule
	 */
	public static Boolean stateCanBeSeenByInterviewerBussinessRules(StateType currentState) {
		StateType[] possibleTypes = {
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
				StateType.CLO,
		};
		return Arrays.asList(possibleTypes).contains(currentState);
	}

	public static Boolean shouldFallBackToTbrOrFin(List<StateDto> states) {
		// If survey-unit has NVA 'Not Visible to All' => no fall-back
		Set<StateType> presentTypes = states.stream().map(StateDto::getType).collect(Collectors.toSet());
		if (presentTypes.contains(StateType.NVA))
			return false;
		// Survey-unit should not already be in TBR or FIN state
		Optional<StateDto> optCurrentState = states.stream().max(Comparator.comparingLong(StateDto::getDate));
		if (optCurrentState.isEmpty()) {
			return false;
		}
		StateType currentState = optCurrentState.get().getType();
		StateType[] fallBackTypes = { StateType.FIN, StateType.TBR };
		return Arrays.stream(fallBackTypes).noneMatch(currentState::equals);
	}

}