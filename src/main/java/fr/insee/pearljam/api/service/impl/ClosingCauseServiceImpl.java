package fr.insee.pearljam.api.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.dto.closingcause.ClosingCauseCountDto;
import fr.insee.pearljam.api.repository.ClosingCauseRepository;
import fr.insee.pearljam.api.repository.InterviewerRepository;
import fr.insee.pearljam.api.repository.OrganizationUnitRepository;
import fr.insee.pearljam.api.repository.StateRepository;
import fr.insee.pearljam.api.service.ClosingCauseService;
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
public class ClosingCauseServiceImpl implements ClosingCauseService {

	@Autowired
	ClosingCauseRepository closingCauseRepository;
	
	@Autowired
	StateRepository stateRepository;

	@Autowired
	UserService userService;

	@Autowired
	InterviewerRepository interviewerRepository;
	
	@Autowired
	OrganizationUnitRepository organizationUnitRepository;

	@Autowired
	UtilsService utilsService;

	private static final Logger LOGGER = LoggerFactory.getLogger(ClosingCauseServiceImpl.class);

	
	@Override
	public ClosingCauseCountDto getClosingCauseCount(String userId, String campaignId, String interviewerId, Long date,
			List<String> associatedOrgUnits) {
		ClosingCauseCountDto closingCauseCountDto = new ClosingCauseCountDto();
		if (!utilsService.checkUserCampaignOUConstraints(userId, campaignId)) {
			return null;
		}
		if (!interviewerRepository.findById(interviewerId).isPresent()) {
			LOGGER.error("No interviewer found for the id {}", interviewerId);
			return null;
		}
		List<String> userOuIds;
		if(!userId.equals(Constants.GUEST)) {
			userOuIds = utilsService.getRelatedOrganizationUnits(userId);
		} else {
			userOuIds = organizationUnitRepository.findAllId();
		}
		
		List<String> intervIds = interviewerRepository.findInterviewersByOrganizationUnits(associatedOrgUnits);
		Long dateToUse = date;
		if (dateToUse == null) {
			dateToUse = System.currentTimeMillis();
		}
		if (!intervIds.isEmpty() && (intervIds.contains(interviewerId)) || userId.equals(Constants.GUEST)) {
			closingCauseCountDto = new ClosingCauseCountDto(closingCauseRepository.getClosingCauseCount(campaignId, interviewerId, userOuIds, dateToUse));
			closingCauseCountDto.setTotal(stateRepository.getTotalStateCount(campaignId, interviewerId, userOuIds, dateToUse));
		}
		if (closingCauseCountDto.getTotal() == null) {
			LOGGER.error("No matching interviewers {} were found for the user {} and the campaign {}", interviewerId,
					userId, campaignId);
			return null;
		}
		return closingCauseCountDto;
	}
}
