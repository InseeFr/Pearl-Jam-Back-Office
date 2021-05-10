package fr.insee.pearljam.api.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.dto.contactoutcome.ContactOutcomeTypeCountCampaignDto;
import fr.insee.pearljam.api.dto.contactoutcome.ContactOutcomeTypeCountDto;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import fr.insee.pearljam.api.repository.CampaignRepository;
import fr.insee.pearljam.api.repository.ClosingCauseRepository;
import fr.insee.pearljam.api.repository.ContactOutcomeRepository;
import fr.insee.pearljam.api.repository.InterviewerRepository;
import fr.insee.pearljam.api.repository.OrganizationUnitRepository;
import fr.insee.pearljam.api.repository.StateRepository;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.api.repository.UserRepository;
import fr.insee.pearljam.api.repository.VisibilityRepository;
import fr.insee.pearljam.api.service.ContactOutcomeService;
import fr.insee.pearljam.api.service.UserService;
import fr.insee.pearljam.api.service.UtilsService;

/**
 * Implementation of the Service for the Interviewer entity
 * 
 * @author scorcaud
 *
 */
@Service
@Transactional
public class ContactOutcomeServiceImpl implements ContactOutcomeService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContactOutcomeServiceImpl.class);

	@Autowired
	CampaignRepository campaignRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ContactOutcomeRepository contactOutcomeRepository;

	@Autowired
	StateRepository stateRepository;
	
	@Autowired
	ClosingCauseRepository closingCauseRepository;

	@Autowired
	InterviewerRepository interviewerRepository;

	@Autowired
	SurveyUnitRepository surveyUnitRepository;

	@Autowired
	VisibilityRepository visibilityRepository;

	@Autowired
	OrganizationUnitRepository organizationUnitRepository;

	@Autowired
	UserService userService;

	@Autowired
	UtilsService utilsService;

	@Override
	public ContactOutcomeTypeCountCampaignDto getContactOutcomeCountTypeByCampaign(String userId, String campaignId,
			Long date) {
		ContactOutcomeTypeCountCampaignDto stateCountCampaignDto = new ContactOutcomeTypeCountCampaignDto();
		if (!utilsService.checkUserCampaignOUConstraints(userId, campaignId)) {
			return null;
		}
		List<ContactOutcomeTypeCountDto> stateCountList = new ArrayList<>();
		Long dateToUse = date;
		if (dateToUse == null) {
			dateToUse = System.currentTimeMillis();
		}
		for (String id : organizationUnitRepository.findAllId()) {
			if (organizationUnitRepository.findChildren(id).isEmpty()
					&& visibilityRepository.findVisibilityInCollectionPeriod(campaignId, id, dateToUse).isPresent()) {
				stateCountList.add(new ContactOutcomeTypeCountDto(id, organizationUnitRepository.findLabel(id),
						contactOutcomeRepository.getContactOutcomeTypeCountByCampaignAndOU(campaignId, id, dateToUse)));
			}
		}
		stateCountCampaignDto.setOrganizationUnits(stateCountList);
		stateCountCampaignDto.setFrance(new ContactOutcomeTypeCountDto(
				contactOutcomeRepository.getContactOutcomeTypeCountByCampaignId(campaignId, dateToUse)));
		if (stateCountCampaignDto.getFrance() == null || stateCountCampaignDto.getOrganizationUnits() == null
				) {
			LOGGER.error("No matching survey units states were found for the user {} and the campaign {}", userId,
					campaignId);
			return null;
		}
		return stateCountCampaignDto;
	}

	@Override
	public List<ContactOutcomeTypeCountDto> getContactOutcomeTypeCountByCampaign(String userId, Long date) {
		List<String> userOuIds;
		if(!userId.equals(Constants.GUEST)) {
			userOuIds = utilsService.getRelatedOrganizationUnits(userId);
		} else {
			userOuIds = organizationUnitRepository.findAllId();
		}
		final Long dateToUse = date==null?System.currentTimeMillis():date;
		List<String> lstCampaignUser = campaignRepository.findAllIdsVisible(userOuIds, dateToUse);
		return lstCampaignUser.stream()
				.map(idCampaign -> new ContactOutcomeTypeCountDto(
						contactOutcomeRepository.getContactOutcomeTypeCountByCampaignId(idCampaign, dateToUse),
						campaignRepository.findDtoById(idCampaign)))
				.collect(Collectors.toList());
	}
	
	@Override
	public ContactOutcomeTypeCountDto getNbSUNotAttributedContactOutcomes(String userId, String id, Long date) {
		List<String> organizationUnits = userService.getUserOUs(userId, true)
				.stream().map(OrganizationUnitDto::getId)
				.collect(Collectors.toList());
		Long dateToUse = date;
		if (dateToUse == null) {
			dateToUse = System.currentTimeMillis();
		}
		
		if(visibilityRepository.findVisibilityInCollectionPeriodForOUs(id, organizationUnits, dateToUse).isEmpty()) {
			return null;
		}
		
		return new ContactOutcomeTypeCountDto(
	        contactOutcomeRepository.findContactOutcomeTypeNotAttributed(id, organizationUnits, dateToUse)
	      );
	}
	
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
			dateToUse = System.currentTimeMillis();
		}
		return new ContactOutcomeTypeCountDto(
				contactOutcomeRepository.findContactOutcomeTypeByInterviewerAndCampaign(campaignId, interviewerId, userOuIds, dateToUse));
	}
}
