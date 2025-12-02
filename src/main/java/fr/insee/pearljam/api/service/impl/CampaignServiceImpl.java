package fr.insee.pearljam.api.service.impl;

import fr.insee.pearljam.api.campaign.dto.input.*;
import fr.insee.pearljam.api.campaign.dto.input.CommunicationTemplateCreateDto;
import fr.insee.pearljam.api.campaign.dto.output.CampaignResponseDto;
import fr.insee.pearljam.api.campaign.dto.output.VisibilityCampaignDto;
import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.api.domain.OrganizationUnit;
import fr.insee.pearljam.api.domain.Referent;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.campaign.CampaignCommonsDto;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.campaign.CampaignSensitivityDto;
import fr.insee.pearljam.api.dto.count.CountDto;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import fr.insee.pearljam.api.dto.referent.ReferentDto;
import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.api.repository.*;
import fr.insee.pearljam.api.service.*;
import fr.insee.pearljam.domain.campaign.model.CampaignVisibility;
import fr.insee.pearljam.domain.campaign.model.Visibility;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationTemplate;
import fr.insee.pearljam.domain.campaign.port.userside.DateService;
import fr.insee.pearljam.domain.campaign.port.userside.VisibilityService;
import fr.insee.pearljam.domain.exception.*;
import fr.insee.pearljam.infrastructure.campaign.entity.CommunicationTemplateDB;
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

		// Creating campaign
		Campaign campaign = new Campaign(campaignId, campaignDto.campaignLabel(),
				campaignDto.identificationConfiguration(),
				campaignDto.contactOutcomeConfiguration(),
				campaignDto.contactAttemptConfiguration(),
				campaignDto.email(),
				campaignDto.sensitivity(),
				campaignDto.collectNextContacts());
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
			List<CommunicationTemplateDB> communicationsDBToCreate = CommunicationTemplateDB.fromModel(communicationTemplatesToCreate, campaign);
			campaign.setCommunicationTemplates(communicationsDBToCreate);
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
						lstCampaignId.remove(campaign.getId());
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
		currentCampaign.setCollectNextContacts(campaignToUpdate.collectNextContacts());

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
