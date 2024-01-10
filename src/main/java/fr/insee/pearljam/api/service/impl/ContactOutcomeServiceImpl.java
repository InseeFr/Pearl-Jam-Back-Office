package fr.insee.pearljam.api.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.dto.contactoutcome.ContactOutcomeTypeCountCampaignDto;
import fr.insee.pearljam.api.dto.contactoutcome.ContactOutcomeTypeCountDto;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.api.repository.CampaignRepository;
import fr.insee.pearljam.api.repository.ContactOutcomeRepository;
import fr.insee.pearljam.api.repository.InterviewerRepository;
import fr.insee.pearljam.api.repository.OrganizationUnitRepository;
import fr.insee.pearljam.api.repository.VisibilityRepository;
import fr.insee.pearljam.api.service.ContactOutcomeService;
import fr.insee.pearljam.api.service.UserService;
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
public class ContactOutcomeServiceImpl implements ContactOutcomeService {

	private final CampaignRepository campaignRepository;

	private final ContactOutcomeRepository contactOutcomeRepository;

	private final InterviewerRepository interviewerRepository;

	private final VisibilityRepository visibilityRepository;

	private final OrganizationUnitRepository organizationUnitRepository;

	private final UserService userService;

	private final UtilsService utilsService;

	@Override
	public ContactOutcomeTypeCountCampaignDto getContactOutcomeCountTypeByCampaign(String userId, String campaignId,
			Long date) throws NotFoundException {
		ContactOutcomeTypeCountCampaignDto stateCountCampaignDto = new ContactOutcomeTypeCountCampaignDto();
		if (!utilsService.checkUserCampaignOUConstraints(userId, campaignId)) {
			throw new NotFoundException(
					String.format("No campaign with id %s  associated to the user %s", campaignId, userId));
		}
		List<ContactOutcomeTypeCountDto> stateCountList = new ArrayList<>();
		Long dateToUse = date;
		if (dateToUse == null) {
			dateToUse = System.currentTimeMillis();
		}
		for (String id : organizationUnitRepository.findAllId()) {
			if (organizationUnitRepository.findChildren(id).isEmpty()
					&& visibilityRepository.findVisibilityByCampaignIdAndOuId(campaignId, id).isPresent()) {
				stateCountList.add(new ContactOutcomeTypeCountDto(id, organizationUnitRepository.findLabel(id),
						contactOutcomeRepository.getContactOutcomeTypeCountByCampaignAndOU(campaignId, id, dateToUse)));
			}
		}
		stateCountCampaignDto.setOrganizationUnits(stateCountList);
		stateCountCampaignDto.setFrance(new ContactOutcomeTypeCountDto(
				contactOutcomeRepository.getContactOutcomeTypeCountByCampaignId(campaignId, dateToUse)));
		if (stateCountCampaignDto.getFrance() == null || stateCountCampaignDto.getOrganizationUnits() == null) {
			throw new NotFoundException("No matching survey units states were found for the user " + userId
					+ " and the campaign " + campaignId);
		}
		return stateCountCampaignDto;
	}

	@Override
	public List<ContactOutcomeTypeCountDto> getContactOutcomeTypeCountByCampaign(String userId, Long date) {
		List<String> userOuIds;
		if (!userId.equals(Constants.GUEST)) {
			userOuIds = utilsService.getRelatedOrganizationUnits(userId);
		} else {
			userOuIds = organizationUnitRepository.findAllId();
		}
		final Long dateToUse = date == null ? System.currentTimeMillis() : date;
		List<String> lstCampaignUser = campaignRepository.findAllCampaignIdsByOuIds(userOuIds);

		return lstCampaignUser.stream()
				.map(idCampaign -> new ContactOutcomeTypeCountDto(
						contactOutcomeRepository.getContactOutcomeTypeCountByCampaignId(idCampaign, dateToUse),
						campaignRepository.findDtoById(idCampaign)))
				.collect(Collectors.toList());
	}

	@Override
	public ContactOutcomeTypeCountDto getNbSUNotAttributedContactOutcomes(String userId, String id, Long date)
			throws NotFoundException {
		if (!utilsService.checkUserCampaignOUConstraints(userId, id)) {
			throw new NotFoundException(String.format("No campaign with id %s associated to the user %s", id, userId));
		}

		List<String> organizationUnits = userService.getUserOUs(userId, true)
				.stream().map(OrganizationUnitDto::getId)
				.collect(Collectors.toList());
		Long dateToUse = date;
		if (dateToUse == null) {
			dateToUse = System.currentTimeMillis();
		}

		return new ContactOutcomeTypeCountDto(
				contactOutcomeRepository.findContactOutcomeTypeNotAttributed(id, organizationUnits, dateToUse));
	}

	@Override
	public ContactOutcomeTypeCountDto getContactOutcomeByInterviewerAndCampaign(String userId, String campaignId,
			String interviewerId, Long date) throws NotFoundException {
		if (!utilsService.checkUserCampaignOUConstraints(userId, campaignId)) {
			throw new NotFoundException(
					String.format("No campaign with id %s  associated to the user %s", campaignId, userId));
		}
		if (!interviewerRepository.findById(interviewerId).isPresent()
				|| !campaignRepository.findById(campaignId).isPresent()) {
			throw new NotFoundException(String.format("The interviewer %s or the campaign %s was not found in database",
					interviewerId, campaignId));
		}
		List<String> userOuIds;
		if (!userId.equals(Constants.GUEST)) {
			userOuIds = utilsService.getRelatedOrganizationUnits(userId);
		} else {
			userOuIds = organizationUnitRepository.findAllId();
		}
		Long dateToUse = date;
		if (dateToUse == null) {
			dateToUse = System.currentTimeMillis();
		}
		return new ContactOutcomeTypeCountDto(
				contactOutcomeRepository.findContactOutcomeTypeByInterviewerAndCampaign(campaignId, interviewerId,
						userOuIds, dateToUse));
	}
}
