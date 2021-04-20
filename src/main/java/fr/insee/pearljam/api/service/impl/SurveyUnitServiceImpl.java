package fr.insee.pearljam.api.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.insee.pearljam.api.bussinessrules.BussinessRules;
import fr.insee.pearljam.api.domain.ClosingCause;
import fr.insee.pearljam.api.domain.ClosingCauseType;
import fr.insee.pearljam.api.domain.Comment;
import fr.insee.pearljam.api.domain.ContactAttempt;
import fr.insee.pearljam.api.domain.ContactOutcome;
import fr.insee.pearljam.api.domain.InseeAddress;
import fr.insee.pearljam.api.domain.Person;
import fr.insee.pearljam.api.domain.PhoneNumber;
import fr.insee.pearljam.api.domain.State;
import fr.insee.pearljam.api.domain.StateType;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.comment.CommentDto;
import fr.insee.pearljam.api.dto.geographicallocation.GeographicalLocationDto;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import fr.insee.pearljam.api.dto.person.PersonDto;
import fr.insee.pearljam.api.dto.state.StateDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitCampaignDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitDetailDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitDto;
import fr.insee.pearljam.api.exception.SurveyUnitException;
import fr.insee.pearljam.api.repository.AddressRepository;
import fr.insee.pearljam.api.repository.CampaignRepository;
import fr.insee.pearljam.api.repository.ClosingCauseRepository;
import fr.insee.pearljam.api.repository.CommentRepository;
import fr.insee.pearljam.api.repository.ContactAttemptRepository;
import fr.insee.pearljam.api.repository.ContactOutcomeRepository;
import fr.insee.pearljam.api.repository.GeographicalLocationRepository;
import fr.insee.pearljam.api.repository.InterviewerRepository;
import fr.insee.pearljam.api.repository.OrganizationUnitRepository;
import fr.insee.pearljam.api.repository.PersonRepository;
import fr.insee.pearljam.api.repository.SampleIdentifierRepository;
import fr.insee.pearljam.api.repository.StateRepository;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.api.repository.UserRepository;
import fr.insee.pearljam.api.repository.VisibilityRepository;
import fr.insee.pearljam.api.service.SurveyUnitService;
import fr.insee.pearljam.api.service.UserService;
import fr.insee.pearljam.api.service.UtilsService;

/**
 * @author scorcaud
 *
 */
