package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.contracts.surveyunit.dto.contactoutcome.ContactOutcomeTypeCountCampaignDto;
import fr.insee.pearljam.contracts.surveyunit.dto.contactoutcome.ContactOutcomeTypeCountDto;
import fr.insee.pearljam.contracts.organizationunit.dto.OrganizationUnitDto;
import fr.insee.pearljam.domain.campaign.port.out.CampaignRepository;
import fr.insee.pearljam.domain.campaign.port.out.VisibilityRepository;
import fr.insee.pearljam.domain.organizationunit.port.in.RelatedOrganizationUnitService;
import fr.insee.pearljam.domain.surveyunit.port.out.ContactOutcomeRepository;
import fr.insee.pearljam.domain.surveyunit.port.in.ContactOutcomeService;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.shared.exception.EntityNotFoundException;
import fr.insee.pearljam.domain.surveyunit.port.out.InterviewerRepository;
import fr.insee.pearljam.domain.organizationunit.port.out.OrganizationUnitRepository;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.surveyunit.service.exception.InterviewerNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
public class ContactOutcomeServiceImpl implements ContactOutcomeService {

	private final CampaignRepository campaignRepository;

	private final ContactOutcomeRepository contactOutcomeRepository;

	private final InterviewerRepository interviewerRepository;

	private final VisibilityRepository visibilityRepository;

	private final OrganizationUnitRepository organizationUnitRepository;

	private final UserService userService;

	private final RelatedOrganizationUnitService relatedOrganizationUnitService;

	@Override
	public ContactOutcomeTypeCountCampaignDto getContactOutcomeCountTypeByCampaign(String userId, String campaignId,
			Long date) throws EntityNotFoundException {
		ContactOutcomeTypeCountCampaignDto stateCountCampaignDto = new ContactOutcomeTypeCountCampaignDto();
		userService.checkUserAssociationToCampaign(campaignId, userId);
		List<ContactOutcomeTypeCountDto> stateCountList = new ArrayList<>();
		Long dateToUse = date;
		if (dateToUse == null) {
			dateToUse = System.currentTimeMillis();
		}
		for (String id : organizationUnitRepository.findAllId()) {
			if (organizationUnitRepository.findChildren(id).isEmpty()
					&& visibilityRepository.findVisibility(campaignId, id).isPresent()) {
				stateCountList.add(new ContactOutcomeTypeCountDto(id, organizationUnitRepository.findLabel(id),
						contactOutcomeRepository.getContactOutcomeTypeCountByCampaignAndOU(campaignId, id, dateToUse)));
			}
		}
		stateCountCampaignDto.setOrganizationUnits(stateCountList);
		stateCountCampaignDto.setFrance(new ContactOutcomeTypeCountDto(
				contactOutcomeRepository.getContactOutcomeTypeCountByCampaignId(campaignId, dateToUse)));
		if (stateCountCampaignDto.getFrance() == null || stateCountCampaignDto.getOrganizationUnits() == null) {
			throw new EntityNotFoundException("No matching survey units states were found for the user " + userId
					+ " and the campaign " + campaignId);
		}
		return stateCountCampaignDto;
	}

	@Override
	public List<ContactOutcomeTypeCountDto> getContactOutcomeTypeCountByCampaign(String userId, Long date) {
		List<String> userOuIds = relatedOrganizationUnitService.getRelatedOrganizationUnits(userId);
		final Long dateToUse = date == null ? System.currentTimeMillis() : date;
		List<String> lstCampaignUser = campaignRepository.findAllCampaignIdsByOuIds(userOuIds);

		return lstCampaignUser.stream()
				.map(idCampaign -> new ContactOutcomeTypeCountDto(
						contactOutcomeRepository.getContactOutcomeTypeCountByCampaignId(idCampaign, dateToUse),
						campaignRepository.findDtoById(idCampaign)))
				.toList();
	}

	@Override
	public ContactOutcomeTypeCountDto getNbSUNotAttributedContactOutcomes(String userId, String campaignId, Long date) throws CampaignNotFoundException {
		userService.checkUserAssociationToCampaign(campaignId, userId) ;

		List<String> organizationUnits = userService.getUserOUs(userId, true)
				.stream().map(OrganizationUnitDto::getId)
				.toList();
		Long dateToUse = date;
		if (dateToUse == null) {
			dateToUse = System.currentTimeMillis();
		}

		return new ContactOutcomeTypeCountDto(
				contactOutcomeRepository.findContactOutcomeTypeNotAttributed(campaignId, organizationUnits, dateToUse));
	}

	@Override
	public ContactOutcomeTypeCountDto getContactOutcomeByInterviewerAndCampaign(String userId, String campaignId,
			String interviewerId, Long date) throws EntityNotFoundException {
		userService.checkUserAssociationToCampaign(campaignId, userId);
		if (interviewerRepository.findById(interviewerId).isEmpty()) {
			throw new InterviewerNotFoundException(interviewerId);
		}
		if (campaignRepository.findById(campaignId).isEmpty()) {
			throw new CampaignNotFoundException();
		}
		List<String> userOuIds = relatedOrganizationUnitService.getRelatedOrganizationUnits(userId);

		Long dateToUse = date;
		if (dateToUse == null) {
			dateToUse = System.currentTimeMillis();
		}
		return new ContactOutcomeTypeCountDto(
				contactOutcomeRepository.findContactOutcomeTypeByInterviewerAndCampaign(campaignId, interviewerId,
						userOuIds, dateToUse));
	}
}
