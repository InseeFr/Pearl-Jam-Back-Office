package fr.insee.pearljam.surveyunit.domain.service;

import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.StateType;
import fr.insee.pearljam.surveyunit.infrastructure.rest.dto.state.StateDto;

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
				return List.of(StateType.NNS,StateType.ANV).contains(currentState);
			case FIN:
				return currentState == StateType.TBR;
			case WFT:
				return List.of(StateType.FIN, StateType.TBR).contains(currentState);
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