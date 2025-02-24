package fr.insee.pearljam.api.bussinessrules;

import fr.insee.pearljam.api.domain.StateType;
import fr.insee.pearljam.api.dto.state.StateDto;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BusinessRulesTest {

	@Test
	void stateCanBeModifiedByManager() {
		assertThat(BusinessRules.stateCanBeModifiedByManager(StateType.CLO, StateType.NVA)).isTrue();
		assertThat(BusinessRules.stateCanBeModifiedByManager(StateType.NVA, StateType.NVA)).isFalse();
		assertThat(BusinessRules.stateCanBeModifiedByManager(StateType.NNS, StateType.ANV)).isTrue();
		assertThat(BusinessRules.stateCanBeModifiedByManager(StateType.FIN, StateType.ANV)).isFalse();
		assertThat(BusinessRules.stateCanBeModifiedByManager(StateType.NNS, StateType.VIN)).isTrue();
		assertThat(BusinessRules.stateCanBeModifiedByManager(StateType.ANV, StateType.VIN)).isTrue();
		assertThat(BusinessRules.stateCanBeModifiedByManager(StateType.WFT, StateType.VIN)).isFalse();
		assertThat(BusinessRules.stateCanBeModifiedByManager(StateType.TBR, StateType.FIN)).isTrue();
		assertThat(BusinessRules.stateCanBeModifiedByManager(StateType.WFT, StateType.FIN)).isFalse();
		assertThat(BusinessRules.stateCanBeModifiedByManager(StateType.FIN, StateType.WFT)).isTrue();
		assertThat(BusinessRules.stateCanBeModifiedByManager(StateType.ANV, StateType.WFT)).isFalse();
		assertThat(BusinessRules.stateCanBeModifiedByManager(StateType.ANV, StateType.CLO)).isTrue();
		assertThat(BusinessRules.stateCanBeModifiedByManager(StateType.ANV, StateType.TBR)).isFalse();
	}

	@Test
	void stateCanBeSeenByInterviewerBussinessRules() {
		assertThat(BusinessRules.stateCanBeSeenByInterviewerBussinessRules(StateType.VIN)).isTrue();
		assertThat(BusinessRules.stateCanBeSeenByInterviewerBussinessRules(StateType.FIN)).isTrue();
		assertThat(BusinessRules.stateCanBeSeenByInterviewerBussinessRules(StateType.CLO)).isTrue();
		assertThat(BusinessRules.stateCanBeSeenByInterviewerBussinessRules(StateType.NNS)).isFalse();
		assertThat(BusinessRules.stateCanBeSeenByInterviewerBussinessRules(StateType.NVA)).isFalse();
	}

	@Test
	void shouldFallBackToTbrOrFin() {
		StateDto state1 = new StateDto(1L, 1L, StateType.NVA);
		StateDto state2 = new StateDto(2L, 2L, StateType.VIN);
		StateDto state3 = new StateDto(3L, 3L, StateType.TBR);
		List<StateDto> statesWithNVA = List.of(state1, state2);
		List<StateDto> statesWithoutTBRorFIN = List.of(state2);
		List<StateDto> statesWithTBR = List.of(state3);

		assertThat(BusinessRules.shouldFallBackToTbrOrFin(statesWithNVA)).isFalse();
		assertThat(BusinessRules.shouldFallBackToTbrOrFin(statesWithoutTBRorFIN)).isTrue();
		assertThat(BusinessRules.shouldFallBackToTbrOrFin(statesWithTBR)).isFalse();
		assertThat(BusinessRules.shouldFallBackToTbrOrFin(List.of())).isFalse();
	}

	@Test
	void testBusinessRulesConstructorThrowsException() throws NoSuchMethodException {
		Constructor<BusinessRules> constructor = BusinessRules.class.getDeclaredConstructor();
		constructor.setAccessible(true);

		assertThrows(InvocationTargetException.class, constructor::newInstance);
	}
}