@Service
@Transactional
public class SurveyUnitServiceImpl implements SurveyUnitService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SurveyUnitServiceImpl.class);

	private static final String GUEST = "GUEST";

	@Autowired
	SurveyUnitRepository surveyUnitRepository;

	@Autowired
	AddressRepository addressRepository;

	@Autowired
	SampleIdentifierRepository sampleIdentifierRepository;

	@Autowired
	CommentRepository commentRepository;

	@Autowired
	ContactAttemptRepository contactAttemptRepository;

	@Autowired
	ContactOutcomeRepository contactOutcomeRepository;

	@Autowired
	StateRepository stateRepository;

	@Autowired
	GeographicalLocationRepository geographicalLocationRepository;

	@Autowired
	InterviewerRepository interviewerRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	CampaignRepository campaignRepository;

	@Autowired
	OrganizationUnitRepository ouRepository;

	@Autowired
	VisibilityRepository visibilityRepository;

	@Autowired
	PersonRepository personRepository;
	
	@Autowired
	ClosingCauseRepository closingCauseRepository;

	@Autowired
	UserService userService;

	@Autowired
	UtilsService utilsService;
  
  public boolean checkHabilitation(String userId, String id) {
    return surveyUnitRepository.findByIdAndInterviewerIdIgnoreCase(id, userId).isPresent();
  }
	public Optional<SurveyUnit> findById(String id) {
		return surveyUnitRepository.findById(id);
	}

	public Optional<SurveyUnit> findByIdAndInterviewerIdIgnoreCase(String userId, String id) {
		return surveyUnitRepository.findByIdAndInterviewerIdIgnoreCase(userId, id);
	}

	public SurveyUnitDetailDto getSurveyUnitDetail(String userId, String suId) {
		Optional<SurveyUnit> surveyUnit = findById(suId);
		if (!surveyUnit.isPresent()) {
			return null;
		}
		if (!canBeSeenByInterviewer(surveyUnit.get().getId())) {
			return null;
		}
		SurveyUnitDetailDto surveyUnitDetailDto = new SurveyUnitDetailDto(surveyUnit.get());

		surveyUnitDetailDto.setAddress(addressRepository.findDtoById(surveyUnit.get().getAddress().getId()));
		surveyUnitDetailDto.setGeographicalLocation(
				new GeographicalLocationDto(surveyUnit.get().getAddress().getGeographicalLocation()));
		surveyUnitDetailDto.setSampleIdentifiers(
				sampleIdentifierRepository.findDtoById(surveyUnit.get().getSampleIdentifier().getId()));
		surveyUnitDetailDto.setComments(commentRepository.findAllDtoBySurveyUnit(surveyUnit.get()));
		surveyUnitDetailDto.setContactAttempts(contactAttemptRepository.findAllDtoBySurveyUnit(surveyUnit.get()));
		surveyUnitDetailDto.setContactOutcome(contactOutcomeRepository.findDtoBySurveyUnit(surveyUnit.get()));
		List<StateDto> states = stateRepository.findAllDtoBySurveyUnitIdOrderByDateAsc(surveyUnit.get().getId());
		states = states.stream().filter(s -> BussinessRules.stateCanBeSeenByInterviewerBussinessRules(s.getType()))
				.collect(Collectors.toList());
		surveyUnitDetailDto.setStates(states);
		return surveyUnitDetailDto;
	}

	public List<SurveyUnitDto> getSurveyUnitDto(String userId) {
		List<String> surveyUnitDtoIds = null;
		if (userId.equals(GUEST)) {
			surveyUnitDtoIds = surveyUnitRepository.findAllIds();
		} else {
			surveyUnitDtoIds = surveyUnitRepository.findIdsByInterviewerId(userId);
		}
		if (surveyUnitDtoIds.isEmpty()) {
			LOGGER.error("No Survey Unit found for interviewer {}", userId);
			return List.of();
		}
		if (userId.equals(GUEST)) {
			return surveyUnitDtoIds.stream()
					.map(idSurveyUnit -> new SurveyUnitDto(idSurveyUnit,
							surveyUnitRepository.findCampaignDtoById(idSurveyUnit),
							visibilityRepository.findVisibilityBySurveyUnitId(idSurveyUnit)))
					.collect(Collectors.toList());
		}
		surveyUnitDtoIds = surveyUnitDtoIds.stream().filter(id -> canBeSeenByInterviewer(id))
				.collect(Collectors.toList());

		return surveyUnitDtoIds.stream()
				.map(idSurveyUnit -> new SurveyUnitDto(idSurveyUnit,
						surveyUnitRepository.findCampaignDtoById(idSurveyUnit),
						visibilityRepository.findVisibilityBySurveyUnitId(idSurveyUnit)))
				.collect(Collectors.toList());
	}

	public boolean canBeSeenByInterviewer(String suId) {
		StateDto dto = stateRepository.findFirstDtoBySurveyUnitIdOrderByDateDesc(suId);
		StateType currentState = dto != null ? dto.getType() : null;
		return currentState != null && BussinessRules.stateCanBeSeenByInterviewerBussinessRules(currentState);
	}

	@Transactional
	public HttpStatus updateSurveyUnitDetail(String userId, String id, SurveyUnitDetailDto surveyUnitDetailDto) {
		if (surveyUnitDetailDto == null) {
			LOGGER.error("Survey Unit in parameter is null");
			return HttpStatus.BAD_REQUEST;
		}
		Optional<SurveyUnit> surveyUnitOpt;
		if (userId.equals(GUEST)) {
			surveyUnitOpt = surveyUnitRepository.findById(id);
		} else {
			surveyUnitOpt = surveyUnitRepository.findByIdAndInterviewerIdIgnoreCase(id, userId);
		}
		if (!surveyUnitOpt.isPresent()) {
			LOGGER.error("Survey Unit {} not found in DB for interviewer {}", id, userId);
			return HttpStatus.NOT_FOUND;
		}
		SurveyUnit surveyUnit = surveyUnitOpt.get();
		updateAddress(surveyUnit, surveyUnitDetailDto);
		try {
			updatePersons(surveyUnitDetailDto);
		} catch (SurveyUnitException e) {
			return HttpStatus.BAD_REQUEST;
		}
		updateComment(surveyUnit, surveyUnitDetailDto);
		updateStates(surveyUnit, surveyUnitDetailDto);
		updateContactAttempt(surveyUnit, surveyUnitDetailDto);
		updateContactOutcome(surveyUnit, surveyUnitDetailDto);
		surveyUnitRepository.save(surveyUnit);
		LOGGER.info("Finish update in DB");
		return HttpStatus.OK;
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
		LOGGER.info("Contact outcome updated");
	}

	private void updateContactAttempt(SurveyUnit surveyUnit, SurveyUnitDetailDto surveyUnitDetailDto) {
		if (surveyUnitDetailDto.getContactAttempts() != null) {
			Set<ContactAttempt> contactAttemps = surveyUnit.getContactAttempts();
			contactAttemps.clear();
			Set<ContactAttempt> newContactAttempts = surveyUnitDetailDto.getContactAttempts().stream()
					.map(dto -> new ContactAttempt(dto, surveyUnit)).collect(Collectors.toSet());
			contactAttemps.addAll(newContactAttempts);
			LOGGER.info("Contact attempts updated");
		}
	}

	private void updateStates(SurveyUnit surveyUnit, SurveyUnitDetailDto surveyUnitDetailDto) {
		
		if (surveyUnitDetailDto.getStates() != null) {
			for (StateDto stateDto : surveyUnitDetailDto.getStates()) {
				stateRepository.save(new State(stateDto.getDate(), surveyUnit, stateDto.getType()));
			}
		}
		
		StateType currentState = stateRepository.findFirstDtoBySurveyUnitIdOrderByDateDesc(surveyUnit.getId())
				.getType();
		if (currentState == StateType.WFS) {
			addStateAuto(surveyUnit);
		}
		
		
		currentState = stateRepository.findFirstDtoBySurveyUnitIdOrderByDateDesc(surveyUnit.getId()).getType();
		if (currentState != StateType.FIN && currentState != StateType.TBR) {
			Set<State> ueStates = surveyUnit.getStates();
			if (ueStates.stream().anyMatch(s -> s.getType() == StateType.FIN)) {
				ueStates.add(new State(new Date().getTime(), surveyUnit, StateType.FIN));
			} else if (ueStates.stream().anyMatch(s -> s.getType() == StateType.TBR)) {
				ueStates.add(new State(new Date().getTime(), surveyUnit, StateType.TBR));
			}
		}
		LOGGER.info("States");
	}

	private void addStateAuto(SurveyUnit surveyUnit) {
		if (surveyUnitRepository.findCountUeTBRByInterviewerIdAndCampaignId(
				surveyUnit.getInterviewer().getId(), surveyUnit.getCampaign().getId(),
				surveyUnit.getId()) < 5) {
			stateRepository.save(new State(new Date().getTime(), surveyUnit, StateType.TBR));
		} else {
			stateRepository.save(new State(new Date().getTime(), surveyUnit, StateType.FIN));
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
			LOGGER.info("Comments updated");
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
						throw new SurveyUnitException();
					}
				} else {
					throw new SurveyUnitException();
				}

			}
			LOGGER.info("Persons updated");
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
		if (person.getPhoneNumbers() != null) {
			Set<PhoneNumber> phoneNumbers = person.getPhoneNumbers().stream()
					.map(pn -> new PhoneNumber(pn, pers)).collect(Collectors.toSet());
			pers.getPhoneNumbers().clear();
			pers.getPhoneNumbers().addAll(phoneNumbers);
		}
	}

	private void updateAddress(SurveyUnit surveyUnit, SurveyUnitDetailDto surveyUnitDetailDto) {
		if (surveyUnitDetailDto.getAddress() != null) {
			InseeAddress inseeAddress;
			Optional<InseeAddress> optionalInseeAddress = addressRepository.findById(surveyUnit.getAddress().getId());
			if (!optionalInseeAddress.isPresent()) {
				inseeAddress = new InseeAddress(surveyUnitDetailDto.getAddress(),
						surveyUnit.getAddress().getGeographicalLocation());
			} else {
				inseeAddress = optionalInseeAddress.get();
				inseeAddress.setL1(surveyUnitDetailDto.getAddress().getL1());
				inseeAddress.setL2(surveyUnitDetailDto.getAddress().getL2());
				inseeAddress.setL3(surveyUnitDetailDto.getAddress().getL3());
				inseeAddress.setL4(surveyUnitDetailDto.getAddress().getL4());
				inseeAddress.setL5(surveyUnitDetailDto.getAddress().getL5());
				inseeAddress.setL6(surveyUnitDetailDto.getAddress().getL6());
				inseeAddress.setL7(surveyUnitDetailDto.getAddress().getL7());
			}
			// Update Address
			addressRepository.save(inseeAddress);
		}
		LOGGER.info("Address updated");
	}

	@Transactional
	public HttpStatus updateSurveyUnitComment(String userId, String suId, CommentDto comment) {
		Optional<SurveyUnit> surveyUnitOpt = surveyUnitRepository.findById(suId);
		if (!surveyUnitOpt.isPresent()) {
			LOGGER.error("Survey Unit {} not found in DB for interviewer {}", suId, userId);
			return HttpStatus.NOT_FOUND;
		}
		if (comment.getType() == null || comment.getValue() == null) {
			return HttpStatus.BAD_REQUEST;
		}
		SurveyUnit surveyUnit = surveyUnitOpt.get();
		Set<Comment> comments = surveyUnit.getComments();
		Set<Comment> commentsToRemove = comments.stream().filter(c -> c.getType() == comment.getType())
				.collect(Collectors.toSet());
		comments.removeAll(commentsToRemove);
		comments.add(new Comment(comment, surveyUnit));
		LOGGER.info("Comments updated");
		surveyUnitRepository.save(surveyUnit);
		return HttpStatus.OK;
	}
	
	@Transactional
	public HttpStatus updateSurveyUnitViewed(String userId, String suId) {
		Optional<SurveyUnit> surveyUnitOpt = surveyUnitRepository.findById(suId);
		if (!surveyUnitOpt.isPresent()) {
			LOGGER.error("Survey Unit {} not found in DB for interviewer {}", suId, userId);
			return HttpStatus.NOT_FOUND;
		}
		SurveyUnit surveyUnit = surveyUnitOpt.get();
		surveyUnit.setViewed(true);
		LOGGER.info("Viewed updated");
		surveyUnitRepository.save(surveyUnit);
		return HttpStatus.OK;
	}

	public Set<SurveyUnitCampaignDto> getSurveyUnitByCampaign(String campaignId, String userId, String state) {
		List<String> lstOuId = userService.getUserOUs(userId, true).stream().map(OrganizationUnitDto::getId)
				.collect(Collectors.toList());
		Set<SurveyUnit> lstSurveyUnit = surveyUnitRepository
				.findByCampaignIdAndOrganizationUnitIdIn(campaignId, lstOuId);
		if (state != null && !state.isEmpty() && state.equalsIgnoreCase(StateType.FIN.toString())) {
			// filter on SU with at least one state FIN
			lstSurveyUnit = lstSurveyUnit.stream().filter(su -> su.isAtLeastState(state)).collect(Collectors.toSet());
		} else if (state != null && !state.isEmpty() && !state.equalsIgnoreCase(StateType.FIN.toString())) {
			// filter on SU with last state equals to state
			lstSurveyUnit = lstSurveyUnit.stream().filter(su -> su.isLastState(state)).collect(Collectors.toSet());
		}
		if (lstSurveyUnit.isEmpty()) {
			LOGGER.error("No Survey Unit found for the user {}", userId);
		}
		return lstSurveyUnit.stream().map(su -> new SurveyUnitCampaignDto(su)).collect(Collectors.toSet());
	}

	public List<SurveyUnitCampaignDto> getClosableSurveyUnits() {
		List<SurveyUnit> suList = surveyUnitRepository.findAllSurveyUnitsInProcessingPhase(System.currentTimeMillis());
		return suList.stream().filter(su -> {
			if (su.getInterviewer() == null) {
				return false;
			}
			StateDto lastState = stateRepository.findFirstDtoBySurveyUnitIdOrderByDateDesc(su.getId());
			if (lastState == null) {
				return false;
			}
			StateType currentState = lastState.getType();
			return currentState != StateType.CLO;
		}).map(su -> new SurveyUnitCampaignDto(su)).collect(Collectors.toList());
	}

	@Transactional
	public HttpStatus addStateToSurveyUnit(String surveyUnitId, StateType state) {
		Optional<SurveyUnit> su = surveyUnitRepository.findById(surveyUnitId);
		if (su.isPresent()) {
			StateType currentState = stateRepository.findFirstDtoBySurveyUnitOrderByDateDesc(su.get()).getType();
			if (Boolean.TRUE.equals(BussinessRules.stateCanBeModifiedByManager(currentState, state))) {
				stateRepository.save(new State(new Date().getTime(), su.get(), state));
				return HttpStatus.OK;
			} else {
				return HttpStatus.FORBIDDEN;
			}

		} else {
			return HttpStatus.BAD_REQUEST;
		}
	}
	
	@Transactional
	public HttpStatus closeSurveyUnit(String surveyUnitId, ClosingCauseType type) {
		Optional<SurveyUnit> su = surveyUnitRepository.findById(surveyUnitId);
		if (su.isPresent()) {
			SurveyUnit surveyUnit = su.get();
			StateType currentState = stateRepository.findFirstDtoBySurveyUnitOrderByDateDesc(su.get()).getType();
			if(currentState.equals(StateType.CLO)) {
				addOrModifyClosingCause(surveyUnit, type);
				return HttpStatus.OK;
			} else if (Boolean.TRUE.equals(BussinessRules.stateCanBeModifiedByManager(currentState, StateType.CLO))) {
				stateRepository.save(new State(new Date().getTime(), su.get(), StateType.CLO));
				addOrModifyClosingCause(surveyUnit, type);
				return HttpStatus.OK;
			} else {
				return HttpStatus.FORBIDDEN;
			}

		} else {
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
			return HttpStatus.NOT_FOUND;
		}
	}
	
	@Transactional
	public void addOrModifyClosingCause(SurveyUnit surveyUnit, ClosingCauseType type) {
		ClosingCause cc;
		if(surveyUnit.getClosingCause()!=null) {
			cc = surveyUnit.getClosingCause();
		}
		else {
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
			LOGGER.error("SU {} not found", suId);
			return List.of();
		}
		return stateRepository.findAllDtoBySurveyUnitIdOrderByDateAsc(suId);

	}

	@Override
	public List<SurveyUnit> getSurveyUnitIdByOrganizationUnits(List<String> lstOuId) {
		return surveyUnitRepository.findByOrganizationUnitIdIn(lstOuId);
	}
}
