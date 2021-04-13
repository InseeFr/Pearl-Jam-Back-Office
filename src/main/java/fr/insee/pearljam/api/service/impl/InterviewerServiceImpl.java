package fr.insee.pearljam.api.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.domain.Interviewer;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.domain.Visibility;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.contactoutcome.ContactOutcomeTypeCountDto;
import fr.insee.pearljam.api.repository.CampaignRepository;
import fr.insee.pearljam.api.repository.ContactOutcomeRepository;
import fr.insee.pearljam.api.repository.InterviewerRepository;
import fr.insee.pearljam.api.repository.OrganizationUnitRepository;
import fr.insee.pearljam.api.repository.VisibilityRepository;
import fr.insee.pearljam.api.service.InterviewerService;
import fr.insee.pearljam.api.service.UtilsService;

/**
 * Implementation of the Service for the Interviewer entity
 * 
 * @author scorcaud
 *
 */
@Service
@Transactional
public class InterviewerServiceImpl implements InterviewerService {

	@Autowired
	ContactOutcomeRepository contactOutcomeRepository;
	
	@Autowired
	CampaignRepository campaignRepository;
	
	@Autowired
	InterviewerRepository interviewerRepository;
	
	@Autowired
	VisibilityRepository visibilityRepository;
	
	@Autowired
	UtilsService utilsService;
	
	@Autowired
	OrganizationUnitRepository organizationUnitRepository;
	
	@Override
	public ContactOutcomeTypeCountDto getContactOutcomeByInterviewerAndCampaign(String userId, String campaignId, String interviewerId, Long date) {
		
		
		if(!interviewerRepository.findById(interviewerId).isPresent() || !campaignRepository.findById(campaignId).isPresent()) {
			return null;
		}
		
		List<String> userOuIds;
		if(!userId.equals(Constants.GUEST)) {
			userOuIds = utilsService.getRelatedOrganizationUnits(userId);
		} else {
			userOuIds = organizationUnitRepository.findAllId();
		}

		Long dateToUse = date;
		if (dateToUse == null) {
			dateToUse = System.currentTimeMillis();;
		}
		return new ContactOutcomeTypeCountDto(
				contactOutcomeRepository.findContactOutcomeTypeByInterviewerAndCampaign(campaignId, interviewerId, userOuIds, dateToUse));
	}
	public List<CampaignDto> findCampaignsOfInterviewer(String interviewerId) {
		Optional<Interviewer> intwOpt = interviewerRepository.findById(interviewerId);
		if(!intwOpt.isPresent()) {
			return null;
		}
		Interviewer intw = intwOpt.get();
		List<String> suIds = intw.getSurveyUnits().stream().map(SurveyUnit::getId).collect(Collectors.toList());
		List<Visibility> visibilities = visibilityRepository.findAllVisibilityBySurveyUnitIds(suIds);
		List<CampaignDto> dtos = new ArrayList<>();
		for(Visibility vi : visibilities) {
			Optional<CampaignDto> dtoOpt = dtos.stream()
				.filter(dto -> dto.getId().equals(vi.getCampaign().getId()))
				.findFirst();
			if(!dtoOpt.isPresent()) {
				dtos.add(new CampaignDto(
						vi.getCampaign().getId(),
						vi.getCampaign().getLabel(),
						vi.getManagementStartDate(),
						vi.getEndDate()));
			}
			else {
				if(dtoOpt.get().getManagementStartDate() > vi.getManagementStartDate()) {
					dtoOpt.get().setManagementStartDate(vi.getManagementStartDate());
				}
				if(dtoOpt.get().getEndDate() < vi.getEndDate()) {
					dtoOpt.get().setEndDate(vi.getEndDate());
				}
			}
		}
		
		return dtos;
		
	}


}
