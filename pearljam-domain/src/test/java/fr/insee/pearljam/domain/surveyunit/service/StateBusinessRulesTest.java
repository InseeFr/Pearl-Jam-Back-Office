package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.contracts.surveyunit.dto.state.StateDto;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StateBusinessRulesTest {

	@Test
	void stateCanBeModifiedByManager() {
		assertThat(StateBusinessRules.stateCanBeModifiedByManager(StateType.CLO, StateType.NVA)).isTrue();
		assertThat(StateBusinessRules.stateCanBeModifiedByManager(StateType.NVA, StateType.NVA)).isFalse();
		assertThat(StateBusinessRules.stateCanBeModifiedByManager(StateType.NNS, StateType.ANV)).isTrue();
		assertThat(StateBusinessRules.stateCanBeModifiedByManager(StateType.FIN, StateType.ANV)).isFalse();
		assertThat(StateBusinessRules.stateCanBeModifiedByManager(StateType.NNS, StateType.VIN)).isTrue();
		assertThat(StateBusinessRules.stateCanBeModifiedByManager(StateType.ANV, StateType.VIN)).isTrue();
		assertThat(StateBusinessRules.stateCanBeModifiedByManager(StateType.WFT, StateType.VIN)).isFalse();
		assertThat(StateBusinessRules.stateCanBeModifiedByManager(StateType.TBR, StateType.FIN)).isTrue();
		assertThat(StateBusinessRules.stateCanBeModifiedByManager(StateType.WFT, StateType.FIN)).isFalse();
		assertThat(StateBusinessRules.stateCanBeModifiedByManager(StateType.FIN, StateType.WFT)).isTrue();
		assertThat(StateBusinessRules.stateCanBeModifiedByManager(StateType.ANV, StateType.WFT)).isFalse();
		assertThat(StateBusinessRules.stateCanBeModifiedByManager(StateType.ANV, StateType.CLO)).isTrue();
		assertThat(StateBusinessRules.stateCanBeModifiedByManager(StateType.ANV, StateType.TBR)).isFalse();
	}

	@Test
	void stateCanBeSeenByInterviewerBussinessRules() {
		assertThat(StateBusinessRules.stateCanBeSeenByInterviewerBussinessRules(StateType.VIN)).isTrue();
		assertThat(StateBusinessRules.stateCanBeSeenByInterviewerBussinessRules(StateType.FIN)).isTrue();
		assertThat(StateBusinessRules.stateCanBeSeenByInterviewerBussinessRules(StateType.CLO)).isTrue();
		assertThat(StateBusinessRules.stateCanBeSeenByInterviewerBussinessRules(StateType.NNS)).isFalse();
		assertThat(StateBusinessRules.stateCanBeSeenByInterviewerBussinessRules(StateType.NVA)).isFalse();
	}

	@Test
	void shouldFallBackToTbrOrFin() {
		StateDto state1 = new StateDto(1L, 1L, StateType.NVA);
		StateDto state2 = new StateDto(2L, 2L, StateType.VIN);
		StateDto state3 = new StateDto(3L, 3L, StateType.TBR);
		List<StateDto> statesWithNVA = List.of(state1, state2);
		List<StateDto> statesWithoutTBRorFIN = List.of(state2);
		List<StateDto> statesWithTBR = List.of(state3);

		assertThat(StateBusinessRules.shouldFallBackToTbrOrFin(statesWithNVA)).isFalse();
		assertThat(StateBusinessRules.shouldFallBackToTbrOrFin(statesWithoutTBRorFIN)).isTrue();
		assertThat(StateBusinessRules.shouldFallBackToTbrOrFin(statesWithTBR)).isFalse();
		assertThat(StateBusinessRules.shouldFallBackToTbrOrFin(List.of())).isFalse();
	}

	@Test
	void testBusinessRulesConstructorThrowsException() throws NoSuchMethodException {
		Constructor<StateBusinessRules> constructor = StateBusinessRules.class.getDeclaredConstructor();
		constructor.setAccessible(true);

		assertThrows(InvocationTargetException.class, constructor::newInstance);
	}
}