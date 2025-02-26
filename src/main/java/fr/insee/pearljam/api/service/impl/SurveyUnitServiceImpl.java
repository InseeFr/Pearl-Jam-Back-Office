package fr.insee.pearljam.api.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pearljam.api.bussinessrules.BusinessRules;
import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.domain.*;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import fr.insee.pearljam.api.dto.person.PersonDto;
import fr.insee.pearljam.api.dto.state.StateDto;
import fr.insee.pearljam.api.dto.surveyunit.*;
import fr.insee.pearljam.api.exception.BadRequestException;
import fr.insee.pearljam.api.repository.*;
import fr.insee.pearljam.api.service.SurveyUnitService;
import fr.insee.pearljam.api.service.SurveyUnitUpdateService;
import java.util.function.Function;
import fr.insee.pearljam.api.service.UserService;
import fr.insee.pearljam.api.service.UtilsService;
import fr.insee.pearljam.api.surveyunit.dto.ContactOutcomeDto;
import fr.insee.pearljam.api.surveyunit.dto.SurveyUnitInterviewerResponseDto;
import fr.insee.pearljam.api.surveyunit.dto.SurveyUnitUpdateDto;
import fr.insee.pearljam.api.surveyunit.dto.SurveyUnitVisibilityDto;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationTemplate;
import fr.insee.pearljam.domain.campaign.port.serverside.VisibilityRepository;
import fr.insee.pearljam.domain.campaign.port.userside.CommunicationTemplateService;
import fr.insee.pearljam.domain.exception.PersonNotFoundException;
import fr.insee.pearljam.domain.exception.SurveyUnitNotFoundException;
import fr.insee.pearljam.domain.surveyunit.model.IdentificationState;
import fr.insee.pearljam.domain.surveyunit.model.SurveyUnitForInterviewer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author scorcaud
 *
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class SurveyUnitServiceImpl implements SurveyUnitService {

	private static final String GUEST = "GUEST";

	private static final String SU_ID_NOT_FOUND_FOR_INTERVIEWER = "Survey Unit {} not found in DB for interviewer {}";
	private static final String SU_ID_NOT_FOUND = "Survey unit with id {} was not found in database";

	private final SurveyUnitRepository surveyUnitRepository;
	private final SurveyUnitTempZoneRepository surveyUnitTempZoneRepository;
	private final AddressRepository addressRepository;
	private final ContactOutcomeRepository contactOutcomeRepository;
	private final StateRepository stateRepository;
	private final InterviewerRepository interviewerRepository;
	private final CampaignRepository campaignRepository;
	private final OrganizationUnitRepository organizationUnitRepository;
	private final VisibilityRepository visibilityRepository;
	private final PersonRepository personRepository;
	private final ClosingCauseRepository closingCauseRepository;
	private final UserService userService;
	private final UtilsService utilsService;
	private final SurveyUnitUpdateService surveyUnitUpdateService;
	private final CommunicationTemplateService communicationTemplateService;

	@Override
	public boolean checkHabilitationInterviewer(String userId, String id) {
		return surveyUnitRepository.findByIdAndInterviewerIdIgnoreCase(id, userId).isPresent();
	}

	@Override
	public boolean checkHabilitationReviewer(String userId, String id) {
		List<String> userOUs = userService.getUserOUs(userId, true)
				.stream().map(OrganizationUnitDto::getId)
				.toList();

		return !surveyUnitRepository.findByIdInOrganizationalUnit(id, userOUs).isEmpty();
	}

	@Override
	public SurveyUnit getSurveyUnit(String surveyUnitId) {
		return surveyUnitRepository
				.findById(surveyUnitId)
				.orElseThrow(() -> new SurveyUnitNotFoundException(surveyUnitId));
	}

	@Override
	public SurveyUnitInterviewerResponseDto getSurveyUnitInterviewerDetail(String userId, String surveyUnitId) {
		SurveyUnit surveyUnit = surveyUnitRepository
				.findByIdAndInterviewerIdIgnoreCase(surveyUnitId, userId)
				.orElseThrow(() -> {
					log.error("Survey unit with id {} is not associated to the interviewer {}", surveyUnitId, userId);
					return new SurveyUnitNotFoundException(surveyUnitId);
				});

		if (!canBeSeenByInterviewer(surveyUnit.getId())) {
			log.error(String.format("Survey unit with id %s is not associated to the interviewer %s anymore", surveyUnitId, userId));
			throw new SurveyUnitNotFoundException(surveyUnitId);
		}

		List<CommunicationTemplate> communicationTemplates = communicationTemplateService.findCommunicationTemplates(surveyUnit.getCampaign().getId());
		SurveyUnitForInterviewer surveyUnitForInterviewer = new SurveyUnitForInterviewer(surveyUnit, communicationTemplates);
		// TODO: when refacto survey unit, return model object instead of dto here
		// cannot do this now as the hibernate session is not propagated to the controller
		return SurveyUnitInterviewerResponseDto.fromModel(surveyUnitForInterviewer);
	}

	public List<SurveyUnitDto> getSurveyUnitDto(String userId, Boolean extended) {
		List<String> surveyUnitDtoIds = surveyUnitRepository.findIdsByInterviewerId(userId);

		if (surveyUnitDtoIds.isEmpty()) {
			log.error("No Survey Unit found for interviewer {}", userId);
			return List.of();
		}

		surveyUnitDtoIds = surveyUnitDtoIds.stream().filter(this::canBeSeenByInterviewer)
				.toList();

		return surveyUnitDtoIds.stream()
				.map(idSurveyUnit -> new SurveyUnitDto(idSurveyUnit,
						campaignRepository.findDtoBySurveyUnitId(idSurveyUnit),
						SurveyUnitVisibilityDto.fromModel(
								visibilityRepository.getVisibilityBySurveyUnitId(idSurveyUnit))))
				.toList();
	}

	@Override
	public boolean canBeSeenByInterviewer(String suId) {
		StateDto dto = stateRepository.findFirstDtoBySurveyUnitIdOrderByDateDesc(suId);
		StateType currentState = dto != null ? dto.type() : null;
		return currentState != null && BusinessRules.stateCanBeSeenByInterviewerBussinessRules(currentState);
	}

	@Transactional
	public SurveyUnitDetailDto updateSurveyUnit(String userId, String surveyUnitId,
												SurveyUnitUpdateDto surveyUnitUpdate) throws PersonNotFoundException {
		log.info("Update Survey Unit {}", surveyUnitId);

		Optional<SurveyUnit> surveyUnitOpt;
		if (userId.equals(GUEST)) {
			surveyUnitOpt = surveyUnitRepository.findById(surveyUnitId);
		} else {
			surveyUnitOpt = surveyUnitRepository.findByIdAndInterviewerIdIgnoreCase(surveyUnitId, userId);
		}
		SurveyUnit surveyUnit = surveyUnitOpt.orElseThrow(() -> new SurveyUnitNotFoundException(surveyUnitId));
		surveyUnit.setMove(surveyUnitUpdate.move());
		updateAddress(surveyUnit, surveyUnitUpdate);
		updatePersons(surveyUnitUpdate);

		updateStates(surveyUnit, surveyUnitUpdate);
		updateContactAttempt(surveyUnit, surveyUnitUpdate);

		surveyUnitUpdateService.updateSurveyUnitInfos(surveyUnit, surveyUnitUpdate);
		surveyUnitRepository.save(surveyUnit);

		log.info("Survey Unit {} - update complete", surveyUnitId);
		return new SurveyUnitDetailDto(surveyUnitRepository.findById(surveyUnitId).get());
	}

	private void updateContactAttempt(SurveyUnit surveyUnit, SurveyUnitUpdateDto surveyUnitUpdateDto) {
		if (surveyUnitUpdateDto.contactAttempts() != null) {
			Set<ContactAttempt> contactAttemps = surveyUnit.getContactAttempts();
			contactAttemps.clear();
			Set<ContactAttempt> newContactAttempts = surveyUnitUpdateDto.contactAttempts().stream()
					.map(dto -> new ContactAttempt(dto, surveyUnit)).collect(Collectors.toSet());
			contactAttemps.addAll(newContactAttempts);
			log.info("Survey-unit {} - Contact attempts updated", surveyUnit.getId());
		}
	}

	private void updateStates(SurveyUnit surveyUnit, SurveyUnitUpdateDto surveyUnitUpdateDto) {
		if (surveyUnitUpdateDto.states() != null) {
			surveyUnitUpdateDto.states().stream()
					.filter(s -> s.id() == null || !stateRepository.existsById(s.id()))
					.forEach(s -> stateRepository.save(new State(s.date(), surveyUnit, s.type())));
		}
		StateType currentState = stateRepository.findFirstDtoBySurveyUnitIdOrderByDateDesc(surveyUnit.getId())
				.type();
		if (currentState == StateType.WFS) {
			addStateAuto(surveyUnit);
		}
		List<StateDto> dbStates = stateRepository.findAllDtoBySurveyUnitIdOrderByDateAsc(surveyUnit.getId());
		if (Boolean.TRUE.equals(BusinessRules.shouldFallBackToTbrOrFin(dbStates))) {
			Set<State> ueStates = surveyUnit.getStates();
			if (ueStates.stream().anyMatch(s -> s.getType() == StateType.FIN)) {
				ueStates.add(new State(new Date().getTime(), surveyUnit, StateType.FIN));
			} else if (ueStates.stream().anyMatch(s -> s.getType() == StateType.TBR)) {
				ueStates.add(new State(new Date().getTime(), surveyUnit, StateType.TBR));
			}
		}
	}

	private void addStateAuto(SurveyUnit surveyUnit) {
		if (surveyUnitRepository.findCountUeINATBRByInterviewerIdAndCampaignId(surveyUnit.getInterviewer().getId(),
				surveyUnit.getCampaign().getId(), surveyUnit.getId()) < 5) {
			stateRepository.save(new State(new Date().getTime(), surveyUnit, StateType.TBR));
			surveyUnit.setClosingCause(null);
		} else {
			stateRepository.save(new State(new Date().getTime(), surveyUnit, StateType.FIN));
			surveyUnit.setClosingCause(null);
		}
	}

	private void updatePersons(SurveyUnitUpdateDto surveyUnitUpdateDto) throws PersonNotFoundException {
		if (surveyUnitUpdateDto.persons() == null) {
            return;
        }
        for (PersonDto personDto : surveyUnitUpdateDto.persons()) {
            Person pers = personRepository.findById(personDto.getId()).orElseThrow(PersonNotFoundException::new);
            updatePerson(personDto, pers);
        }
	}

	private void updatePerson(PersonDto person, Person pers) {
		if (person.getFirstName() != null) {
			pers.setFirstName(person.getFirstName());
		}
		if (person.getLastName() != null) {
			pers.setLastName(person.getLastName());
		}
		if (person.getBirthdate() != null) {
			pers.setBirthdate(person.getBirthdate());
		}
		if (person.getTitle() != null) {
			pers.setTitle(person.getTitle());
		}
		if (person.getEmail() != null) {
			pers.setEmail(person.getEmail());
		}
		if (person.getFavoriteEmail() != null) {
			pers.setFavoriteEmail(person.getFavoriteEmail());
		}
		if (person.getPrivileged() != null) {
			pers.setPrivileged(person.getPrivileged());
		}
		if (person.getPhoneNumbers() != null) {
			Set<PhoneNumber> phoneNumbers = person.getPhoneNumbers().stream().map(pn -> new PhoneNumber(pn, pers))
					.collect(Collectors.toSet());
			pers.getPhoneNumbers().clear();
			pers.getPhoneNumbers().addAll(phoneNumbers);
		}
	}

	private void updateAddress(SurveyUnit surveyUnit, SurveyUnitUpdateDto surveyUnitUpdateDto) {
		if (surveyUnitUpdateDto.address() != null) {
			InseeAddress inseeAddress;
			Optional<InseeAddress> optionalInseeAddress = addressRepository.findById(surveyUnit.getAddress().getId());
			if (optionalInseeAddress.isEmpty()) {
				inseeAddress = new InseeAddress(surveyUnitUpdateDto.address());
			} else {
				inseeAddress = optionalInseeAddress.get();
				inseeAddress.setL1(surveyUnitUpdateDto.address().getL1());
				inseeAddress.setL2(surveyUnitUpdateDto.address().getL2());
				inseeAddress.setL3(surveyUnitUpdateDto.address().getL3());
				inseeAddress.setL4(surveyUnitUpdateDto.address().getL4());
				inseeAddress.setL5(surveyUnitUpdateDto.address().getL5());
				inseeAddress.setL6(surveyUnitUpdateDto.address().getL6());
				inseeAddress.setL7(surveyUnitUpdateDto.address().getL7());
				inseeAddress.setBuilding(surveyUnitUpdateDto.address().getBuilding());
				inseeAddress.setFloor(surveyUnitUpdateDto.address().getFloor());
				inseeAddress.setDoor(surveyUnitUpdateDto.address().getDoor());
				inseeAddress.setStaircase(surveyUnitUpdateDto.address().getStaircase());
				inseeAddress.setElevator(surveyUnitUpdateDto.address().getElevator());
				inseeAddress.setCityPriorityDistrict(surveyUnitUpdateDto.address().getCityPriorityDistrict());
			}
			// Update Address
			addressRepository.save(inseeAddress);
		}
	}

	@Transactional
	public HttpStatus updateSurveyUnitViewed(String userId, String suId) {
		Optional<SurveyUnit> surveyUnitOpt = surveyUnitRepository.findById(suId);
		if (surveyUnitOpt.isEmpty()) {
			log.error(SU_ID_NOT_FOUND_FOR_INTERVIEWER, suId, userId);
			return HttpStatus.NOT_FOUND;
		}
		SurveyUnit surveyUnit = surveyUnitOpt.get();
		surveyUnit.setViewed(true);
		surveyUnitRepository.save(surveyUnit);
		return HttpStatus.OK;
	}

	public Set<SurveyUnitCampaignDto> getSurveyUnitByCampaign(String campaignId, String userId, String state) {
		List<String> lstOuId = userService.getUserOUs(userId, true).stream().map(OrganizationUnitDto::getId)
				.toList();
		Set<SurveyUnit> lstSurveyUnit = surveyUnitRepository.findByCampaignIdAndOrganizationUnitIdIn(campaignId,
				lstOuId);
		if (state != null && !state.isEmpty() && state.equalsIgnoreCase(StateType.FIN.toString())) {
			// filter on SU with at least one state FIN
			lstSurveyUnit = lstSurveyUnit.stream().filter(su -> su.isAtLeastState(state)).collect(Collectors.toSet());
		} else if (state != null && !state.isEmpty() && !state.equalsIgnoreCase(StateType.FIN.toString())) {
			// filter on SU with last state equals to state
			lstSurveyUnit = lstSurveyUnit.stream().filter(su -> su.isLastState(state)).collect(Collectors.toSet());
		}
		if (lstSurveyUnit.isEmpty()) {
			log.warn("No Survey Unit found for the user {}", userId);
		}
		return lstSurveyUnit.stream().map(SurveyUnitCampaignDto::new).collect(Collectors.toSet());
	}

	// TODO : use future identification state in rules instead of specific identification attributes
	public List<SurveyUnitCampaignDto> getClosableSurveyUnits(HttpServletRequest request, String userId) {
		List<String> lstOuId = userService.getUserOUs(userId, true).stream().map(OrganizationUnitDto::getId)
				.toList();

		// Récupération des SurveyUnitIds pour chaque configuration
		Map<IdentificationConfiguration, List<String>> surveyUnitIdsByConfig = Arrays.stream(IdentificationConfiguration.values())
				.collect(Collectors.toMap(
						config -> config,
						config -> surveyUnitRepository.findSurveyUnitIdsOfOrganizationUnitsInProcessingPhaseByIdentificationConfiguration(
								System.currentTimeMillis(), lstOuId, config)
				));

		// Mapping entre chaque configuration et la méthode appropriée pour récupérer les unités fermables
		Map<IdentificationConfiguration, Function<List<String>, List<SurveyUnit>>> closableSurveyUnitMethods = Map.of(
				IdentificationConfiguration.NOIDENT, surveyUnitRepository::findClosableNoIdentSurveyUnitId,
				IdentificationConfiguration.IASCO, surveyUnitRepository::findClosableIascoSurveyUnitId,
				IdentificationConfiguration.INDF2F, surveyUnitRepository::findClosableIndf2fFSurveyUnitId,
				IdentificationConfiguration.INDF2FNOR, surveyUnitRepository::findClosableIndf2fnorFSurveyUnitId,
				IdentificationConfiguration.INDTEL, surveyUnitRepository::findClosableIndtelFSurveyUnitId,
				IdentificationConfiguration.INDTELNOR, surveyUnitRepository::findClosableIndtelnorFSurveyUnitId
		);


		// Récupération des SurveyUnits à vérifier
		List<SurveyUnit> suToCheck = surveyUnitIdsByConfig.entrySet().stream()
				.flatMap(entry -> {
					IdentificationConfiguration config = entry.getKey();
					List<String> surveyUnitIds = entry.getValue();
					return closableSurveyUnitMethods.getOrDefault(config, ids -> List.of()).apply(surveyUnitIds).stream();
				}).toList();

		Map<String, String> mapQuestionnaireStateBySu = Collections.emptyMap();

		try {
			mapQuestionnaireStateBySu = getQuestionnaireStatesFromDataCollection(request,
					suToCheck.stream().map(SurveyUnit::getId).toList());

		} catch (Exception e) {
			log.error("Could not get data collection API : {}", e.getMessage());
			log.error("All questionnaire states will be considered null");
		}

		final Map<String, String> map = mapQuestionnaireStateBySu;

		return suToCheck.stream().map(su -> {
			SurveyUnitCampaignDto sudto = new SurveyUnitCampaignDto(su);
					IdentificationState identificationResult =
							IdentificationState.getState(su.getModelIdentification(),
									sudto.getIdentificationConfiguration());
			sudto.setIdentificationState(identificationResult);
			String questionnaireState = Optional.ofNullable(map.get(su.getId())).orElse(Constants.UNAVAILABLE);
			sudto.setQuestionnaireState(questionnaireState);
			return sudto;

		}).filter(this::isClosable)
				.toList();

	}

	private boolean isClosable(SurveyUnitCampaignDto sudto) {

		boolean hasQuestionnaire = !Constants.UNAVAILABLE.equals(sudto.getQuestionnaireState());

		ContactOutcomeDto outcome = sudto.getContactOutcome();
		if (outcome == null)
			return !hasQuestionnaire;
        return switch (outcome.type()) {
            case INA, NOA -> !hasQuestionnaire;
            default -> true;
        };
	}

	private Map<String, String> getQuestionnaireStatesFromDataCollection(HttpServletRequest request,
			List<String> lstSu) {
		ResponseEntity<SurveyUnitOkNokDto> result = utilsService.getQuestionnairesStateFromDataCollection(request,
				lstSu);
		log.info("GET state from data collection service call resulting in {}", result.getStatusCode());
		SurveyUnitOkNokDto object = result.getBody();
		HttpStatusCode responseCode = result.getStatusCode();

		if (!responseCode.equals(HttpStatus.OK)) {
			String code = responseCode.toString();
			log.error("Data collection API responded with error code {}", code);
		}
		if (object == null) {
			log.error("Could not get response from data collection API");
			throw new BadRequestException(404, "Could not get response from data collection API");
		}
		Map<String, String> mapResult = new HashMap<>();
		object.getSurveyUnitNOK().forEach(su -> mapResult.put(su.getId(), Constants.UNAVAILABLE));
		object.getSurveyUnitOK().forEach(su -> mapResult.put(su.getId(), su.getStateData().getState()));
		return mapResult;
	}

	@Transactional
	public HttpStatus addStateToSurveyUnit(String surveyUnitId, StateType state) {
		Optional<SurveyUnit> su = surveyUnitRepository.findById(surveyUnitId);
		if (su.isPresent()) {
			StateType currentState = stateRepository.findFirstDtoBySurveyUnitOrderByDateDesc(su.get()).type();
			if (Boolean.TRUE.equals(BusinessRules.stateCanBeModifiedByManager(currentState, state))) {
				if (StateType.TBR.equals(state) || StateType.FIN.equals(state)) {
					log.info("Deleting closing causes of survey unit {}", surveyUnitId);
					closingCauseRepository.deleteBySurveyUnitId(surveyUnitId);
				}
				stateRepository.save(new State(new Date().getTime(), su.get(), state));
				return HttpStatus.OK;
			} else {
				log.error("Cannot pass from state {} to state {}, it does not respect bussiness rules", currentState,
						state);
				return HttpStatus.FORBIDDEN;
			}
		} else {
			log.error(SU_ID_NOT_FOUND, surveyUnitId);
			return HttpStatus.BAD_REQUEST;
		}
	}

	@Transactional
	public HttpStatus closeSurveyUnit(String surveyUnitId, ClosingCauseType type) {
		Optional<SurveyUnit> su = surveyUnitRepository.findById(surveyUnitId);

		if (su.isPresent()) {
			SurveyUnit surveyUnit = su.get();
			log.info("{} -> {}", surveyUnitId, type);
			StateType currentState = stateRepository.findFirstDtoBySurveyUnitIdOrderByDateDesc(surveyUnitId).type();
			if (currentState.equals(StateType.CLO)) {
				addOrModifyClosingCause(surveyUnit, type);
				return HttpStatus.OK;
			} else if (Boolean.TRUE.equals(BusinessRules.stateCanBeModifiedByManager(currentState, StateType.CLO))) {
				stateRepository.save(new State(new Date().getTime(), su.get(), StateType.CLO));
				addOrModifyClosingCause(surveyUnit, type);
				return HttpStatus.OK;
			} else {
				log.error("Cannot pass from state {} to state {}, it does not respect bussiness rules", currentState,
						StateType.CLO);
				return HttpStatus.FORBIDDEN;
			}

		} else {
			log.error(SU_ID_NOT_FOUND, surveyUnitId);
			return HttpStatus.BAD_REQUEST;
		}
	}

	@Transactional
	public HttpStatus updateClosingCause(String surveyUnitId, ClosingCauseType type) {
		Optional<SurveyUnit> su = surveyUnitRepository.findById(surveyUnitId);
		if (su.isPresent()) {
			SurveyUnit surveyUnit = su.get();
			addOrModifyClosingCause(surveyUnit, type);
			return HttpStatus.OK;
		} else {
			log.error(SU_ID_NOT_FOUND, surveyUnitId);
			return HttpStatus.NOT_FOUND;
		}
	}

	private void addOrModifyClosingCause(SurveyUnit surveyUnit, ClosingCauseType type) {
		ClosingCause cc;
		if (surveyUnit.getClosingCause() != null) {
			cc = surveyUnit.getClosingCause();
		} else {
			cc = new ClosingCause();
			cc.setSurveyUnit(surveyUnit);
		}
		cc.setDate(System.currentTimeMillis());
		cc.setType(type);

		surveyUnit.setClosingCause(cc);
		surveyUnitRepository.save(surveyUnit);
	}

	public List<StateDto> getListStatesBySurveyUnitId(String suId) {
		Optional<SurveyUnit> su = surveyUnitRepository.findById(suId);
		if (su.isEmpty()) {
			log.error("SU {} not found in database", suId);
			return List.of();
		}
		return stateRepository.findAllDtoBySurveyUnitIdOrderByDateAsc(suId);

	}

	@Override
	public List<SurveyUnit> getSurveyUnitIdByOrganizationUnits(List<String> lstOuId) {
		return surveyUnitRepository.findByOrganizationUnitIdIn(lstOuId);
	}

	@Override
	public Response createSurveyUnits(List<SurveyUnitContextDto> surveyUnits) {
		// Check duplicate line in interviewers to create
		Map<String, Integer> duplicates = new HashMap<>();
		List<String> surveyUnitErrors = new ArrayList<>();
		List<SurveyUnit> listSurveyUnits = new ArrayList<>();
		List<String> surveyUnitsDb = surveyUnitRepository.findAllIds();
		Map<String, Campaign> mapCampaigns = campaignRepository.findAllById(
				surveyUnits.stream()
						.map(SurveyUnitContextDto::getCampaign)
						.toList())
				.stream().collect(Collectors.toMap(Campaign::getId, c -> c));
		Map<String, OrganizationUnit> mapOrganizationUnits = organizationUnitRepository.findAllById(
				surveyUnits.stream()
						.map(SurveyUnitContextDto::getOrganizationUnitId)
						.toList())
				.stream().collect(Collectors.toMap(OrganizationUnit::getId, gl -> gl));
		surveyUnits.forEach(su -> {
			if (!duplicates.containsKey(su.getId())) {
				duplicates.put(su.getId(), 0);
			}
			duplicates.put(su.getId(), duplicates.get(su.getId()) + 1);
			if (surveyUnitsDb.contains(su.getId())) {
				duplicates.put(su.getId(), duplicates.get(su.getId()) + 1);
			}
			if (!checkValidity(su, mapOrganizationUnits, mapCampaigns)) {
				surveyUnitErrors.add(su.getId());
			}
			listSurveyUnits.add(new SurveyUnit(su, mapOrganizationUnits.get(su.getOrganizationUnitId()),
					mapCampaigns.get(su.getCampaign())));
		});
		// Check attributes are not null
		if (!surveyUnitErrors.isEmpty()) {
			log.error("Invalid format : [{}]", String.join(", ", surveyUnitErrors));
			return new Response(String.format("Invalid format : [%s]", String.join(", ", surveyUnitErrors)),
					HttpStatus.BAD_REQUEST);
		}

		// Check duplicate lines
		if (!duplicates.keySet().stream().filter(id -> duplicates.get(id) > 1).collect(Collectors.toSet()).isEmpty()) {
			log.error("Duplicate entry : [{}]", String.join(", ", duplicates.keySet()));
			return new Response(String.format("Duplicate entries : [%s]", String.join(", ", duplicates.keySet())),
					HttpStatus.BAD_REQUEST);
		}
		surveyUnitRepository.saveAll(listSurveyUnits);
		return new Response(String.format("%s surveyUnits created", listSurveyUnits.size()), HttpStatus.OK);
	}

	private boolean checkValidity(SurveyUnitContextDto su, Map<String, OrganizationUnit> ous,
			Map<String, Campaign> camps) {
		if (!su.isValid()) {
			log.info("Su {} is not valid", su.getId());
			return false;
		}
		if (!ous.containsKey(su.getOrganizationUnitId())) {
			log.info("Su {} : OU {} not found!", su.getId(), su.getOrganizationUnitId());

			return false;
		}
		if (!camps.containsKey(su.getCampaign())) {
			log.info("Su {} : camp {} not found!", su.getId(), su.getCampaign());
			return false;
		}

		return true;
	}

	@Override
	@Transactional
	public Response createSurveyUnitInterviewerLinks(List<SurveyUnitInterviewerLinkDto> surveyUnitInterviewerLink) {

		// Get SurveyUnits and Interviewers to create
		Map<String, SurveyUnit> mapSurveyUnit = surveyUnitRepository
				.findAllById(surveyUnitInterviewerLink.stream()
						.map(SurveyUnitInterviewerLinkDto::getSurveyUnitId)
						.toList())
				.stream().collect(Collectors.toMap(SurveyUnit::getId, su -> su));
		Map<String, Interviewer> mapInterviewer = interviewerRepository
				.findAllById(surveyUnitInterviewerLink.stream()
						.map(SurveyUnitInterviewerLinkDto::getInterviewerId)
						.toList())
				.stream().collect(Collectors.toMap(Interviewer::getId, itw -> itw));

		// Create new assignment
		List<String> errors = surveyUnitInterviewerLink.stream()
				.filter(link -> !link.isValid()
						|| !mapSurveyUnit.containsKey(link.getSurveyUnitId())
						|| !mapInterviewer.containsKey(link.getInterviewerId()))
				.map(SurveyUnitInterviewerLinkDto::getLink).toList();
		if (!errors.isEmpty()) {
			log.error("Invalid value : [{}]", String.join(", ", errors));
			return new Response(String.format("Invalid value : [%s]", String.join(", ", errors)),
					HttpStatus.BAD_REQUEST);
		}
		surveyUnitInterviewerLink.forEach(link -> {
			mapSurveyUnit.get(link.getSurveyUnitId()).setInterviewer(mapInterviewer.get(link.getInterviewerId()));
			surveyUnitRepository.save(mapSurveyUnit.get(link.getSurveyUnitId()));
		});
		log.info("{} links Survey-unit/Interviewer created or updated", surveyUnitInterviewerLink.size());
		return new Response(
				String.format("%s links Survey-unit/Interviewer created or updated", surveyUnitInterviewerLink.size()),
				HttpStatus.OK);
	}

	@Override
	public void delete(String surveyUnitId) {
		SurveyUnit surveyUnit = getSurveyUnit(surveyUnitId);
		surveyUnitTempZoneRepository.deleteBySurveyUnitId(surveyUnit.getId());
		surveyUnitRepository.deleteById(surveyUnit.getId());
	}

	@Override
	public void saveSurveyUnitToTempZone(String id, String userId, JsonNode surveyUnit) {
		Long date = new Date().getTime();
		SurveyUnitTempZone surveyUnitTempZoneToSave = new SurveyUnitTempZone(id, userId, date, surveyUnit);
		surveyUnitTempZoneRepository.save(surveyUnitTempZoneToSave);
	}

	@Override
	public List<SurveyUnitTempZone> getAllSurveyUnitTempZone() {
		return surveyUnitTempZoneRepository.findAll();
	}

	@Override
	public List<String> getAllIds() {
		return surveyUnitRepository.findAllIds();
	}

	@Override
	public List<String> getAllIdsByCampaignId(String campaignId) {
		return surveyUnitRepository.findAllIdsByCampaignId(campaignId);
	}

	@Override
	public List<String> getAllIdsByInterviewerId(String interviewerId) {
		return surveyUnitRepository.findAllIdsByInterviewerId(interviewerId);
	}

	@Override
	public void removeInterviewerLink(List<String> ids) {
		surveyUnitRepository.setInterviewer(ids, null);
	}
}
