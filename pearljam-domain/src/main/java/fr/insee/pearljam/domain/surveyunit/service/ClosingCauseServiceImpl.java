package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.InterviewerDB;
import fr.insee.pearljam.api.surveyunit.dto.closingcause.ClosingCauseCountDto;
import fr.insee.pearljam.api.web.exception.NotFoundException;
import fr.insee.pearljam.domain.surveyunit.port.out.ClosingCauseRepository;
import fr.insee.pearljam.domain.organizationunit.port.in.RelatedOrganizationUnitService;
import fr.insee.pearljam.domain.surveyunit.port.out.InterviewerRepository;
import fr.insee.pearljam.domain.organizationunit.port.out.OrganizationUnitRepository;
import fr.insee.pearljam.domain.surveyunit.port.out.StateRepository;
import fr.insee.pearljam.domain.surveyunit.port.in.ClosingCauseService;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
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
	private final RelatedOrganizationUnitService relatedOrganizationUnitService;
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
		List<String> userOuIds = relatedOrganizationUnitService.getRelatedOrganizationUnits(userId);

		List<String> intervIds = interviewerRepository.findInterviewersByOrganizationUnits(associatedOrgUnits)
				.stream().map(InterviewerDB::getId).toList();
		Long dateToUse = date;
		if (dateToUse == null) {
			dateToUse = System.currentTimeMillis();
		}
		if (!intervIds.isEmpty() && (intervIds.contains(interviewerId))) {
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
