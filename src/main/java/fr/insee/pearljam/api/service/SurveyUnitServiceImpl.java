package fr.insee.pearljam.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

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
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitDetailDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitDto;
import fr.insee.pearljam.api.repository.AddressRepository;
import fr.insee.pearljam.api.repository.CommentRepository;
import fr.insee.pearljam.api.repository.ContactAttemptRepository;
import fr.insee.pearljam.api.repository.ContactOutcomeRepository;
import fr.insee.pearljam.api.repository.GeographicalLocationRepository;
import fr.insee.pearljam.api.repository.SampleIdentifierRepository;
import fr.insee.pearljam.api.repository.StateRepository;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;


/**
 * @author scorcaud
 *
 */
@Service
public class SurveyUnitServiceImpl implements SurveyUnitService {
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
			surveyUnit = surveyUnitRepository.findByIdAndInterviewerId(id, userId);
		}
		if(!surveyUnit.isPresent()) {
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
		if(surveyUnitDetailDto == null || !id.equals(surveyUnitDetailDto.getId()) || !surveyUnitDetailDto.isValid()) {
			return HttpStatus.BAD_REQUEST;
		}
		Optional<SurveyUnit> surveyUnit = null;
		if(userId.equals(GUEST)) {
			surveyUnit = surveyUnitRepository.findById(id);
		}else {
			surveyUnit = surveyUnitRepository.findByIdAndInterviewerId(id, userId);
		}
		if(!surveyUnit.isPresent()) {
			return HttpStatus.NOT_FOUND;
		}
		//Update of SurveyUnit
		surveyUnit.get().setFirstName(surveyUnitDetailDto.getFirstName());
		surveyUnit.get().setLasttName(surveyUnitDetailDto.getLastName());
		surveyUnit.get().setPhoneNumbers(surveyUnitDetailDto.getPhoneNumbers());
		surveyUnitRepository.save(surveyUnit.get());
	
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
		
		//Update State
		for(StateDto stateDto : surveyUnitDetailDto.getStates()) {
			stateRepository.save(new State(stateDto.getDate(), surveyUnit.get(), stateDto.getType()));
		}
		
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
		return HttpStatus.OK;
	}
}
