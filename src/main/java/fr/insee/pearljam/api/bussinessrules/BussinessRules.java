package fr.insee.pearljam.api.bussinessrules;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import fr.insee.pearljam.api.domain.StateType;
import fr.insee.pearljam.api.dto.state.StateDto;

public class BussinessRules {
	private BussinessRules() {
		throw new IllegalStateException("BusinessRules static class");
	}
	
	// Checks if a survey unit is allowed to pass from a state to another
	public static Boolean stateCanBeModified(StateType currentState, StateType targetState) {
		switch(targetState) {
			case NVA: return currentState != StateType.NVA;
			case ANV: return currentState == StateType.NNS;
			case VIN: return currentState == StateType.NNS || currentState == StateType.ANV;
			case VIC: return currentState == StateType.VIN;
			case FIN: return currentState == StateType.TBR || currentState == StateType.WFS;
			case WFT: return currentState == StateType.FIN || currentState == StateType.TBR
					|| currentState == StateType.INS;
			case PRC: return currentState == StateType.VIC;
			case AOC: return currentState == StateType.APS || currentState == StateType.PRC;
			case APS: return currentState == StateType.AOC || currentState == StateType.PRC;
			case INS: return currentState == StateType.APS || currentState == StateType.PRC 
					|| currentState == StateType.FIN || currentState == StateType.TBR;
			case WFS: return currentState == StateType.WFT;
			case TBR: return currentState == StateType.WFS;
			case NVM: return currentState == StateType.ANV;
			default: return false;
		}
	}
	
	/* Checks if a survey unit is allowed to pass from a state to another
	 * via a manager action
	 */
	public static Boolean stateCanBeModifiedByManager(StateType currentState, StateType targetState) {
		switch(targetState) {
			case NVA: return currentState != StateType.NVA;
			case ANV: return currentState == StateType.NNS;
			case VIN: return currentState == StateType.NNS || currentState == StateType.ANV;
			case FIN: return currentState == StateType.TBR;
			case WFT: return currentState == StateType.FIN || currentState == StateType.TBR;
			case CLO: return true;
			default: return false;
		}
	}
	
	/* Checks if a survey unit is allowed to pass from a state to another
	 * via an interviewer action
	 */
	public static Boolean stateCanBeModifiedByInterviewer(StateType currentState, StateType targetState) {
		switch(targetState) {
			case PRC: return currentState == StateType.VIC;
			case AOC: return currentState == StateType.APS || currentState == StateType.PRC;
			case APS: return currentState == StateType.AOC || currentState == StateType.PRC;
			case INS: return currentState == StateType.APS || currentState == StateType.PRC 
					|| currentState == StateType.FIN || currentState == StateType.TBR;
			case WFT: return currentState == StateType.INS;
			case WFS: return currentState == StateType.WFT;
			case TBR: return currentState == StateType.WFS;
			case FIN: return currentState == StateType.WFS;
			default: return false;
		}
	}
	
	/* Checks if a survey unit is allowed to pass from a state to another
	 * via an automatic bussiness rule
	 */
	public static Boolean stateCanBeModifiedBussinessRules(StateType currentState, StateType targetState) {
		switch(targetState) {
			case VIC: return currentState == StateType.VIN;
			case WFT: return currentState == StateType.INS;			
			case FIN: return currentState == StateType.WFS;
			case TBR: return currentState == StateType.WFS;
			case ANV: return currentState == StateType.FIN;
			case NVM: return currentState == StateType.ANV;
			default: return false;
		}
	}
	
	/* Checks if a survey unit can be seen by the interviewer
	 * via an automatic bussiness rule
	 */
	public static Boolean stateCanBeSeenByInterviewerBussinessRules(StateType currentState) {
		//return new StateType[] {NNS,ANV, NVM}.includes(currentState)
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
		return Arrays.stream(possibleTypes).anyMatch(currentState::equals);
	}

	public static Boolean shouldFallBackToTbrOrFin(List<StateDto> states) {
		// If survey-unit has NVA 'Not Visible to All' => no fall-back
		Set<StateType> presentTypes = states.stream().map(StateDto::getType).collect(Collectors.toSet());
		if (presentTypes.contains(StateType.NVA))
			return false;
		// Survey-unit should not already be in TBR or FIN state
		StateType currentState = states.stream().max(Comparator.comparingLong(StateDto::getDate)).get().getType();
		StateType[] fallBackTypes = { StateType.FIN, StateType.TBR };
		return ! Arrays.stream(fallBackTypes).anyMatch(currentState::equals);
	}

}