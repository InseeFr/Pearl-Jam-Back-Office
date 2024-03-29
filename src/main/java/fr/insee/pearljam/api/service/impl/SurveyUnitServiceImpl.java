package fr.insee.pearljam.api.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pearljam.api.domain.*;
import fr.insee.pearljam.api.repository.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.insee.pearljam.api.bussinessrules.BussinessRules;
import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.domain.communication.CommunicationRequest;
import fr.insee.pearljam.api.dto.comment.CommentDto;
import fr.insee.pearljam.api.dto.communication.CommunicationRequestDto;
import fr.insee.pearljam.api.dto.contactoutcome.ContactOutcomeDto;
import fr.insee.pearljam.api.dto.identification.IdentificationDto;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import fr.insee.pearljam.api.dto.person.PersonDto;
import fr.insee.pearljam.api.dto.state.StateDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitCampaignDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitContextDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitDetailDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitInterviewerLinkDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitOkNokDto;
import fr.insee.pearljam.api.exception.BadRequestException;
import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.api.exception.SurveyUnitException;
import fr.insee.pearljam.api.service.IdentificationService;
import fr.insee.pearljam.api.service.SurveyUnitService;
import fr.insee.pearljam.api.service.UserService;
import fr.insee.pearljam.api.service.UtilsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
	private final IdentificationRepository identificationRepository;
	private final IdentificationService identificationService;
	private final UserService userService;
	private final UtilsService utilsService;

	@Override
	public boolean checkHabilitationInterviewer(String userId, String id) {
		return surveyUnitRepository.findByIdAndInterviewerIdIgnoreCase(id, userId).isPresent();
	}

	@Override
	public boolean checkHabilitationReviewer(String userId, String id) {
		List<String> userOUs = userService.getUserOUs(userId, true)
				.stream().map(OrganizationUnitDto::getId)
				.collect(Collectors.toList());

		return !surveyUnitRepository.findByIdInOrganizationalUnit(id, userOUs).isEmpty();
	}

	public Optional<SurveyUnit> findById(String id) {
		return surveyUnitRepository.findById(id);
	}

	public Optional<SurveyUnit> findByIdAndInterviewerIdIgnoreCase(String userId, String id) {
		return surveyUnitRepository.findByIdAndInterviewerIdIgnoreCase(userId, id);
	}

	public SurveyUnitDetailDto getSurveyUnitDetail(String userId, String id)
			throws SurveyUnitException, NotFoundException {
		Optional<SurveyUnit> surveyUnit = surveyUnitRepository.findById(id);
		if (!surveyUnit.isPresent()) {
			throw new NotFoundException(String.format("Survey unit with id %s was not found in database", id));
		}
		if (!canBeSeenByInterviewer(surveyUnit.get().getId())) {
			throw new SurveyUnitException(
					String.format("Survey unit with id %s is not associated to the interviewer %s", id, userId));
		}
		return new SurveyUnitDetailDto(surveyUnit.get());
	}

	public List<SurveyUnitDto> getSurveyUnitDto(String userId, Boolean extended) {
		List<String> surveyUnitDtoIds = null;
		if (userId.equals(GUEST)) {
			surveyUnitDtoIds = surveyUnitRepository.findAllIds();
		} else {
			surveyUnitDtoIds = surveyUnitRepository.findIdsByInterviewerId(userId);
		}
		if (surveyUnitDtoIds.isEmpty()) {
			log.error("No Survey Unit found for interviewer {}", userId);
			return List.of();
		}
		if (userId.equals(GUEST)) {
			return surveyUnitDtoIds.stream()
					.map(idSurveyUnit -> new SurveyUnitDto(surveyUnitRepository.findById(idSurveyUnit).get(),
							visibilityRepository.findVisibilityBySurveyUnitId(idSurveyUnit), extended))
					.collect(Collectors.toList());
		}
		surveyUnitDtoIds = surveyUnitDtoIds.stream().filter(this::canBeSeenByInterviewer)
				.collect(Collectors.toList());

		return surveyUnitDtoIds.stream()
				.map(idSurveyUnit -> new SurveyUnitDto(idSurveyUnit,
						campaignRepository.findDtoBySurveyUnitId(idSurveyUnit),
						visibilityRepository.findVisibilityBySurveyUnitId(idSurveyUnit)))

				.collect(Collectors.toList());
	}

	@Override
	public boolean canBeSeenByInterviewer(String suId) {
		StateDto dto = stateRepository.findFirstDtoBySurveyUnitIdOrderByDateDesc(suId);
		StateType currentState = dto != null ? dto.getType() : null;
		return currentState != null && BussinessRules.stateCanBeSeenByInterviewerBussinessRules(currentState);
	}

	@Transactional
	public ResponseEntity<SurveyUnitDetailDto> updateSurveyUnitDetail(String userId, String id,
			SurveyUnitDetailDto surveyUnitDetailDto) {
		log.info("Update Survey Unit {}", id);
		if (surveyUnitDetailDto == null) {
			log.error("Survey Unit in parameter is null");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		Optional<SurveyUnit> surveyUnitOpt;
		if (userId.equals(GUEST)) {
			surveyUnitOpt = surveyUnitRepository.findById(id);
		} else {
			surveyUnitOpt = surveyUnitRepository.findByIdAndInterviewerIdIgnoreCase(id, userId);
		}
		if (!surveyUnitOpt.isPresent()) {
			log.error(SU_ID_NOT_FOUND_FOR_INTERVIEWER, id, userId);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		SurveyUnit surveyUnit = surveyUnitOpt.get();
		surveyUnit.setMove(surveyUnitDetailDto.isMove());
		updateAddress(surveyUnit, surveyUnitDetailDto);
		try {
			updatePersons(surveyUnitDetailDto);
		} catch (SurveyUnitException e) {
			log.error("Error when updating persons related to survey Unit {} - {}", id, e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		updateComment(surveyUnit, surveyUnitDetailDto);
		updateStates(surveyUnit, surveyUnitDetailDto);
		updateContactAttempt(surveyUnit, surveyUnitDetailDto);
		updateContactOutcome(surveyUnit, surveyUnitDetailDto);
		updateIdentification(surveyUnit, surveyUnitDetailDto);
		updateCommunicationRequest(surveyUnit, surveyUnitDetailDto);
		surveyUnitRepository.save(surveyUnit);
		log.info("Survey Unit {} - update complete", id);
		SurveyUnitDetailDto updatedSurveyUnit = new SurveyUnitDetailDto(surveyUnitRepository.findById(id).get());
		return new ResponseEntity<>(updatedSurveyUnit, HttpStatus.OK);
	}

	private void updateCommunicationRequest(SurveyUnit surveyUnit, SurveyUnitDetailDto surveyUnitDetailDto) {
		if (surveyUnitDetailDto.getCommunicationRequests() != null) {
			List<CommunicationRequestDto> incommingCommRequests = surveyUnitDetailDto.getCommunicationRequests();
			Set<CommunicationRequest> communicationRequests = surveyUnit.getCommunicationRequests();
			Set<CommunicationRequest> newCommunicationsRequests = incommingCommRequests.stream()
					.filter(commRequest -> commRequest.getId() == null)
					.map(dto -> new CommunicationRequest(dto, surveyUnit)).collect(Collectors.toSet());
			communicationRequests.addAll(newCommunicationsRequests);

			// for each communiactionRequest : update state then try to call messhugah if
			// needed
			// communicationRequests.stream()...
			// handle INITIATED -> READY
			// if(shouldAddReadyStatus(incommingCommRequests)) {
			// communicationRequests.add(generateReadyCommunicationRequest(surveyUnit));
			// }
			// TODO : call messhugah to dispatch
			// if(shouldTryToGenerateCommunication(newCommunicationsRequests)){
			// boolean communicationRequestService.generateCommunication(commReq)
			// }
		}
		log.info("Survey Unit {} - communicationRequests updated", surveyUnit.getId());
	}

	private void updateIdentification(SurveyUnit surveyUnit, SurveyUnitDetailDto surveyUnitDetailDto) {
		if (surveyUnitDetailDto.getIdentification() != null) {
			Identification identification = identificationService.findBySurveyUnitId(surveyUnit.getId());
			if (identification == null)
				identification = new Identification();
			IdentificationDto identDto = surveyUnitDetailDto.getIdentification();
			identification.setIdentification(identDto.getIdentification());
			identification.setAccess(identDto.getAccess());
			identification.setSituation(identDto.getSituation());
			identification.setCategory(identDto.getCategory());
			identification.setOccupant(identDto.getOccupant());
			identification.setSurveyUnit(surveyUnit);
			identificationRepository.save(identification);
		}
		log.info("Survey-unit {} - Identification updated", surveyUnit.getId());
	}

	private void updateContactOutcome(SurveyUnit surveyUnit, SurveyUnitDetailDto surveyUnitDetailDto) {
		if (surveyUnitDetailDto.getContactOutcome() != null) {
			ContactOutcome contactOutcome = contactOutcomeRepository.findBySurveyUnit(surveyUnit)
					.orElseGet(ContactOutcome::new);
			contactOutcome.setDate(surveyUnitDetailDto.getContactOutcome().getDate());
			contactOutcome.setType(surveyUnitDetailDto.getContactOutcome().getType());
			contactOutcome.setTotalNumberOfContactAttempts(
					surveyUnitDetailDto.getContactOutcome().getTotalNumberOfContactAttempts());
			contactOutcome.setSurveyUnit(surveyUnit);
			contactOutcomeRepository.save(contactOutcome);
		}
		log.info("Survey-unit {} - Contact outcome updated", surveyUnit.getId());
	}

	private void updateContactAttempt(SurveyUnit surveyUnit, SurveyUnitDetailDto surveyUnitDetailDto) {
		if (surveyUnitDetailDto.getContactAttempts() != null) {
			Set<ContactAttempt> contactAttemps = surveyUnit.getContactAttempts();
			contactAttemps.clear();
			Set<ContactAttempt> newContactAttempts = surveyUnitDetailDto.getContactAttempts().stream()
					.map(dto -> new ContactAttempt(dto, surveyUnit)).collect(Collectors.toSet());
			contactAttemps.addAll(newContactAttempts);
			log.info("Survey-unit {} - Contact attempts updated", surveyUnit.getId());
		}
	}

	private void updateStates(SurveyUnit surveyUnit, SurveyUnitDetailDto surveyUnitDetailDto) {
		if (surveyUnitDetailDto.getStates() != null) {
			surveyUnitDetailDto.getStates().stream()
					.filter(s -> s.getId() == null || !stateRepository.existsById(s.getId()))
					.forEach(s -> stateRepository.save(new State(s.getDate(), surveyUnit, s.getType())));
		}
		StateType currentState = stateRepository.findFirstDtoBySurveyUnitIdOrderByDateDesc(surveyUnit.getId())
				.getType();
		if (currentState == StateType.WFS) {
			addStateAuto(surveyUnit);
		}
		List<StateDto> dbStates = stateRepository.findAllDtoBySurveyUnitIdOrderByDateAsc(surveyUnit.getId());
		if (Boolean.TRUE.equals(BussinessRules.shouldFallBackToTbrOrFin(dbStates))) {
			Set<State> ueStates = surveyUnit.getStates();
			if (ueStates.stream().anyMatch(s -> s.getType() == StateType.FIN)) {
				ueStates.add(new State(new Date().getTime(), surveyUnit, StateType.FIN));
			} else if (ueStates.stream().anyMatch(s -> s.getType() == StateType.TBR)) {
				ueStates.add(new State(new Date().getTime(), surveyUnit, StateType.TBR));
			}
		}
		log.info("Survey-unit {} - States updated", surveyUnit.getId());
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

	private void updateComment(SurveyUnit surveyUnit, SurveyUnitDetailDto surveyUnitDetailDto) {
		if (surveyUnitDetailDto.getComments() != null) {
			Set<Comment> comments = surveyUnit.getComments();
			Set<Comment> commentsToRemove = comments.stream()
					.filter(c -> surveyUnitDetailDto.getComments().stream().anyMatch(c2 -> c2.getType() == c.getType()))
					.collect(Collectors.toSet());
			comments.removeAll(commentsToRemove);
			Set<Comment> newComments = surveyUnitDetailDto.getComments().stream().filter(dto -> dto.getType() != null)
					.map(dto -> new Comment(dto, surveyUnit)).collect(Collectors.toSet());
			comments.addAll(newComments);
			log.info("Survey-unit {} - Comments updated", surveyUnit.getId());
		}
	}

	private void updatePersons(SurveyUnitDetailDto surveyUnitDetailDto) throws SurveyUnitException {
		if (surveyUnitDetailDto.getPersons() != null) {
			for (PersonDto person : surveyUnitDetailDto.getPersons()) {
				if (person.getId() != null) {
					Optional<Person> persOpt = personRepository.findById(person.getId());
					if (persOpt.isPresent()) {
						updatePerson(person, persOpt.get());
					} else {
						throw new SurveyUnitException(
								String.format("Person with id %s was not found in database", person.getId()));
					}
				} else {
					throw new SurveyUnitException("One of the persons in argument has a null id");
				}

			}
			log.info("Survey-unit {} - Persons updated", surveyUnitDetailDto.getId());
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
		if (person.isFavoriteEmail() != null) {
			pers.setFavoriteEmail(person.isFavoriteEmail());
		}
		if (person.isPrivileged() != null) {
			pers.setPrivileged(person.isPrivileged());
		}
		if (person.getPhoneNumbers() != null) {
			Set<PhoneNumber> phoneNumbers = person.getPhoneNumbers().stream().map(pn -> new PhoneNumber(pn, pers))
					.collect(Collectors.toSet());
			pers.getPhoneNumbers().clear();
			pers.getPhoneNumbers().addAll(phoneNumbers);
		}
	}

	private void updateAddress(SurveyUnit surveyUnit, SurveyUnitDetailDto surveyUnitDetailDto) {
		if (surveyUnitDetailDto.getAddress() != null) {
			InseeAddress inseeAddress;
			Optional<InseeAddress> optionalInseeAddress = addressRepository.findById(surveyUnit.getAddress().getId());
			if (!optionalInseeAddress.isPresent()) {
				inseeAddress = new InseeAddress(surveyUnitDetailDto.getAddress());
			} else {
				inseeAddress = optionalInseeAddress.get();
				inseeAddress.setL1(surveyUnitDetailDto.getAddress().getL1());
				inseeAddress.setL2(surveyUnitDetailDto.getAddress().getL2());
				inseeAddress.setL3(surveyUnitDetailDto.getAddress().getL3());
				inseeAddress.setL4(surveyUnitDetailDto.getAddress().getL4());
				inseeAddress.setL5(surveyUnitDetailDto.getAddress().getL5());
				inseeAddress.setL6(surveyUnitDetailDto.getAddress().getL6());
				inseeAddress.setL7(surveyUnitDetailDto.getAddress().getL7());
				inseeAddress.setBuilding(surveyUnitDetailDto.getAddress().getBuilding());
				inseeAddress.setFloor(surveyUnitDetailDto.getAddress().getFloor());
				inseeAddress.setDoor(surveyUnitDetailDto.getAddress().getDoor());
				inseeAddress.setStaircase(surveyUnitDetailDto.getAddress().getStaircase());
				inseeAddress.setElevator(surveyUnitDetailDto.getAddress().isElevator());
				inseeAddress.setCityPriorityDistrict(surveyUnitDetailDto.getAddress().isCityPriorityDistrict());
			}
			// Update Address
			addressRepository.save(inseeAddress);
		}
		log.info("Survey-unit {} - Address updated", surveyUnit.getId());
	}

	@Transactional
	public HttpStatus updateSurveyUnitComment(String userId, String suId, CommentDto comment) {
		Optional<SurveyUnit> surveyUnitOpt = surveyUnitRepository.findById(suId);
		if (!surveyUnitOpt.isPresent()) {
			log.error(SU_ID_NOT_FOUND_FOR_INTERVIEWER, suId, userId);
			return HttpStatus.NOT_FOUND;
		}
		if (comment.getType() == null || comment.getValue() == null) {
			log.error("Some of the required fields (type, value) are missing in request body");
			return HttpStatus.BAD_REQUEST;
		}
		SurveyUnit surveyUnit = surveyUnitOpt.get();
		Set<Comment> comments = surveyUnit.getComments();
		Set<Comment> commentsToRemove = comments.stream().filter(c -> c.getType() == comment.getType())
				.collect(Collectors.toSet());
		comments.removeAll(commentsToRemove);
		comments.add(new Comment(comment, surveyUnit));
		log.info("Comments updated");
		surveyUnitRepository.save(surveyUnit);
		return HttpStatus.OK;
	}

	@Transactional
	public HttpStatus updateSurveyUnitViewed(String userId, String suId) {
		Optional<SurveyUnit> surveyUnitOpt = surveyUnitRepository.findById(suId);
		if (!surveyUnitOpt.isPresent()) {
			log.error(SU_ID_NOT_FOUND_FOR_INTERVIEWER, suId, userId);
			return HttpStatus.NOT_FOUND;
		}
		SurveyUnit surveyUnit = surveyUnitOpt.get();
		surveyUnit.setViewed(true);
		log.info("Viewed updated");
		surveyUnitRepository.save(surveyUnit);
		return HttpStatus.OK;
	}

	public Set<SurveyUnitCampaignDto> getSurveyUnitByCampaign(String campaignId, String userId, String state) {
		List<String> lstOuId = userService.getUserOUs(userId, true).stream().map(OrganizationUnitDto::getId)
				.collect(Collectors.toList());
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

	public List<SurveyUnitCampaignDto> getClosableSurveyUnits(HttpServletRequest request, String userId) {
		List<String> lstOuId = userService.getUserOUs(userId, true).stream().map(OrganizationUnitDto::getId)
				.collect(Collectors.toList());

		List<String> noIdentSurveyUnitIds = surveyUnitRepository
				.findSurveyUnitIdsOfOrganizationUnitsInProcessingPhaseByIdentificationConfiguration(
						System.currentTimeMillis(), lstOuId, IdentificationConfiguration.NOIDENT);
		List<String> iascoSurveyUnitIds = surveyUnitRepository
				.findSurveyUnitIdsOfOrganizationUnitsInProcessingPhaseByIdentificationConfiguration(
						System.currentTimeMillis(), lstOuId, IdentificationConfiguration.IASCO);

		// apply different business rules to select SU
		List<SurveyUnit> noIdentSurveyUnitsToCheck = surveyUnitRepository
				.findClosableNoIdentSurveyUnitId(noIdentSurveyUnitIds);
		List<SurveyUnit> iascoSurveyUnitsToCheck = surveyUnitRepository
				.findClosableIascoSurveyUnitId(iascoSurveyUnitIds);

		// merge lists
		List<SurveyUnit> suToCheck = Stream.concat(noIdentSurveyUnitsToCheck.stream(), iascoSurveyUnitsToCheck.stream())
				.collect(Collectors.toList());

		Map<String, String> mapQuestionnaireStateBySu = Collections.emptyMap();

		try {
			mapQuestionnaireStateBySu = getQuestionnaireStatesFromDataCollection(request,
					suToCheck.stream().map(SurveyUnit::getId).collect(Collectors.toList()));

		} catch (Exception e) {
			log.error("Could not get data collection API : {}", e.getMessage());
			log.error("All questionnaire states will be considered null");
		}

		final Map<String, String> map = mapQuestionnaireStateBySu;

		return suToCheck.stream().map(su -> {
			SurveyUnitCampaignDto sudto = new SurveyUnitCampaignDto(su);
			String identificationResult = identificationService.getIdentificationState(su.getIdentification());
			sudto.setIdentificationState(identificationResult);
			String questionnaireState = Optional.ofNullable(map.get(su.getId())).orElse(Constants.UNAVAILABLE);
			sudto.setQuestionnaireState(questionnaireState);
			return sudto;

		}).filter(this::isClosable)
				.collect(Collectors.toList());

	}

	private boolean isClosable(SurveyUnitCampaignDto sudto) {

		boolean hasQuestionnaire = !Constants.UNAVAILABLE.equals(sudto.getQuestionnaireState());

		ContactOutcomeDto outcome = sudto.getContactOutcome();
		if (outcome == null)
			return !hasQuestionnaire;
		switch (outcome.getType()) {
			case INA:
			case NOA:
				return !hasQuestionnaire;
			default:
				return true;
		}
	}

	private Map<String, String> getQuestionnaireStatesFromDataCollection(HttpServletRequest request,
			List<String> lstSu) {
		ResponseEntity<SurveyUnitOkNokDto> result = utilsService.getQuestionnairesStateFromDataCollection(request,
				lstSu);
		log.info("GET state from data collection service call resulting in {}", result.getStatusCode());
		SurveyUnitOkNokDto object = result.getBody();
		HttpStatus responseCode = result.getStatusCode();

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
			StateType currentState = stateRepository.findFirstDtoBySurveyUnitOrderByDateDesc(su.get()).getType();
			if (Boolean.TRUE.equals(BussinessRules.stateCanBeModifiedByManager(currentState, state))) {
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
			StateType currentState = stateRepository.findFirstDtoBySurveyUnitOrderByDateDesc(su.get()).getType();
			if (currentState.equals(StateType.CLO)) {
				addOrModifyClosingCause(surveyUnit, type);
				return HttpStatus.OK;
			} else if (Boolean.TRUE.equals(BussinessRules.stateCanBeModifiedByManager(currentState, StateType.CLO))) {
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

	@Transactional
	public void addOrModifyClosingCause(SurveyUnit surveyUnit, ClosingCauseType type) {
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
		if (!su.isPresent()) {
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
						.collect(Collectors.toList()))
				.stream().collect(Collectors.toMap(Campaign::getId, c -> c));
		Map<String, OrganizationUnit> mapOrganizationUnits = organizationUnitRepository.findAllById(
				surveyUnits.stream()
						.map(SurveyUnitContextDto::getOrganizationUnitId)
						.collect(Collectors.toList()))
				.stream().collect(Collectors.toMap(OrganizationUnit::getId, gl -> gl));
		surveyUnits.stream().forEach(su -> {
			if (!duplicates.containsKey(su.getId())) {
				duplicates.put(su.getId(), 0);
			}
			duplicates.put(su.getId(), duplicates.get(su.getId()) + 1);
			if (surveyUnitsDb.contains(su.getId())) {
				duplicates.put(su.getId(), duplicates.get(su.getId()) + 1);
			}
			if (!su.isValid()
					|| !mapOrganizationUnits.containsKey(su.getOrganizationUnitId())
					|| !mapCampaigns.containsKey(su.getCampaign())) {
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

	@Override
	@Transactional
	public Response createSurveyUnitInterviewerLinks(List<SurveyUnitInterviewerLinkDto> surveyUnitInterviewerLink) {

		// Get SurveyUnits and Interviewers to create
		Map<String, SurveyUnit> mapSurveyUnit = surveyUnitRepository
				.findAllById(surveyUnitInterviewerLink.stream()
						.map(SurveyUnitInterviewerLinkDto::getSurveyUnitId)
						.collect(Collectors.toList()))
				.stream().collect(Collectors.toMap(SurveyUnit::getId, su -> su));
		Map<String, Interviewer> mapInterviewer = interviewerRepository
				.findAllById(surveyUnitInterviewerLink.stream()
						.map(SurveyUnitInterviewerLinkDto::getInterviewerId)
						.collect(Collectors.toList()))
				.stream().collect(Collectors.toMap(Interviewer::getId, itw -> itw));

		// Create new assignment
		List<String> errors = surveyUnitInterviewerLink.stream()
				.filter(link -> !link.isValid()
						|| !mapSurveyUnit.containsKey(link.getSurveyUnitId())
						|| !mapInterviewer.containsKey(link.getInterviewerId()))
				.map(SurveyUnitInterviewerLinkDto::getLink).collect(Collectors.toList());
		if (!errors.isEmpty()) {
			log.error("Invalid value : [{}]", String.join(", ", errors));
			return new Response(String.format("Invalid value : [%s]", String.join(", ", errors)),
					HttpStatus.BAD_REQUEST);
		}
		surveyUnitInterviewerLink.stream().forEach(link -> {
			mapSurveyUnit.get(link.getSurveyUnitId()).setInterviewer(mapInterviewer.get(link.getInterviewerId()));
			surveyUnitRepository.save(mapSurveyUnit.get(link.getSurveyUnitId()));
		});
		log.info("{} links Survey-unit/Interviewer created or updated", surveyUnitInterviewerLink.size());
		return new Response(
				String.format("%s links Survey-unit/Interviewer created or updated", surveyUnitInterviewerLink.size()),
				HttpStatus.OK);
	}

	@Override
	public void delete(SurveyUnit surveyUnit) {
		surveyUnitTempZoneRepository.deleteBySurveyUnitId(surveyUnit.getId());
		surveyUnitRepository.delete(surveyUnit);
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
