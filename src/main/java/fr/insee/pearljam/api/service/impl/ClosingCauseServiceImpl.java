package fr.insee.pearljam.api.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.dto.closingcause.ClosingCauseCountDto;
import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.api.repository.ClosingCauseRepository;
import fr.insee.pearljam.api.repository.InterviewerRepository;
import fr.insee.pearljam.api.repository.OrganizationUnitRepository;
import fr.insee.pearljam.api.repository.StateRepository;
import fr.insee.pearljam.api.service.ClosingCauseService;
import fr.insee.pearljam.api.service.UtilsService;
import lombok.RequiredArgsConstructor;

/**
 * Implementation of the Service for the Interviewer entity
 * 
 * @author scorcaud
 *
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ClosingCauseServiceImpl implements ClosingCauseService {

	private final ClosingCauseRepository closingCauseRepository;

	private final StateRepository stateRepository;

	private final InterviewerRepository interviewerRepository;

	private final OrganizationUnitRepository organizationUnitRepository;

	private final UtilsService utilsService;

	@Override
	public ClosingCauseCountDto getClosingCauseCount(String userId, String campaignId, String interviewerId, Long date,
			List<String> associatedOrgUnits) throws NotFoundException {
		ClosingCauseCountDto closingCauseCountDto = new ClosingCauseCountDto();
		if (!utilsService.checkUserCampaignOUConstraints(userId, campaignId)) {
			throw new NotFoundException(
					String.format("No campaign with id %s  associated to the user %s", campaignId, userId));
		}
		if (!interviewerRepository.findById(interviewerId).isPresent()) {
			throw new NotFoundException("No interviewer found for the id " + interviewerId);
		}
		List<String> userOuIds;
		if (!userId.equals(Constants.GUEST)) {
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
			closingCauseCountDto = new ClosingCauseCountDto(
					closingCauseRepository.getClosingCauseCount(campaignId, interviewerId, userOuIds, dateToUse));
			closingCauseCountDto
					.setTotal(stateRepository.getTotalStateCount(campaignId, interviewerId, userOuIds, dateToUse));
		}
		if (closingCauseCountDto.getTotal() == null) {
			throw new NotFoundException("No matching interviewers " + interviewerId + " were found for the user "
					+ userId + " and the campaign " + interviewerId);
		}
		return closingCauseCountDto;
	}
}
