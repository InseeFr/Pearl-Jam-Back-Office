package fr.insee.pearljam.domain.campaign.service;

import fr.insee.pearljam.contracts.campaign.dto.*;
import fr.insee.pearljam.contracts.campaign.dto.input.*;
import fr.insee.pearljam.contracts.campaign.dto.output.CampaignResponseDto;
import fr.insee.pearljam.contracts.campaign.dto.output.VisibilityCampaignDto;
import fr.insee.pearljam.contracts.organizationunit.dto.OrganizationUnitDto;
import fr.insee.pearljam.domain.campaign.CampaignPreferenceModel;
import fr.insee.pearljam.domain.campaign.readmodel.CampaignVisibility;
import fr.insee.pearljam.domain.campaign.model.SurveyUnitCounts;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationTemplate;
import fr.insee.pearljam.domain.campaign.port.in.*;
import fr.insee.pearljam.domain.campaign.port.out.CampaignRepository;
import fr.insee.pearljam.domain.campaign.port.out.CampaignVisibilityPort;
import fr.insee.pearljam.domain.campaign.port.out.ReferentRepository;
import fr.insee.pearljam.domain.campaign.service.exception.*;
import fr.insee.pearljam.domain.campaign.service.model.Visibility;
import fr.insee.pearljam.domain.message.port.out.MessageRepository;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.port.out.OrganizationUnitRepository;
import fr.insee.pearljam.domain.organizationunit.port.out.UserRepository;
import fr.insee.pearljam.domain.reporting.readmodel.progress.CampaignPhase;
import fr.insee.pearljam.domain.surveyunit.model.count.InterviewerCount;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitCountService;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitService;
import fr.insee.pearljam.domain.surveyunit.port.out.InterviewerCountRepository;
import fr.insee.pearljam.domain.surveyunit.port.out.SurveyUnitRepository;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CampaignDB;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CommunicationTemplateDB;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.ReferentDB;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.VisibilityDB;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.OrganizationUnitDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.SurveyUnitDB;
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

    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;
    private final SurveyUnitRepository surveyUnitRepository;
    private final OrganizationUnitRepository organizationUnitRepository;
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final SurveyUnitService surveyUnitService;
    private final PreferenceService preferenceService;
    private final ReferentService referentService;
    private final ReferentRepository referentRepository;
    private final VisibilityService visibilityService;
    private final CampaignVisibilityPort campaignVisibilityPort;
    private final DateService dateService;
    private final InterviewerCountRepository interviewerCountRepository;
    private final SurveyUnitCountService surveyUnitCountService;

    /**
     * @deprecated will be removed/refactored
     */
    @Override
    @Deprecated(forRemoval = true)
    public List<CampaignDto> getPreferredCampaigns(String userId) {

        List<String> organizationUnitIds = userService
                .getUserOUs(userId, true)
                .stream()
                .map(OrganizationUnitDto::getId)
                .toList();

        Long currentTimestamp = dateService.getCurrentTimestamp();
        List<CampaignDto> userCampaigns = campaignRepository.findByUserAndManagementVisibility(organizationUnitIds, userId, currentTimestamp);

        for (CampaignDto campaign : userCampaigns) {
            CampaignVisibility campaignVisibility = campaignVisibilityPort.getCampaignVisibility(campaign.getId(), organizationUnitIds);
            campaign.setManagementStartDate(campaignVisibility.managementStartDate());
            campaign.setInterviewerStartDate(campaignVisibility.interviewerStartDate());
            campaign.setIdentificationPhaseStartDate(campaignVisibility.identificationPhaseStartDate());
            campaign.setCollectionStartDate(campaignVisibility.collectionStartDate());
            campaign.setCollectionEndDate(campaignVisibility.collectionEndDate());
            campaign.setEndDate(campaignVisibility.endDate());
            campaign.setCampaignStats(surveyUnitRepository.getCampaignStats(campaign.getId(), organizationUnitIds));
            campaign.setReferents(referentRepository.findByCampaignId(campaign.getId())
                    .stream()
                    .map(ref -> new ReferentDto(ref.getFirstName(), ref.getLastName(), ref.getPhoneNumber(), ref.getRole()))
                    .toList());
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

        return campaignRepository.findByOuIdWithPreference(organizationUnitIds, userId, dateService.getCurrentTimestamp());
    }

    @Override
    public List<CampaignPreferenceModel> getCampaignPreferencesForSpecificPhase(String userId, CampaignPhase campaignPhase) {
        List<CampaignDto> campaigns = getPreferredCampaigns(userId);

        List<CampaignDto> campaignsFilteredForPhase = campaigns.stream().filter(c -> CampaignPhase.fromDates(
                dateService.getCurrentTimestamp(),
                c.getManagementStartDate(),
                c.getInterviewerStartDate(),
                c.getCollectionEndDate(),
                c.getEndDate()).equals(campaignPhase)).toList();

        return campaignsFilteredForPhase.stream()
                .map(c -> new CampaignPreferenceModel(c.getId(), c.getLabel(), true)).toList();

    }

    @Override
    public CountDto getNbSUAbandonedByCampaign(String userId, String campaignId) throws CampaignNotFoundException {
        int nbSUAbandoned = 0;
        userService.checkUserAssociationToCampaign(campaignId, userId);
        return new CountDto(nbSUAbandoned);
    }

    @Override
    public CountDto getNbSUNotAttributedByCampaign(String userId, String campaignId) throws CampaignNotFoundException {
        int nbSUNotAttributed = 0;
        userService.checkUserAssociationToCampaign(campaignId, userId);
        return new CountDto(nbSUNotAttributed);
    }

    @Override
    public void createCampaign(CampaignCreateDto campaignDto)
            throws CampaignAlreadyExistException, OrganizationalUnitNotFoundException, VisibilityHasInvalidDatesException {

        String campaignId = campaignDto.campaign().toUpperCase();
        Optional<CampaignDB> campOpt = campaignRepository.findById(campaignId);
        if (campOpt.isPresent()) {
            throw new CampaignAlreadyExistException();
        }

        // Creating campaign
        CampaignDB campaign = new CampaignDB(campaignId, campaignDto.campaignLabel(),
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
            if (!Visibility.isValid(visibility)) {
                throw new VisibilityHasInvalidDatesException();
            }
            OrganizationUnitDB organizationUnit = organizationUnitRepository.findById(visibility.organizationalUnitId())
                    .orElseThrow(OrganizationalUnitNotFoundException::new);
            visibilitiesDBToCreate.add(VisibilityDB.fromModel(visibility, campaign, organizationUnit));
        }
        campaign.setVisibilities(visibilitiesDBToCreate);

        if (campaignDto.referents() != null) {
            updateReferents(campaign, campaignDto.referents());
        }

        List<CommunicationTemplate> communicationTemplatesToCreate = CommunicationTemplateCreateDto.toModel(campaignDto.communicationTemplates(), campaignId);
        List<CommunicationTemplateDB> communicationsDBToCreate = CommunicationTemplateDB.fromModel(communicationTemplatesToCreate, campaign);
        campaign.setCommunicationTemplates(communicationsDBToCreate);
        campaignRepository.save(campaign);
    }

    @Override
    public Optional<CampaignDB> findById(String campaignId) {
        return campaignRepository.findById(campaignId);
    }

    @Override
    public void delete(String campaignId, boolean force) throws CampaignNotFoundException, CampaignOnGoingException {
        CampaignDB campaign = findById(campaignId)
                .orElseThrow(CampaignNotFoundException::new);

        if (!force && isCampaignOngoing(campaignId)) {
            throw new CampaignOnGoingException();
        }
        surveyUnitRepository.findByCampaignId(campaign.getId())
                .forEach(surveyunit -> surveyUnitService.delete(surveyunit.getId()));
        userRepository.findAll()
                .forEach(user -> {
                    List<String> lstCampaignId = new ArrayList<>(user.getCampaigns().stream().map(CampaignDB::getId)
                            .toList());
                    if (lstCampaignId.contains(campaign.getId())) {
                        lstCampaignId.remove(campaign.getId());
                        try {
                            preferenceService.setPreferences(lstCampaignId, user.getId());
                        } catch (CampaignNotFoundException _) {
                            // campaign already checked
                        }
                    }
                });
        messageRepository.deleteCampaignMessageRecipientByCampaignId(campaign.getId());
        campaignRepository.delete(campaign);
    }

    @Override
    public void updateCampaign(String campaignId, CampaignUpdateDto campaignToUpdate) throws CampaignNotFoundException, VisibilityNotFoundException, VisibilityHasInvalidDatesException {
        CampaignDB currentCampaign = campaignRepository.findByIdIgnoreCase(campaignId)
                .orElseThrow(CampaignNotFoundException::new);

        if (campaignToUpdate.visibilities() != null) {
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
        if (campaignToUpdate.referents() != null) {
            updateReferents(currentCampaign, campaignToUpdate.referents());
        }
        currentCampaign.setCollectNextContacts(campaignToUpdate.collectNextContacts());

        campaignRepository.save(currentCampaign);
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
        return campaignRepository.findAllDto().stream().map(camp -> {
            camp.setCampaignStats(surveyUnitRepository.getCampaignStats(camp.getId(), lstOuId));
            return camp;
        }).toList();
    }

    @Override
    public List<CampaignDto> getInterviewerCampaigns(String userId) {

        Map<String, String> map = surveyUnitRepository.findByInterviewerIdIgnoreCase(userId).stream()
                .collect(Collectors.toMap(su -> su.getCampaign().getId(), SurveyUnitDB::getId,
                        (existing, replacement) -> existing));

        return map.entrySet().stream()
                .filter(entry -> surveyUnitService.canBeSeenByInterviewer(entry.getValue()))
                .map(entry -> campaignRepository.findDtoById(entry.getKey())).collect((Collectors.toList()));
    }

    @Override
    public boolean isCampaignOngoing(String campaignId) throws CampaignNotFoundException {
        CampaignDB campaign = findById(campaignId)
                .orElseThrow(CampaignNotFoundException::new);
        List<Visibility> visibilities = visibilityService.findVisibilities(campaign.getId());
        return visibilities.stream()
                .anyMatch(visibility -> visibility.endDate() > dateService.getCurrentTimestamp());
    }

    private void updateReferents(CampaignDB campaign, @NonNull List<ReferentDto> referentDtos) {
        List<ReferentDB> referents = campaign.getReferents();
        referents.clear();
        referentDtos.forEach(refDto -> {
            ReferentDB ref = new ReferentDB();
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
        CampaignDB campaignDB = campaignRepository.findById(campaignId)
                .orElseThrow(CampaignNotFoundException::new);
        List<ReferentDto> referents = referentService.findByCampaignId(campaignId);
        List<VisibilityCampaignDto> visibilities = VisibilityCampaignDto.fromModel(
                visibilityService.findVisibilities(campaignId)
        );
        return CampaignResponseDto.fromModel(
                campaignDB.getId(),
                campaignDB.getLabel(),
                campaignDB.getEmail(),
                campaignDB.getIdentificationConfiguration(),
                campaignDB.getContactOutcomeConfiguration(),
                campaignDB.getContactAttemptConfiguration(),
                campaignDB.getSensitivity(),
                referents,
                visibilities);
    }

    @Override
    public List<CampaignSensitivityDto> getCampaignSensitivityDto() {
        return campaignRepository.findAll().stream()
                .map(campaign -> CampaignSensitivityDto.fromModel(campaign.getId(), campaign.getSensitivity()))
                .toList();
    }

    @Override
    public CampaignCommonsDto findCampaignCommonsById(String campaignId) throws CampaignNotFoundException {
        CampaignDB campaign = campaignRepository.findById(campaignId)
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
        List<CampaignDB> campaigns = campaignRepository.findAll();
        for (CampaignDB campaign : campaigns) {
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

    @Override
    public PortalDataDto findCampaignPortalData(String campaignId, String userId) throws CampaignNotFoundException {
        // Check user association to campaign (security check)
        userService.checkUserAssociationToCampaign(campaignId, userId);

        CampaignDB campaign = campaignRepository.findById(campaignId)
                .orElseThrow(CampaignNotFoundException::new);

        // Get user's organization units for visibility filtering
        List<String> organizationUnitIds = userService.getUserOUs(userId, true)
                .stream()
                .map(OrganizationUnitDto::getId)
                .toList();

        // Get referents
        List<ReferentDto> referents = referentService.findByCampaignId(campaignId);

        // Get interviewers with their survey unit counts (filtered by organization units)
        List<InterviewerCount> interviewerCounts = interviewerCountRepository.findCampaignInterviewers(campaignId, organizationUnitIds);

        // Get campaign visibility dates (filtered by organization units)
        CampaignVisibility campaignVisibility = campaignVisibilityPort.getCampaignVisibility(campaignId, organizationUnitIds);

        // Get survey unit counts from domain service
        SurveyUnitCounts surveyUnitCounts = surveyUnitCountService.getSurveyUnitCounts(campaignId, organizationUnitIds);

        log.info("[{}] get {} portal data : {} interviewers / {} abandoned / {} unallocated / {} total ",
                userId, campaignId,
                interviewerCounts.size(),
                surveyUnitCounts.abandoned(),
                surveyUnitCounts.unallocated(),
                surveyUnitCounts.total());

        return PortalDataDto.fromModel(
                campaign.getId(),
                campaign.getLabel(),
                campaign.getEmail(),
                campaignVisibility,
                referents,
                interviewerCounts,
                surveyUnitCounts
        );
    }
}
