package fr.insee.pearljam.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.api.domain.Comment;
import fr.insee.pearljam.api.domain.ContactAttempt;
import fr.insee.pearljam.api.domain.ContactOutcome;
import fr.insee.pearljam.api.domain.InseeAddress;
import fr.insee.pearljam.api.domain.State;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.comment.CommentDto;
import fr.insee.pearljam.api.dto.contactattempt.ContactAttemptDto;
import fr.insee.pearljam.api.dto.geographicallocation.GeographicalLocationDto;
import fr.insee.pearljam.api.dto.state.StateDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitCampaignDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitDetailDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitDto;
import fr.insee.pearljam.api.repository.AddressRepository;
import fr.insee.pearljam.api.repository.CommentRepository;
import fr.insee.pearljam.api.repository.ContactAttemptRepository;
import fr.insee.pearljam.api.repository.ContactOutcomeRepository;
import fr.insee.pearljam.api.repository.GeographicalLocationRepository;
import fr.insee.pearljam.api.repository.InterviewerRepository;
import fr.insee.pearljam.api.repository.SampleIdentifierRepository;
import fr.insee.pearljam.api.repository.StateRepository;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;


/**
 * @author scorcaud
 *
 */
@Service
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
	
	/**
	 * Retrieve the SurveyUnitDetail entity by Id and UserId
	 * @param userId
	 * @param id
	 * @return SurveyUnitDetailDto
	 */
	@Override
	public SurveyUnitDetailDto getSurveyUnitDetail(String userId, String id) {
		Optional<SurveyUnit> surveyUnit = null;
		if(userId.equals(GUEST)) {
			surveyUnit = surveyUnitRepository.findById(id);
		} else {
			surveyUnit = surveyUnitRepository.findByIdAndInterviewerIdIgnoreCase(id, userId);
		}
		if(!surveyUnit.isPresent()) {
			LOGGER.error("Survey Unit {} not found in DB for interviewer {}", id, userId);
			return null;
		}
		SurveyUnitDetailDto surveyUnitDetailDto= new SurveyUnitDetailDto(surveyUnit.get());
		surveyUnitDetailDto.setAddress(addressRepository.findDtoById(surveyUnit.get().getAddress().getId()));
		surveyUnitDetailDto.setGeographicalLocation(new GeographicalLocationDto(surveyUnit.get().getAddress().getGeographicalLocation()));
		surveyUnitDetailDto.setSampleIdentifiers(sampleIdentifierRepository.findDtoById(surveyUnit.get().getSampleIdentifier().getId()));
		surveyUnitDetailDto.setComments(commentRepository.findAllDtoBySurveyUnit(surveyUnit.get()));
		surveyUnitDetailDto.setContactAttempts(contactAttemptRepository.findAllDtoBySurveyUnit(surveyUnit.get()));
		surveyUnitDetailDto.setContactOutcome(contactOutcomeRepository.findDtoBySurveyUnit(surveyUnit.get()));
		surveyUnitDetailDto.setStates(new ArrayList<StateDto>());
		surveyUnitDetailDto.setLastState(stateRepository.findFirstDtoBySurveyUnitOrderByDate(surveyUnit.get()));
		return surveyUnitDetailDto;
	}

	/**
	 * Retrieve all the SurveyUnit entity by userId
	 * @param userId
	 * @return List of SurveyUnitDto
	 */
	@Override
	public List<SurveyUnitDto> getSurveyUnitDto(String userId) {
		List<SurveyUnitDto> surveyUnitDtoReturned = new ArrayList<>();
		List<String> surveyUnitDtoIds = null;
		if(userId.equals(GUEST)) {
			surveyUnitDtoIds = surveyUnitRepository.findAllIds();
		} else {
			surveyUnitDtoIds = surveyUnitRepository.findIdsByInterviewerId(userId);
		}
		if(surveyUnitDtoIds.isEmpty()) {
			LOGGER.error("No Survey Unit found for interviewer {}", userId);
			return List.of();
		}
		for(String idSurveyUnit : surveyUnitDtoIds) {
			CampaignDto campaign = surveyUnitRepository.findCampaignDtoById(idSurveyUnit);
			surveyUnitDtoReturned.add( new SurveyUnitDto(idSurveyUnit, campaign));
		}
		return surveyUnitDtoReturned;
	}
	
	/**
	 * Update the SurveyUnit by Id and UserId with the SurveyUnitDetailDto passed in parameter
	 * @param userId
	 * @param id
	 * @param surveyUnitDetailDto
	 * @return HttpStatus
	 */
	@Transactional
	@Override
	public HttpStatus updateSurveyUnitDetail(String userId, String id, SurveyUnitDetailDto surveyUnitDetailDto) {
		if(surveyUnitDetailDto == null) {
			LOGGER.error("Survey Unit in parameter is null");
			return HttpStatus.BAD_REQUEST;
		}
		if(!id.equals(surveyUnitDetailDto.getId())) {
			LOGGER.error("Survey unit id and id in parameter are different", userId);
			return HttpStatus.BAD_REQUEST;
		}
		if(!surveyUnitDetailDto.isValid()) {
			LOGGER.error("Survey Unit in parameter is not well formed", userId);
			return HttpStatus.BAD_REQUEST;
		}
		Optional<SurveyUnit> surveyUnit = null;
		if(userId.equals(GUEST)) {
			surveyUnit = surveyUnitRepository.findById(id);
		}else {
			surveyUnit = surveyUnitRepository.findByIdAndInterviewerIdIgnoreCase(id, userId);
		}
		if(!surveyUnit.isPresent()) {
			LOGGER.error("Survey Unit {} not found in DB for interviewer {}", id, userId);
			return HttpStatus.NOT_FOUND;
		}
		//Update of SurveyUnit
		LOGGER.info("Start update in DB");
		surveyUnit.get().setFirstName(surveyUnitDetailDto.getFirstName());
		surveyUnit.get().setLasttName(surveyUnitDetailDto.getLastName());
		surveyUnit.get().setPhoneNumbers(surveyUnitDetailDto.getPhoneNumbers());
		surveyUnitRepository.save(surveyUnit.get());
		LOGGER.info("Update survey unit ok");
	
		if(surveyUnitDetailDto.getAddress()!=null) {
			InseeAddress inseeAddress;
			Optional<InseeAddress> optionalInseeAddress = addressRepository.findById(surveyUnit.get().getAddress().getId());
			if(!optionalInseeAddress.isPresent()) {
				inseeAddress = new InseeAddress(surveyUnitDetailDto.getAddress(), surveyUnit.get().getAddress().getGeographicalLocation());
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
			//Update Address
			addressRepository.save(inseeAddress);
		}
		LOGGER.info("Update address ok");
		
		//Update Comment
		Comment comment;
		for(CommentDto commentDto : surveyUnitDetailDto.getComments()) {
			Optional<Comment> optionalComment = commentRepository.findBySurveyUnitAndType(surveyUnit.get(), commentDto.getType());
			if(!optionalComment.isPresent()) {
				comment = new Comment();
			} else {
				comment = optionalComment.get();
			}
			comment.setSurveyUnit(surveyUnit.get());
			comment.setType(commentDto.getType());
			comment.setValue(commentDto.getValue());
			commentRepository.save(comment);
		}
		LOGGER.info("Update comments ok");
		
		//Update State
		for(StateDto stateDto : surveyUnitDetailDto.getStates()) {
			stateRepository.save(new State(stateDto.getDate(), surveyUnit.get(), stateDto.getType()));
		}
		LOGGER.info("Update states ok");
		
		//Update ContactAttempt
		for(ContactAttemptDto contactAttemptDto : surveyUnitDetailDto.getContactAttempts()) {
			Optional<ContactAttempt> optionalContactAttempt = contactAttemptRepository.findBySurveyUnitAndStatus(surveyUnit.get(), contactAttemptDto.getStatus());
			if(!optionalContactAttempt.isPresent()) {
				contactAttemptRepository.save(new ContactAttempt(contactAttemptDto.getDate(), contactAttemptDto.getStatus(), surveyUnit.get()));
			} else {
				contactAttemptRepository.deleteByIdAndStatus(optionalContactAttempt.get().getId(), optionalContactAttempt.get().getStatus());
				contactAttemptRepository.save(new ContactAttempt(contactAttemptDto.getDate(), contactAttemptDto.getStatus(), surveyUnit.get()));
			}
		}
		LOGGER.info("Update contact attempts ok");
		
		//Update ContactOutcome
		if(surveyUnitDetailDto.getContactOutcome()!=null){
			ContactOutcome contactOutcome;
			Optional<ContactOutcome> contactOutcomeOptional = contactOutcomeRepository.findBySurveyUnit(surveyUnit.get());
			if(contactOutcomeOptional.isPresent()) {
				contactOutcome = contactOutcomeOptional.get();
			} else {
				contactOutcome = new ContactOutcome();
			}
			contactOutcome.setDate(surveyUnitDetailDto.getContactOutcome().getDate());
			contactOutcome.setType(surveyUnitDetailDto.getContactOutcome().getType());
			contactOutcome.setTotalNumberOfContactAttempts(surveyUnitDetailDto.getContactOutcome().getTotalNumberOfContactAttempts());
			contactOutcome.setSurveyUnit(surveyUnit.get());
			contactOutcomeRepository.save(contactOutcome);
		}
		LOGGER.info("Update contact outcome ok");
		LOGGER.info("Finish update in DB");
		return HttpStatus.OK;
	}
	
	public List<SurveyUnitCampaignDto> getSurveyUnitByCampaign(String id, String userId) {
		List<SurveyUnitCampaignDto> surveyUnitCampaignReturned = new ArrayList<>();
		List<String> surveyUnitDtoIds = null;
		if(userId.equals(GUEST)) {
			surveyUnitDtoIds = surveyUnitRepository.findAllIds();
		} else {
			surveyUnitDtoIds = surveyUnitRepository.findIdsByCampaignIdAndUserId(id, userId);
		}
		if(surveyUnitDtoIds.isEmpty()) {
			LOGGER.error("No Survey Unit found for the user {}", userId);
			return List.of();
		}
		for(String idSurveyUnit : surveyUnitDtoIds) {
			SurveyUnitCampaignDto surveyUnit = new SurveyUnitCampaignDto();
			surveyUnit.setId(idSurveyUnit);
			surveyUnit.setSsech(sampleIdentifierRepository.findSsechBySurveyUnitId(idSurveyUnit));
			surveyUnit.setInterviewer( interviewerRepository.findInterviewersDtoBySurveyUnitId(idSurveyUnit));
			String locationAndCity = addressRepository.findLocationAndCityBySurveyUnitId(idSurveyUnit);
			surveyUnit.setLocation(locationAndCity.split(" ")[0]);
			surveyUnit.setCity(locationAndCity.split(" ")[1]);
			surveyUnitCampaignReturned.add(surveyUnit);
		}
		return surveyUnitCampaignReturned;		
	}
}
