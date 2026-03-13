package fr.insee.pearljam.api.service.impl;

import fr.insee.pearljam.api.campaign.dto.input.*;
import fr.insee.pearljam.api.campaign.dto.input.CommunicationTemplateCreateDto;
import fr.insee.pearljam.api.campaign.dto.output.CampaignResponseDto;
import fr.insee.pearljam.api.campaign.dto.output.VisibilityCampaignDto;
import fr.insee.pearljam.api.domain.OrganizationUnit;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.campaign.CampaignCommonsDto;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.campaign.CampaignPreferenceDto;
import fr.insee.pearljam.api.dto.campaign.CampaignSensitivityDto;
import fr.insee.pearljam.api.dto.count.CountDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import fr.insee.pearljam.api.dto.referent.ReferentDto;
import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.api.repository.MessageRepository;
import fr.insee.pearljam.api.repository.OrganizationUnitRepository;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.api.repository.UserRepository;
import fr.insee.pearljam.api.service.*;
import fr.insee.pearljam.infrastructure.campaign.jpa.CampaignJpaRepository;
import fr.insee.pearljam.domain.campaign.model.Campaign;
import fr.insee.pearljam.domain.campaign.model.CampaignVisibility;
import fr.insee.pearljam.domain.campaign.model.Visibility;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationTemplate;
import fr.insee.pearljam.domain.campaign.port.userside.DateService;
import fr.insee.pearljam.domain.campaign.port.userside.VisibilityService;
import fr.insee.pearljam.domain.exception.*;
import fr.insee.pearljam.infrastructure.campaign.entity.CampaignDB;
import fr.insee.pearljam.infrastructure.campaign.entity.CommunicationTemplateDB;
import fr.insee.pearljam.infrastructure.campaign.entity.ReferentDB;
import fr.insee.pearljam.infrastructure.campaign.entity.VisibilityDB;
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

	private final CampaignJpaRepository campaignJpaRepository;
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
	public List<CampaignDto> getPreferredCampaigns(String userId) {

		List<String> organizationUnitIds = userService
				.getUserOUs(userId, true)
				.stream()
				.map(OrganizationUnitDto::getId)
				.toList();

		Long currentTimestamp = dateService.getCurrentTimestamp();
		List<CampaignDto> userCampaigns = campaignJpaRepository.findByUserAndManagementVisibility(organizationUnitIds, userId, currentTimestamp);

		for (CampaignDto campaign : userCampaigns) {
			CampaignVisibility campaignVisibility = visibilityService.getCampaignVisibility(campaign.getId(), organizationUnitIds);
			campaign.setManagementStartDate(campaignVisibility.managementStartDate());
			campaign.setInterviewerStartDate(campaignVisibility.interviewerStartDate());
			campaign.setIdentificationPhaseStartDate(campaignVisibility.identificationPhaseStartDate());
			campaign.setCollectionStartDate(campaignVisibility.collectionStartDate());
			campaign.setCollectionEndDate(campaignVisibility.collectionEndDate());
			campaign.setEndDate(campaignVisibility.endDate());
			campaign.setCampaignStats(surveyUnitRepository.getCampaignStats(campaign.getId(), organizationUnitIds));
			campaign.setReferents(referentService.findByCampaignId(campaign.getId()));
		}
		return userCampaigns;
	}

	@Override
	public List<CampaignPreferenceDto> getCampaignPreferences(String userId) {

		List<String> organizationUnitIds = userService
				.getUserOUs(userId, true)
				.stream()
				.map(OrganizationUnitDto::getId)
				.toList();

		return campaignJpaRepository.findByOuIdWithPreference(organizationUnitIds, userId, dateService.getCurrentTimestamp());
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

		for (String orgId : campaignJpaRepository.findAllOrganistionUnitIdByCampaignId(campaignId)) {
			if (userOrgUnitIds.contains(orgId)) {
				interviewersDtoReturned.addAll(
						campaignJpaRepository.findInterviewersDtoByCampaignIdAndOrganisationUnitId(campaignId, orgId));
			}
		}
		if (interviewersDtoReturned.isEmpty()) {
			log.warn("No interviewers found for the campaign {}", campaignId);
		}
		return interviewersDtoReturned;
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
		Optional<CampaignDB> campOpt = campaignJpaRepository.findById(campaignId);
		if (campOpt.isPresent()) {
			throw new CampaignAlreadyExistException();
		}

		// Creating campaign
		CampaignDB campaignDB = CampaignDB.fromModel(new Campaign(campaignId, campaignDto.campaignLabel(),
				campaignDto.identificationConfiguration(),
				campaignDto.contactOutcomeConfiguration(),
				campaignDto.contactAttemptConfiguration(),
				campaignDto.email(),
				campaignDto.sensitivity(),
				campaignDto.collectNextContacts()));
		campaignDB.setReferents(new ArrayList<>());
		campaignDB.setCommunicationTemplates(new ArrayList<>());

		List<VisibilityDB> visibilitiesDBToCreate = new ArrayList<>();
		List<Visibility> visibilities = VisibilityCampaignCreateDto.toModel(campaignDto.visibilities(), campaignDto.campaign());
		for (Visibility visibility : visibilities) {
			if(!Visibility.isValid(visibility)) {
				throw new VisibilityHasInvalidDatesException();
			}
			OrganizationUnit organizationUnit = organizationUnitRepository.findById(visibility.organizationalUnitId())
					.orElseThrow(OrganizationalUnitNotFoundException::new);
			visibilitiesDBToCreate.add(VisibilityDB.fromModel(visibility, campaignDB, organizationUnit));
		}
		campaignDB.setVisibilities(visibilitiesDBToCreate);

		if(campaignDto.referents() != null) {
			updateReferents(campaignDB, campaignDto.referents());
		}

		List<CommunicationTemplate> communicationTemplatesToCreate = CommunicationTemplateCreateDto.toModel(campaignDto.communicationTemplates(), campaignId);
			List<CommunicationTemplateDB> communicationsDBToCreate = CommunicationTemplateDB.fromModel(communicationTemplatesToCreate, campaignDB);
			campaignDB.setCommunicationTemplates(communicationsDBToCreate);
		campaignJpaRepository.save(campaignDB);
	}

	@Override
	public Optional<Campaign> findById(String campaignId) {
		return campaignJpaRepository.findById(campaignId).map(CampaignDB::toModel);
	}

	@Override
	public void delete(String campaignId, boolean force) throws CampaignNotFoundException, CampaignOnGoingException {
		CampaignDB campaignDB = campaignJpaRepository.findById(campaignId)
				.orElseThrow(CampaignNotFoundException::new);

		if (!force && isCampaignOngoing(campaignId)) {
			throw new CampaignOnGoingException();
		}
		surveyUnitRepository.findByCampaignId(campaignDB.getId())
				.forEach(surveyunit -> surveyUnitService.delete(surveyunit.getId()));
		userRepository.findAll()
				.forEach(user -> {
					List<String> lstCampaignId = new ArrayList<>(user.getCampaigns().stream().map(CampaignDB::getId)
							.toList());
					if (lstCampaignId.contains(campaignDB.getId())) {
						lstCampaignId.remove(campaignDB.getId());
						preferenceService.setPreferences(lstCampaignId, user.getId());
					}
				});
		messageRepository.deleteCampaignMessageRecipientByCampaignId(campaignDB.getId());
		campaignJpaRepository.delete(campaignDB);
	}

	@Override
	public void updateCampaign(String campaignId, CampaignUpdateDto campaignToUpdate) throws CampaignNotFoundException, OrganizationalUnitNotFoundException, VisibilityNotFoundException, VisibilityHasInvalidDatesException {
		CampaignDB currentCampaign = campaignJpaRepository.findByIdIgnoreCase(campaignId)
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
		currentCampaign.setCollectNextContacts(campaignToUpdate.collectNextContacts());

		campaignJpaRepository.save(currentCampaign);
	}

	private void updateConfiguration(CampaignDB currentCampaign, CampaignUpdateDto campDto) {

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
		return campaignJpaRepository.findAllDto().stream().map(camp -> {
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
				.map(entry -> campaignJpaRepository.findDtoById(entry.getKey())).collect((Collectors.toList()));
	}

	@Override
	public boolean isCampaignOngoing(String campaignId) throws CampaignNotFoundException {
		if (!campaignJpaRepository.existsById(campaignId)) {
			throw new CampaignNotFoundException();
		}
		List<Visibility> visibilities = visibilityService.findVisibilities(campaignId);
		return visibilities.stream()
				.anyMatch(visibility -> visibility.endDate() > dateService.getCurrentTimestamp());
	}

	private void updateReferents(CampaignDB campaignDB, @NonNull List<ReferentDto> referentDtos) {
		List<ReferentDB> referents = campaignDB.getReferents();
		referents.clear();
		referentDtos.forEach(refDto -> {
			ReferentDB ref = new ReferentDB();
			ref.setCampaign(campaignDB);
			ref.setFirstName(refDto.getFirstName());
			ref.setLastName(refDto.getLastName());
			ref.setPhoneNumber(refDto.getPhoneNumber());
			ref.setRole(refDto.getRole());
			referents.add(ref);
		});
	}

	@Override
	public CampaignResponseDto getCampaignDtoById(String campaignId) throws CampaignNotFoundException {
		CampaignDB campaignDB = campaignJpaRepository.findById(campaignId)
				.orElseThrow(CampaignNotFoundException::new);
		List<ReferentDto> referents = referentService.findByCampaignId(campaignId);
		List<VisibilityCampaignDto> visibilities = VisibilityCampaignDto.fromModel(
				visibilityService.findVisibilities(campaignId)
		);
		return CampaignResponseDto.fromModel(campaignDB, referents, visibilities);
	}

	@Override
	public List<CampaignSensitivityDto> getCampaignSensitivityDto() {
		return campaignJpaRepository.findAll().stream().map(CampaignSensitivityDto::fromModel).toList();
	}

	@Override
	public CampaignCommonsDto findCampaignCommonsById(String campaignId) throws CampaignNotFoundException {
		CampaignDB campaignDB = campaignJpaRepository.findById(campaignId)
				.orElseThrow(CampaignNotFoundException::new);
		return new CampaignCommonsDto(
				campaignDB.getId(),
				"LUNATIC_NORMAL",
				campaignDB.getSensitivity(),
				campaignDB.getContactAttemptConfiguration().name());
	}

	@Override
	public List<CampaignCommonsDto> findCampaignsCommonsOngoing() throws CampaignNotFoundException {
		List<CampaignCommonsDto> campaignsCommonsOngoing = new ArrayList<>();
		List<CampaignDB> campaigns = campaignJpaRepository.findAll();
		for (CampaignDB campaignDB : campaigns) {
			if (isCampaignOngoing(campaignDB.getId())) {
				campaignsCommonsOngoing.add(new CampaignCommonsDto(
							campaignDB.getId(),
							"LUNATIC_NORMAL",
							campaignDB.getSensitivity(),
							campaignDB.getContactAttemptConfiguration().name())
				);
			}
		}
		return campaignsCommonsOngoing;
	}
}
