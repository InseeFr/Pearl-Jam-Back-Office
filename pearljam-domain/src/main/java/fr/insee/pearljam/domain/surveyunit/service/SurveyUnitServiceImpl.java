package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.contracts.campaign.dto.output.CommunicationTemplateResponseDto;
import fr.insee.pearljam.contracts.organizationunit.dto.OrganizationUnitDto;
import fr.insee.pearljam.contracts.surveyunit.dto.closable.ClosableSurveyUnitDto;
import fr.insee.pearljam.contracts.surveyunit.dto.contacthistory.NextContactHistoryDto;
import fr.insee.pearljam.contracts.surveyunit.dto.contacthistory.PreviousContactHistoryDto;
import fr.insee.pearljam.contracts.surveyunit.dto.identification.IdentificationDto;
import fr.insee.pearljam.contracts.surveyunit.dto.person.PersonDto;
import fr.insee.pearljam.contracts.surveyunit.dto.state.StateDto;
import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.*;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationTemplate;
import fr.insee.pearljam.domain.campaign.port.in.CommunicationTemplateService;
import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.campaign.port.out.CampaignRepository;
import fr.insee.pearljam.domain.campaign.port.out.VisibilityRepository;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.port.out.OrganizationUnitRepository;
import fr.insee.pearljam.domain.shared.model.Response;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitService;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitUpdateService;
import fr.insee.pearljam.domain.surveyunit.port.out.*;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitCandidateView;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitView;
import fr.insee.pearljam.domain.surveyunit.port.out.view.SurveyUnitCampaignView;
import fr.insee.pearljam.domain.surveyunit.service.exception.SurveyUnitNotFoundException;
import fr.insee.pearljam.domain.surveyunit.service.model.SurveyUnitForInterviewer;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CampaignDB;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.OrganizationUnitDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static fr.insee.pearljam.contracts.constants.Constants.QUESTIONNAIRE_STATE_UNAVAILABLE;

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
	private final StateRepository stateRepository;
	private final InterviewerRepository interviewerRepository;
	private final CampaignRepository campaignRepository;
	private final OrganizationUnitRepository organizationUnitRepository;
	private final VisibilityRepository visibilityRepository;
	private final ClosingCauseRepository closingCauseRepository;
	private final UserService userService;
	private final QuestionnaireStateClient questionnaireStateClient;
	private final SurveyUnitUpdateService surveyUnitUpdateService;
	private final CommunicationTemplateService communicationTemplateService;
	private final DateService dateService;
	private final JsonMapper jsonMapper;

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
	public SurveyUnitDB getSurveyUnit(String surveyUnitId) {
		return surveyUnitRepository
				.findById(surveyUnitId)
				.orElseThrow(() -> new SurveyUnitNotFoundException(surveyUnitId));
	}

	@Override
	public SurveyUnitInterviewerResponseDto buildSurveyUnitInterviewerResponse(SurveyUnitDB surveyUnit) {
		List<CommunicationTemplate> communicationTemplates =
				communicationTemplateService.findCommunicationTemplates(surveyUnit.getCampaign().getId());

		SurveyUnitForInterviewer surveyUnitForInterviewer = new SurveyUnitForInterviewer(surveyUnit, communicationTemplates);

		return toSurveyUnitInterviewerResponseDto(surveyUnitForInterviewer);
	}


	@Override
	public SurveyUnitInterviewerResponseDto getSurveyUnitInterviewerDetail(String userId, String surveyUnitId) {
		SurveyUnitDB surveyUnit = surveyUnitRepository
				.findByIdAndInterviewerIdIgnoreCase(surveyUnitId, userId)
				.orElseThrow(() -> {
					log.error("Survey unit with id {} is not associated to the interviewer {}", surveyUnitId, userId);
					return new SurveyUnitNotFoundException(surveyUnitId);
				});

		if (!canBeSeenByInterviewer(surveyUnit.getId())) {
			log.error("Survey unit with id {} is not associated to the interviewer {} anymore", surveyUnitId, userId);
			throw new SurveyUnitNotFoundException(surveyUnitId);
		}

		return buildSurveyUnitInterviewerResponse(surveyUnit);
	}

	@Override
	public SurveyUnitInterviewerResponseDto getSurveyUnitDetail(String surveyUnitId) {
		SurveyUnitDB surveyUnit = surveyUnitRepository
				.findById(surveyUnitId)
				.orElseThrow(() -> {
					log.error("Survey unit with id {} does not exist", surveyUnitId);
					return new SurveyUnitNotFoundException(surveyUnitId);
				});

		return buildSurveyUnitInterviewerResponse(surveyUnit);
	}

	public List<SurveyUnitDto> getSurveyUnitDto(String userId, Boolean extended) {
		long now = dateService.getCurrentTimestamp();

		List<String> visibleTypes =
				StateBusinessRules.statesVisibleToInterviewer().stream()
						.map(Enum::name)
						.toList();
		List<String> surveyUnitDtoIds = surveyUnitRepository.findIdsByInterviewerIdWithinVisibilityScope(userId, now, visibleTypes);

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
		return currentState != null && StateBusinessRules.stateCanBeSeenByInterviewerBussinessRules(currentState);
	}

	@Transactional
	public SurveyUnitDetailDto updateSurveyUnit(String userId, String surveyUnitId,
	                                            SurveyUnitUpdateDto surveyUnitUpdate)  {
		log.info("Update Survey Unit {}", surveyUnitId);

		Optional<SurveyUnitDB> surveyUnitOpt;
		if (userId.equals(GUEST)) {
			surveyUnitOpt = surveyUnitRepository.findById(surveyUnitId);
		} else {
			surveyUnitOpt = surveyUnitRepository.findByIdAndInterviewerIdIgnoreCase(surveyUnitId, userId);
		}
		SurveyUnitDB surveyUnit = surveyUnitOpt.orElseThrow(() -> new SurveyUnitNotFoundException(surveyUnitId));
		surveyUnit.setMove(surveyUnitUpdate.move());
		updateAddress(surveyUnit, surveyUnitUpdate);

		updateStates(surveyUnit, surveyUnitUpdate);
		updateContactAttempt(surveyUnit, surveyUnitUpdate);

		surveyUnitUpdateService.updateSurveyUnitInfos(surveyUnit, surveyUnitUpdate);
		surveyUnitRepository.save(surveyUnit);

		log.info("Survey Unit {} - update complete", surveyUnitId);
		return toSurveyUnitDetailDto(surveyUnitRepository.findById(surveyUnitId).orElseThrow(() -> new SurveyUnitNotFoundException(surveyUnitId)));
	}

	private SurveyUnitInterviewerResponseDto toSurveyUnitInterviewerResponseDto(SurveyUnitForInterviewer surveyUnitForInterviewer) {
		SurveyUnitDB surveyUnit = surveyUnitForInterviewer.surveyUnit();
		List<PersonDto> persons = surveyUnit.getModelPersons().stream()
				.map(PersonDto::fromModel)
				.toList();
		List<CommentDto> comments = CommentDto.fromModel(surveyUnit.getModelComments());
		List<ContactAttemptDto> contactAttempts = surveyUnit.getContactAttempts().stream()
				.map(contactAttempt -> new ContactAttemptDto(contactAttempt.getDate(), contactAttempt.getStatus(), contactAttempt.getMedium()))
				.toList();
		List<StateDto> states = surveyUnit.getStates().stream()
				.sorted(Comparator.comparing(StateDB::getDate, Comparator.nullsLast(Comparator.reverseOrder())))
				.filter(state -> StateBusinessRules.stateCanBeSeenByInterviewerBussinessRules(state.getType()))
				.map(state -> new StateDto(state.getId(), state.getDate(), state.getType()))
				.toList();
		ContactOutcomeDto contactOutcome = surveyUnit.getContactOutcome() == null
				? null
				: ContactOutcomeDto.fromModel(surveyUnit.getModelContactOutcome());

		return new SurveyUnitInterviewerResponseDto(
				surveyUnit.getId(),
				surveyUnit.getDisplayName(),
				persons,
				toAddressDto(surveyUnit),
				surveyUnit.isPriority(),
				surveyUnit.getMove(),
				surveyUnit.getCampaign().getId(),
				comments,
				toSampleIdentifiersDto(surveyUnit),
				states,
				contactAttempts,
				contactOutcome,
				IdentificationDto.fromModel(surveyUnit.getModelIdentification()),
				CommunicationTemplateResponseDto.fromModel(surveyUnitForInterviewer.communicationTemplates()),
				CommunicationRequestResponseDto.fromModel(surveyUnit.getModelCommunicationRequests()),
				PreviousContactHistoryDto.fromModel(surveyUnit.getPreviousContactHistory()),
				NextContactHistoryDto.fromModel(surveyUnit.getNextContactHistory()));
	}

	private SurveyUnitDetailDto toSurveyUnitDetailDto(SurveyUnitDB surveyUnit) {
		SurveyUnitDetailDto detailDto = new SurveyUnitDetailDto();
		detailDto.setId(surveyUnit.getId());
		detailDto.setPersons(surveyUnit.getPersons().stream()
				.filter(person -> person.getContactHistoryType() == null)
				.map(person -> PersonDB.toModel(person, null))
				.map(PersonDto::fromModel)
				.toList());
		detailDto.setAddress(toAddressDto(surveyUnit));
		detailDto.setPriority(surveyUnit.isPriority());
		detailDto.setMove(surveyUnit.getMove());
		detailDto.setCampaign(surveyUnit.getCampaign().getId());
		detailDto.setComments(CommentDto.fromModel(surveyUnit.getModelComments()));
		detailDto.setSampleIdentifiers(toSampleIdentifiersDto(surveyUnit));
		detailDto.setStates(surveyUnit.getStates().stream()
				.sorted(Comparator.comparing(StateDB::getDate, Comparator.nullsLast(Comparator.reverseOrder())))
				.filter(state -> StateBusinessRules.stateCanBeSeenByInterviewerBussinessRules(state.getType()))
				.map(state -> new StateDto(state.getId(), state.getDate(), state.getType()))
				.toList());
		detailDto.setContactAttempts(surveyUnit.getContactAttempts().stream()
				.map(contactAttempt -> new ContactAttemptDto(contactAttempt.getDate(), contactAttempt.getStatus(), contactAttempt.getMedium()))
				.toList());
		detailDto.setContactOutcome(surveyUnit.getContactOutcome() == null ? null : ContactOutcomeDto.fromModel(surveyUnit.getModelContactOutcome()));
		detailDto.setIdentification(IdentificationDto.fromModel(surveyUnit.getModelIdentification()));
		detailDto.setCommunicationRequests(CommunicationRequestResponseDto.fromModel(surveyUnit.getModelCommunicationRequests()));
		detailDto.setPreviousContactHistory(PreviousContactHistoryDto.fromModel(surveyUnit.getPreviousContactHistory()));
		detailDto.setNextContactHistory(NextContactHistoryDto.fromModel(surveyUnit.getNextContactHistory()));
		return detailDto;
	}

	private AddressDto toAddressDto(SurveyUnitDB surveyUnit) {
		if (surveyUnit.getAddress() instanceof InseeAddressDB addressDB) {
			return new AddressDto(
					addressDB.getL1(),
					addressDB.getL2(),
					addressDB.getL3(),
					addressDB.getL4(),
					addressDB.getL5(),
					addressDB.getL6(),
					addressDB.getL7(),
					addressDB.getElevator(),
					addressDB.getBuilding(),
					addressDB.getFloor(),
					addressDB.getDoor(),
					addressDB.getStaircase(),
					addressDB.getCityPriorityDistrict());
		}
		return null;
	}

	private SampleIdentifiersDto toSampleIdentifiersDto(SurveyUnitDB surveyUnit) {
		if (surveyUnit.getSampleIdentifier() instanceof InseeSampleIdentifierDB sampleIdentifierDB) {
			return new SampleIdentifiersDto(
					sampleIdentifierDB.getBs(),
					sampleIdentifierDB.getEc(),
					sampleIdentifierDB.getLe(),
					sampleIdentifierDB.getNoi(),
					sampleIdentifierDB.getNumfa(),
					sampleIdentifierDB.getRges(),
					sampleIdentifierDB.getSsech(),
					sampleIdentifierDB.getNolog(),
					sampleIdentifierDB.getNole(),
					sampleIdentifierDB.getAutre(),
					sampleIdentifierDB.getNograp());
		}
		return null;
	}

	private void updateContactAttempt(SurveyUnitDB surveyUnit, SurveyUnitUpdateDto surveyUnitUpdateDto) {
		if (surveyUnitUpdateDto.contactAttempts() != null) {
			Set<ContactAttemptDB> contactAttemps = surveyUnit.getContactAttempts();
			contactAttemps.clear();
			Set<ContactAttemptDB> newContactAttempts = surveyUnitUpdateDto.contactAttempts().stream()
					.map(dto -> new ContactAttemptDB(dto, surveyUnit)).collect(Collectors.toSet());
			contactAttemps.addAll(newContactAttempts);
			log.info("Survey-unit {} - Contact attempts updated", surveyUnit.getId());
		}
	}

	private void updateStates(SurveyUnitDB surveyUnit, SurveyUnitUpdateDto surveyUnitUpdateDto) {
		if (surveyUnitUpdateDto.states() != null) {
			surveyUnitUpdateDto.states().stream()
					.filter(s -> s.id() == null || !stateRepository.existsById(s.id()))
					.forEach(s -> stateRepository.save(new StateDB(s.date(), surveyUnit, s.type())));
		}
		StateType currentState = stateRepository.findFirstDtoBySurveyUnitIdOrderByDateDesc(surveyUnit.getId())
				.type();
		if (currentState == StateType.WFS) {
			addStateAuto(surveyUnit);
		}
		List<StateDto> dbStates = stateRepository.findAllDtoBySurveyUnitIdOrderByDateAsc(surveyUnit.getId());
		if (StateBusinessRules.shouldFallBackToTbrOrFin(dbStates)) {
			Set<StateDB> ueStates = surveyUnit.getStates();
			if (ueStates.stream().anyMatch(s -> s.getType() == StateType.FIN)) {
				ueStates.add(new StateDB(new Date().getTime(), surveyUnit, StateType.FIN));
			} else if (ueStates.stream().anyMatch(s -> s.getType() == StateType.TBR)) {
				ueStates.add(new StateDB(new Date().getTime(), surveyUnit, StateType.TBR));
			}
		}
	}

	private void addStateAuto(SurveyUnitDB surveyUnit) {
		if (surveyUnitRepository.findCountUeINATBRByInterviewerIdAndCampaignId(surveyUnit.getInterviewer().getId(),
				surveyUnit.getCampaign().getId(), surveyUnit.getId()) < 5) {
			stateRepository.save(new StateDB(new Date().getTime(), surveyUnit, StateType.TBR));
			surveyUnit.setClosingCause(null);
		} else {
			stateRepository.save(new StateDB(new Date().getTime(), surveyUnit, StateType.FIN));
			surveyUnit.setClosingCause(null);
		}
	}

	private void updateAddress(SurveyUnitDB surveyUnit, SurveyUnitUpdateDto surveyUnitUpdateDto) {
		if (surveyUnitUpdateDto.address() != null) {
			InseeAddressDB inseeAddress;
			Optional<InseeAddressDB> optionalInseeAddress = addressRepository.findById(surveyUnit.getAddress().getId());
			if (optionalInseeAddress.isEmpty()) {
				inseeAddress = new InseeAddressDB(surveyUnitUpdateDto.address());
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
		Optional<SurveyUnitDB> surveyUnitOpt = surveyUnitRepository.findById(suId);
		if (surveyUnitOpt.isEmpty()) {
			log.error(SU_ID_NOT_FOUND_FOR_INTERVIEWER, suId, userId);
			return HttpStatus.NOT_FOUND;
		}
		SurveyUnitDB surveyUnit = surveyUnitOpt.get();
		surveyUnit.setViewed(true);
		surveyUnitRepository.save(surveyUnit);
		return HttpStatus.OK;
	}

	public Set<SurveyUnitCampaignDto> getSurveyUnitByCampaign(String campaignId, String userId, StateType state) {
		List<String> lstOuId = userService.getUserOUs(userId, true)
				.stream()
				.map(OrganizationUnitDto::getId)
				.toList();

		Set<SurveyUnitCampaignView> surveyUnits = switch(state) {
			case FIN -> surveyUnitRepository.findFinalizedByCampaignIdAndOrganizationUnitIdIn(campaignId,
					lstOuId);
			case CLO -> surveyUnitRepository.findClosedByCampaignIdAndOrganizationUnitIdIn(campaignId,
					lstOuId);
			case null -> surveyUnitRepository.findByCampaignIdAndOrganizationUnitIdIn(campaignId,
					lstOuId);
			default -> surveyUnitRepository.findByCampaignIdAndStateAndOrganizationUnitIdIn(campaignId,
					lstOuId, state.name());
		};

		if (surveyUnits.isEmpty()) {
			log.warn("No Survey Unit found for the user {}", userId);
		}
		return surveyUnits
				.stream()
				.map(SurveyUnitCampaignDto::from)
				.collect(Collectors.toSet());
	}

	@Transactional(readOnly = true)
	public List<ClosableSurveyUnitDto> getClosableSurveyUnits(
			String userId) {

		List<String> lstOuIds = userService.getUserOUs(userId, true).stream()
				.map(OrganizationUnitDto::getId)
				.toList();

		long now = dateService.getCurrentTimestamp();

		List<ClosableSurveyUnitCandidateView> candidates =
				surveyUnitRepository.findClosableCandidates(now, null, lstOuIds);

		if (candidates.isEmpty()) {
			return List.of();
		}

		Map<String, ClosableSurveyUnitCandidateView> candidatesById =
				candidates.parallelStream()
						.collect(Collectors.toMap(
								ClosableSurveyUnitCandidateView::getId,
								Function.identity()
						));

		final Map<String, String> questionnaireStates = getQuestionnaireStatesFromDataCollection(candidatesById.keySet());

		Map<String, ClosableSurveyUnitCandidateView> eligibleSurveyUnitsById =
				candidates.parallelStream()
						.filter(candidate -> isClosable(candidate, questionnaireStates.get(candidate.getId())))
						.collect(Collectors.toMap(
								ClosableSurveyUnitCandidateView::getId,
								Function.identity()
						));

		List<ClosableSurveyUnitView> closableSurveyUnitProjections =
				surveyUnitRepository.findClosableSurveyUnits(eligibleSurveyUnitsById.keySet());

		return closableSurveyUnitProjections
				.parallelStream()
				.map(closableSurveyUnitProjection -> {
					String surveyUnitId = closableSurveyUnitProjection.getId();
					return ClosableSurveyUnitDto.from(
							candidatesById.get(surveyUnitId),
							closableSurveyUnitProjection,
							questionnaireStates.get(surveyUnitId) == null ? QUESTIONNAIRE_STATE_UNAVAILABLE : questionnaireStates.get(surveyUnitId)
					);
				})
				.toList();
	}

	private boolean isClosable(ClosableSurveyUnitCandidateView candidate, String questionnaireState) {
		StateType currentState = candidate.getCurrentStateType();
		ContactOutcomeType outcomeType = candidate.getContactOutcomeType();

		boolean neverTransmitted =
				currentState != null
						&& !Set.of(StateType.TBR, StateType.FIN, StateType.CLO).contains(currentState);

		boolean inaWithoutQuestionnaire =
				outcomeType == ContactOutcomeType.INA
						&& (questionnaireState == null || QUESTIONNAIRE_STATE_UNAVAILABLE.equals(questionnaireState));

		return neverTransmitted || inaWithoutQuestionnaire;
	}


	private Map<String, String> getQuestionnaireStatesFromDataCollection(
			Set<String> lstSu) {
		Map<String, String> mapResult = new HashMap<>();
		try {
			ResponseEntity<InterrogationOkNokDto> result = questionnaireStateClient.getQuestionnairesStateFromDataCollection(lstSu);
			log.info("GET state from data collection service call resulting in {}", result.getStatusCode());
			InterrogationOkNokDto object = result.getBody();
			HttpStatusCode responseCode = result.getStatusCode();

			if (!responseCode.equals(HttpStatus.OK)) {
				String code = responseCode.toString();
				log.error("Data collection API responded with error code {}", code);
			}
			if (object == null) {
				log.error("Could not get response from data collection API");
				throw new IllegalStateException("Could not get response from data collection API");
			}
			object.interrogationNOK().forEach(su -> mapResult.put(su.id(), QUESTIONNAIRE_STATE_UNAVAILABLE));
			object.interrogationOK().forEach(su -> mapResult.put(su.id(), su.stateData().getState()));
		} catch (Exception e) {
			log.error("Could not get data collection API : {}", e.getMessage());
			log.error("All questionnaire states will be considered null");
			lstSu.forEach(id -> mapResult.put(id, QUESTIONNAIRE_STATE_UNAVAILABLE) );
		}
		return mapResult;
	}

	/**
	 * @deprecated still used by CPIES + Sabiane Management V1, replaced by in SurveyUnitStatePort
	 *
	 */
	@Transactional
	@Deprecated(forRemoval = true)
	public HttpStatus addStateToSurveyUnit(String surveyUnitId, StateType state) {
		Optional<SurveyUnitDB> su = surveyUnitRepository.findById(surveyUnitId);
		if (su.isPresent()) {
			StateType currentState = stateRepository.findFirstDtoBySurveyUnitOrderByDateDesc(su.get()).type();
			if (StateBusinessRules.stateCanBeModifiedByManager(currentState, state)) {
				if (StateType.TBR.equals(state) || StateType.FIN.equals(state)) {
					log.info("Deleting closing causes of survey unit {}", surveyUnitId);
					closingCauseRepository.deleteBySurveyUnitId(surveyUnitId);
				}
				stateRepository.save(new StateDB(new Date().getTime(), su.get(), state));
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
		Optional<SurveyUnitDB> su = surveyUnitRepository.findById(surveyUnitId);

		if (su.isPresent()) {
			SurveyUnitDB surveyUnit = su.get();
			log.info("{} -> {}", surveyUnitId, type);
			StateType currentState = stateRepository.findFirstDtoBySurveyUnitIdOrderByDateDesc(surveyUnitId).type();
			if (currentState.equals(StateType.CLO)) {
				addOrModifyClosingCause(surveyUnit, type);
				return HttpStatus.OK;
			} else if (StateBusinessRules.stateCanBeModifiedByManager(currentState, StateType.CLO)) {
				stateRepository.save(new StateDB(dateService.getCurrentTimestamp(), su.get(), StateType.CLO));
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
		Optional<SurveyUnitDB> su = surveyUnitRepository.findById(surveyUnitId);
		if (su.isPresent()) {
			SurveyUnitDB surveyUnit = su.get();
			addOrModifyClosingCause(surveyUnit, type);
			return HttpStatus.OK;
		} else {
			log.error(SU_ID_NOT_FOUND, surveyUnitId);
			return HttpStatus.NOT_FOUND;
		}
	}

	private void addOrModifyClosingCause(SurveyUnitDB surveyUnit, ClosingCauseType type) {
		ClosingCauseDB cc;
		if (surveyUnit.getClosingCause() != null) {
			cc = surveyUnit.getClosingCause();
		} else {
			cc = new ClosingCauseDB();
			cc.setSurveyUnit(surveyUnit);
		}
		cc.setDate(dateService.getCurrentTimestamp());
		cc.setType(type);

		surveyUnit.setClosingCause(cc);
		surveyUnitRepository.save(surveyUnit);
	}

	public List<StateDto> getListStatesBySurveyUnitId(String suId) {
		Optional<SurveyUnitDB> su = surveyUnitRepository.findById(suId);
		if (su.isEmpty()) {
			log.error("SU {} not found in database", suId);
			return List.of();
		}
		return stateRepository.findAllDtoBySurveyUnitIdOrderByDateAsc(suId);

	}

	@Override
	public Response createSurveyUnits(List<SurveyUnitCreationDto> surveyUnits) {
		// Check duplicate line in interviewers to create
		Map<String, Integer> duplicates = new HashMap<>();
		List<String> surveyUnitErrors = new ArrayList<>();
		List<SurveyUnitDB> listSurveyUnits = new ArrayList<>();
		List<String> surveyUnitsDb = surveyUnitRepository.findAllIds();
		Map<String, CampaignDB> mapCampaigns = campaignRepository.findAllById(
						surveyUnits.stream()
								.map(SurveyUnitCreationDto::getCampaign)
								.toList())
				.stream().collect(Collectors.toMap(CampaignDB::getId, c -> c));
		Map<String, OrganizationUnitDB> mapOrganizationUnits = organizationUnitRepository.findAllById(
						surveyUnits.stream()
								.map(SurveyUnitCreationDto::getOrganizationUnitId)
								.toList())
				.stream().collect(Collectors.toMap(OrganizationUnitDB::getId, gl -> gl));
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
			listSurveyUnits.add(new SurveyUnitDB(su, mapOrganizationUnits.get(su.getOrganizationUnitId()),
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

	private boolean checkValidity(SurveyUnitCreationDto su, Map<String, OrganizationUnitDB> ous,
	                              Map<String, CampaignDB> camps) {
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
		Map<String, SurveyUnitDB> mapSurveyUnit = surveyUnitRepository
				.findAllById(surveyUnitInterviewerLink.stream()
						.map(SurveyUnitInterviewerLinkDto::getSurveyUnitId)
						.toList())
				.stream().collect(Collectors.toMap(SurveyUnitDB::getId, su -> su));
		Map<String, InterviewerDB> mapInterviewer = interviewerRepository
				.findAllById(surveyUnitInterviewerLink.stream()
						.map(SurveyUnitInterviewerLinkDto::getInterviewerId)
						.toList())
				.stream().collect(Collectors.toMap(InterviewerDB::getId, itw -> itw));

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
		SurveyUnitDB surveyUnit = getSurveyUnit(surveyUnitId);
		surveyUnitTempZoneRepository.deleteBySurveyUnitId(surveyUnit.getId());
		surveyUnitRepository.deleteById(surveyUnit.getId());
	}

	@Override
	public void saveSurveyUnitToTempZone(String id, String userId, JsonNode surveyUnit) {
		Long date = new Date().getTime();
		String jsonSurveyUnit = jsonMapper.writeValueAsString(surveyUnit);
		SurveyUnitTempZoneDB surveyUnitTempZoneToSave = new SurveyUnitTempZoneDB(id, userId, date, jsonSurveyUnit);
		surveyUnitTempZoneRepository.save(surveyUnitTempZoneToSave);
	}

	@Override
	public List<SurveyUnitTempZoneDB> getAllSurveyUnitTempZone() {
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

	@Override
	public List<SurveyUnitInterviewerResponseDto> getSurveyUnitsDetails(List<String> surveyUnitIds) {
		return surveyUnitRepository
				.findAllById(surveyUnitIds)
				.stream()
				.map(this::buildSurveyUnitInterviewerResponse)
				.toList();
	}
}
