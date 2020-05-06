package fr.insee.pearljam.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.comment.CommentDto;
import fr.insee.pearljam.api.dto.contactattempt.ContactAttemptDto;
import fr.insee.pearljam.api.dto.geographicallocation.GeographicalLocationDto;
import fr.insee.pearljam.api.dto.state.StateDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitDetailDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitDto;
import fr.insee.pearljam.api.repository.AddressRepository;
import fr.insee.pearljam.api.repository.CampaignRepository;
import fr.insee.pearljam.api.repository.CommentRepository;
import fr.insee.pearljam.api.repository.ContactAttemptRepository;
import fr.insee.pearljam.api.repository.ContactOutcomeRepository;
import fr.insee.pearljam.api.repository.SampleIdentifierRepository;
import fr.insee.pearljam.api.repository.StateRepository;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;

@Service
public class SurveyUnitServiceImpl implements SurveyUnitService {

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
	CampaignRepository campaignRepository;
	
	@Override
	public SurveyUnitDetailDto getSurveyUnitDetail(String id) {
		Optional<SurveyUnit> surveyUnit = surveyUnitRepository.findById(id);
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
		surveyUnitDetailDto.setLastState(stateRepository.findFirstBySurveyUnitOrderByDate(surveyUnit.get()));
		return surveyUnitDetailDto;
	}

	@Override
	public List<SurveyUnitDto> getSurveyUnitDto(String idInterviewer) {
		List<String> surveyUnitDtoIds = surveyUnitRepository.findDtoIdBy_IdInterviewer(idInterviewer);
		List<SurveyUnitDto> surveyUnitDtoReturned = new ArrayList<SurveyUnitDto>();
		if(surveyUnitDtoIds.isEmpty()) {
			return null;
		}
		for(String idSurveyUnit : surveyUnitDtoIds) {
			CampaignDto campaign = campaignRepository.findDtoBySurveyUnitId(idSurveyUnit);
			surveyUnitDtoReturned.add( new SurveyUnitDto(idSurveyUnit, campaign));
		}
		return surveyUnitDtoReturned;
	}
	
	@Override
	public HttpStatus updateSurveyUnitDetail(String id, SurveyUnitDetailDto surveyUnitDetailDto) {
		if(surveyUnitDetailDto == null) {
			return HttpStatus.BAD_REQUEST;
		}
		//addressRepository.save(new InseeAddress(surveyUnitDetailDto.getAddress()));
		Optional<SurveyUnit> surveyUnit = surveyUnitRepository.findById(id);
		if(!surveyUnit.isPresent()) {
			return HttpStatus.NOT_FOUND;
		}
		surveyUnit.get().setFirstName(surveyUnitDetailDto.getFirstName());
		surveyUnit.get().setLasttName(surveyUnitDetailDto.getLastName());
		surveyUnit.get().setPhoneNumbers(surveyUnitDetailDto.getPhoneNumbers());
		surveyUnitRepository.save(surveyUnit.get());
		/*for(CommentDto commentDto : surveyUnitDetailDto.getComments()) {
			commentRepository.saveBySurveyUnitId(commentDto.getType().toString(), commentDto.getValue(), id);
		}
		for(StateDto stateDto : surveyUnitDetailDto.getStates()) {
			stateRepository.saveById(stateDto.getDate(), stateDto.getType().toString(), stateDto.getId());
		}
		for(ContactAttemptDto contactAttemptDto : surveyUnitDetailDto.getContactAttempts()) {
			contactAttemptRepository.saveBySurveyUnitId(contactAttemptDto.getDate(), contactAttemptDto.getStatus().toString(), id);
		}
		contactOutcomeRepository.save(surveyUnitDetailDto.getContactOutcome());*/
		
		return HttpStatus.OK;
	}

}
