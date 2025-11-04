package fr.insee.pearljam.campaign.domain.service;

import fr.insee.pearljam.campaign.domain.port.userside.CampaignService;
import fr.insee.pearljam.campaign.domain.port.userside.ReferentService;
import fr.insee.pearljam.campaign.infrastructure.persistence.jpa.repository.CampaignRepository;
import fr.insee.pearljam.campaign.infrastructure.rest.dto.input.*;
import fr.insee.pearljam.campaign.infrastructure.rest.dto.output.CampaignResponseDto;
import fr.insee.pearljam.campaign.infrastructure.rest.dto.output.VisibilityCampaignDto;
import fr.insee.pearljam.campaign.infrastructure.persistence.jpa.entity.Campaign;
import fr.insee.pearljam.configuration.web.exception.NotFoundException;
import fr.insee.pearljam.message.infrastructure.persistence.jpa.repository.MessageRepository;
import fr.insee.pearljam.organization.domain.port.userside.PreferenceService;
import fr.insee.pearljam.organization.domain.port.userside.UserService;
import fr.insee.pearljam.organization.domain.port.userside.UtilsService;
import fr.insee.pearljam.organization.infrastructure.persistence.jpa.repository.OrganizationUnitRepository;
import fr.insee.pearljam.organization.infrastructure.persistence.jpa.repository.UserRepository;
import fr.insee.pearljam.organization.infrastructure.persistence.jpa.entity.OrganizationUnit;
import fr.insee.pearljam.campaign.infrastructure.persistence.jpa.entity.Referent;
import fr.insee.pearljam.surveyunit.domain.port.userside.SurveyUnitService;
import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.repository.SurveyUnitRepository;
import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.SurveyUnit;
import fr.insee.pearljam.campaign.infrastructure.rest.dto.CampaignCommonsDto;
import fr.insee.pearljam.campaign.infrastructure.rest.dto.CampaignDto;
import fr.insee.pearljam.campaign.infrastructure.rest.dto.CampaignSensitivityDto;
import fr.insee.pearljam.campaign.infrastructure.rest.dto.CountDto;
import fr.insee.pearljam.surveyunit.infrastructure.rest.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.organization.infrastructure.rest.dto.OrganizationUnitDto;
import fr.insee.pearljam.campaign.infrastructure.rest.dto.ReferentDto;
import fr.insee.pearljam.campaign.domain.model.CampaignVisibility;
import fr.insee.pearljam.campaign.domain.model.Visibility;
import fr.insee.pearljam.campaign.domain.model.communication.CommunicationTemplate;
import fr.insee.pearljam.campaign.domain.port.userside.DateService;
import fr.insee.pearljam.campaign.domain.port.userside.VisibilityService;
import fr.insee.pearljam.campaign.domain.service.exception.*;
import fr.insee.pearljam.campaign.infrastructure.persistence.jpa.entity.CommunicationTemplateDB;
import fr.insee.pearljam.campaign.infrastructure.persistence.jpa.entity.VisibilityDB;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the Service for the Interviewer entity
 * 
 * @author scorcaud
 *
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class CampaignServiceImpl implements CampaignService {

	private static final String USER_CAMP_CONST_MSG = "No campaign with id %s  associated to the user %s";

	private final CampaignRepository campaignRepository;
	private final UserRepository userRepository;
	private final SurveyUnitRepository surveyUnitRepository;
	private final OrganizationUnitRepository organizationUnitRepository;
	private final MessageRepository messageRepository;
	private final UserService userService;
	private final UtilsService utilsService;
	private final SurveyUnitService surveyUnitService;
	private final PreferenceService preferenceService;
	private final ReferentService referentService;
	private final VisibilityService visibilityService;
	private final DateService dateService;

	@Override
	public List<CampaignDto> getListCampaign(String userId) {

		List<OrganizationUnitDto> organizationUnits = userService.getUserOUs(userId, true);

		List<String> campaignDtoIds = organizationUnits.stream()
				.map(OrganizationUnitDto::getId)
				.flatMap(ouId -> campaignRepository.findIdsByOuId(ouId).stream())
				.distinct()
				.toList();

		List<String> lstOuId = organizationUnits.stream()
				.map(OrganizationUnitDto::getId).toList();

		List<CampaignDto> campaignDtoReturned = new ArrayList<>();
		for (String idCampaign : campaignDtoIds) {
			CampaignDto campaign = campaignRepository.findDtoById(idCampaign);

			CampaignVisibility campaignVisibility = visibilityService.getCampaignVisibility(idCampaign, lstOuId);
			campaign.setManagementStartDate(campaignVisibility.managementStartDate());
			campaign.setInterviewerStartDate(campaignVisibility.interviewerStartDate());
			campaign.setIdentificationPhaseStartDate(campaignVisibility.identificationPhaseStartDate());
			campaign.setCollectionStartDate(campaignVisibility.collectionStartDate());
			campaign.setCollectionEndDate(campaignVisibility.collectionEndDate());
			campaign.setEndDate(campaignVisibility.endDate());
			campaign.setCampaignStats(surveyUnitRepository.getCampaignStats(idCampaign, lstOuId));
			campaign.setPreference(isUserPreference(userId, idCampaign));
			campaign.setReferents(referentService.findByCampaignId(idCampaign));
			campaignDtoReturned.add(campaign);
		}
		return campaignDtoReturned;
	}

	@Override
	public List<InterviewerDto> getListInterviewers(String userId, String campaignId) throws NotFoundException {
		List<InterviewerDto> interviewersDtoReturned = new ArrayList<>();
		if (!utilsService.checkUserCampaignOUConstraints(userId, campaignId)) {
			throw new NotFoundException(String.format(USER_CAMP_CONST_MSG, campaignId, userId));
		}

		List<OrganizationUnitDto> organizationUnits = userService.getUserOUs(userId, false);
		List<String> userOrgUnitIds = organizationUnits.stream().map(OrganizationUnitDto::getId)
				.toList();

		for (String orgId : campaignRepository.findAllOrganistionUnitIdByCampaignId(campaignId)) {
			if (userOrgUnitIds.contains(orgId)) {
				interviewersDtoReturned.addAll(
						campaignRepository.findInterviewersDtoByCampaignIdAndOrganisationUnitId(campaignId, orgId));
			}
		}
		if (interviewersDtoReturned.isEmpty()) {
			log.warn("No interviewers found for the campaign {}", campaignId);
		}
		return interviewersDtoReturned;
	}

	@Override
	public boolean isUserPreference(String userId, String campaignId) {
		return (campaignRepository.checkCampaignPreferences(userId, campaignId).isEmpty()) || "GUEST".equals(userId);
	}

	@Override
	public CountDto getNbSUAbandonedByCampaign(String userId, String campaignId) throws NotFoundException {
		int nbSUAbandoned = 0;
		if (!utilsService.checkUserCampaignOUConstraints(userId, campaignId)) {
			throw new NotFoundException(String.format(USER_CAMP_CONST_MSG, campaignId, userId));
		}
		return new CountDto(nbSUAbandoned);
	}

	@Override
	public CountDto getNbSUNotAttributedByCampaign(String userId, String campaignId) throws NotFoundException {
		int nbSUNotAttributed = 0;
		if (!utilsService.checkUserCampaignOUConstraints(userId, campaignId)) {
			throw new NotFoundException(String.format(USER_CAMP_CONST_MSG, campaignId, userId));
		}
		return new CountDto(nbSUNotAttributed);
	}

	@Override
	public void createCampaign(CampaignCreateDto campaignDto)
            throws CampaignAlreadyExistException, OrganizationalUnitNotFoundException, VisibilityHasInvalidDatesException {

		String campaignId = campaignDto.campaign().toUpperCase();
		Optional<Campaign> campOpt = campaignRepository.findById(campaignId);
		if (campOpt.isPresent()) {
			throw new CampaignAlreadyExistException();
		}

		boolean sensitivity=false;
		if(campaignDto.sensitivity()!=null){
			sensitivity=campaignDto.sensitivity();
		}

		// Creating campaign
		Campaign campaign = new Campaign(campaignId, campaignDto.campaignLabel(),
				campaignDto.identificationConfiguration(),
				campaignDto.contactOutcomeConfiguration(),
				campaignDto.contactAttemptConfiguration(),
				campaignDto.email(),
				sensitivity);
		campaign.setReferents(new ArrayList<>());
		campaign.setCommunicationTemplates(new ArrayList<>());

		List<VisibilityDB> visibilitiesDBToCreate = new ArrayList<>();
		List<Visibility> visibilities = VisibilityCampaignCreateDto.toModel(campaignDto.visibilities(), campaignDto.campaign());
		for (Visibility visibility : visibilities) {
			if(!Visibility.isValid(visibility)) {
				throw new VisibilityHasInvalidDatesException();
			}
			OrganizationUnit organizationUnit = organizationUnitRepository.findById(visibility.organizationalUnitId())
					.orElseThrow(OrganizationalUnitNotFoundException::new);
			visibilitiesDBToCreate.add(VisibilityDB.fromModel(visibility, campaign, organizationUnit));
		}
		campaign.setVisibilities(visibilitiesDBToCreate);

		if(campaignDto.referents() != null) {
			updateReferents(campaign, campaignDto.referents());
		}

		List<CommunicationTemplate> communicationTemplatesToCreate = CommunicationTemplateCreateDto.toModel(campaignDto.communicationTemplates(), campaignId);
		if(communicationTemplatesToCreate != null) {
			List<CommunicationTemplateDB> communicationsDBToCreate = CommunicationTemplateDB.fromModel(communicationTemplatesToCreate, campaign);
			campaign.setCommunicationTemplates(communicationsDBToCreate);
		}
		campaignRepository.save(campaign);
	}

	@Override
	public Optional<Campaign> findById(String campaignId) {
		return campaignRepository.findById(campaignId);
	}

	@Override
	public void delete(String campaignId, boolean force) throws CampaignNotFoundException, CampaignOnGoingException {
		Campaign campaign = findById(campaignId)
				.orElseThrow(CampaignNotFoundException::new);

		if (!force && isCampaignOngoing(campaignId)) {
			throw new CampaignOnGoingException();
		}
		surveyUnitRepository.findByCampaignId(campaign.getId())
				.forEach(surveyunit -> surveyUnitService.delete(surveyunit.getId()));
		userRepository.findAll()
				.forEach(user -> {
					List<String> lstCampaignId = new ArrayList<>(user.getCampaigns().stream().map(Campaign::getId)
							.toList());
					if (lstCampaignId.contains(campaign.getId())) {
						lstCampaignId.remove(lstCampaignId.indexOf(campaign.getId()));
						preferenceService.setPreferences(lstCampaignId, user.getId());
					}
				});
		messageRepository.deleteCampaignMessageRecipientByCampaignId(campaign.getId());
		campaignRepository.delete(campaign);
	}

	@Override
	public void updateCampaign(String campaignId, CampaignUpdateDto campaignToUpdate) throws CampaignNotFoundException, OrganizationalUnitNotFoundException, VisibilityNotFoundException, VisibilityHasInvalidDatesException {
		Campaign currentCampaign = campaignRepository.findByIdIgnoreCase(campaignId)
				.orElseThrow(CampaignNotFoundException::new);

		if(campaignToUpdate.visibilities() != null) {
			List<Visibility> visibilitiesToUpdate = VisibilityCampaignUpdateDto.toModel(campaignToUpdate.visibilities(), campaignId);
			for (Visibility visibilityToUpdate : visibilitiesToUpdate) {
				visibilityService.updateVisibility(visibilityToUpdate);
			}
		}

		currentCampaign.setLabel(campaignToUpdate.campaignLabel());
		if (!StringUtils.isBlank(campaignToUpdate.email())) {
			currentCampaign.setEmail(campaignToUpdate.email());
		}

		updateConfiguration(currentCampaign, campaignToUpdate);
		if(campaignToUpdate.referents() != null) {
			updateReferents(currentCampaign, campaignToUpdate.referents());
		}

		campaignRepository.save(currentCampaign);
	}

	private void updateConfiguration(Campaign currentCampaign, CampaignUpdateDto campDto) {

		// identificationConfiguration should not be updated anymore
		if (campDto.contactOutcomeConfiguration() != null) {
			currentCampaign.setContactOutcomeConfiguration(campDto.contactOutcomeConfiguration());
		}
		if (campDto.contactAttemptConfiguration() != null) {
			currentCampaign.setContactAttemptConfiguration(campDto.contactAttemptConfiguration());
		}
	}

	@Override
	public List<CampaignDto> getAllCampaigns() {
		List<String> lstOuId = organizationUnitRepository.findAllId();
		return campaignRepository.findAllDto().stream().map(camp -> {
			camp.setCampaignStats(surveyUnitRepository.getCampaignStats(camp.getId(), lstOuId));
			return camp;
		}).toList();
	}

	@Override
	public List<CampaignDto> getInterviewerCampaigns(String userId) {

		Map<String, String> map = surveyUnitRepository.findByInterviewerIdIgnoreCase(userId).stream()
				.collect(Collectors.toMap(su -> su.getCampaign().getId(), SurveyUnit::getId,
						(existing, replacement) -> existing));

		return map.entrySet().stream()
				.filter(entry -> surveyUnitService.canBeSeenByInterviewer(entry.getValue()))
				.map(entry -> campaignRepository.findDtoById(entry.getKey())).collect((Collectors.toList()));
	}

	@Override
	public boolean isCampaignOngoing(String campaignId) throws CampaignNotFoundException {
		Campaign campaign = findById(campaignId)
				.orElseThrow(CampaignNotFoundException::new);
		List<Visibility> visibilities = visibilityService.findVisibilities(campaign.getId());
		return visibilities.stream()
				.anyMatch(visibility -> visibility.endDate() > dateService.getCurrentTimestamp());
	}

	private void updateReferents(Campaign campaign, @NonNull List<ReferentDto> referentDtos) {
		List<Referent> referents = campaign.getReferents();
		referents.clear();
		referentDtos.forEach(refDto -> {
			Referent ref = new Referent();
			ref.setCampaign(campaign);
			ref.setFirstName(refDto.getFirstName());
			ref.setLastName(refDto.getLastName());
			ref.setPhoneNumber(refDto.getPhoneNumber());
			ref.setRole(refDto.getRole());
			referents.add(ref);
		});
	}

	@Override
	public CampaignResponseDto getCampaignDtoById(String campaignId) throws CampaignNotFoundException {
		Campaign campaignDB = campaignRepository.findById(campaignId)
				.orElseThrow(CampaignNotFoundException::new);
		List<ReferentDto> referents = referentService.findByCampaignId(campaignId);
		List<VisibilityCampaignDto> visibilities = VisibilityCampaignDto.fromModel(
				visibilityService.findVisibilities(campaignId)
		);
		return CampaignResponseDto.fromModel(campaignDB, referents, visibilities);
	}

	@Override
	public List<CampaignSensitivityDto> getCampaignSensitivityDto() {
		return campaignRepository.findAll().stream().map(CampaignSensitivityDto::fromModel).toList();
	}

	@Override
	public CampaignCommonsDto findCampaignCommonsById(String campaignId) throws CampaignNotFoundException {
		Campaign campaign = campaignRepository.findById(campaignId)
				.orElseThrow(CampaignNotFoundException::new);
		return new CampaignCommonsDto(
				campaign.getId(),
				"LUNATIC_NORMAL",
				campaign.getSensitivity(),
				campaign.getContactAttemptConfiguration().name());
	}

	@Override
	public List<CampaignCommonsDto> findCampaignsCommonsOngoing() throws CampaignNotFoundException {
		List<CampaignCommonsDto> campaignsCommonsOngoing = new ArrayList<>();
		List<Campaign> campaigns = campaignRepository.findAll();
		for (Campaign campaign : campaigns) {
			if (isCampaignOngoing(campaign.getId())) {
				campaignsCommonsOngoing.add(new CampaignCommonsDto(
							campaign.getId(),
							"LUNATIC_NORMAL",
							campaign.getSensitivity(),
							campaign.getContactAttemptConfiguration().name())
				);
			}
		}
		return campaignsCommonsOngoing;
	}
}
