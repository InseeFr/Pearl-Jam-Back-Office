package fr.insee.pearljam.domain.closingcause.service;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.domain.interviewer.model.Interviewer;
import fr.insee.pearljam.api.closingcause.dto.ClosingCauseCountDto;
import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.api.repository.ClosingCauseRepository;
import fr.insee.pearljam.api.repository.InterviewerRepository;
import fr.insee.pearljam.api.repository.OrganizationUnitRepository;
import fr.insee.pearljam.api.repository.StateRepository;
import fr.insee.pearljam.domain.common.port.userside.UtilsService;
import fr.insee.pearljam.domain.closingcause.port.userside.ClosingCauseService;
import fr.insee.pearljam.domain.user.port.userside.UserService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
	private final UserService userService;

	@SneakyThrows
    @Override
	public ClosingCauseCountDto getClosingCauseCount(String userId, String campaignId, String interviewerId, Long date,
			List<String> associatedOrgUnits)  {
		ClosingCauseCountDto closingCauseCountDto = new ClosingCauseCountDto();
		userService.checkUserAssociationToCampaign(campaignId, userId);
		if (interviewerRepository.findById(interviewerId).isEmpty()) {
			throw new NotFoundException("No interviewer found for the id " + interviewerId);
		}
		List<String> userOuIds;
		if (!userId.equals(Constants.GUEST)) {
			userOuIds = utilsService.getRelatedOrganizationUnits(userId);
		} else {
			userOuIds = organizationUnitRepository.findAllId();
		}

		List<String> intervIds = interviewerRepository.findInterviewersByOrganizationUnits(associatedOrgUnits)
				.stream().map(Interviewer::getId).toList();
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
