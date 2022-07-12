package fr.insee.pearljam.api.service.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.api.domain.OrganizationUnit;
import fr.insee.pearljam.api.domain.Referent;
import fr.insee.pearljam.api.domain.Response;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.domain.Visibility;
import fr.insee.pearljam.api.domain.VisibilityId;
import fr.insee.pearljam.api.dto.campaign.CampaignContextDto;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.count.CountDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import fr.insee.pearljam.api.dto.visibility.VisibilityContextDto;
import fr.insee.pearljam.api.dto.visibility.VisibilityDto;
import fr.insee.pearljam.api.exception.NoOrganizationUnitException;
import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.api.exception.VisibilityException;
import fr.insee.pearljam.api.repository.CampaignRepository;
import fr.insee.pearljam.api.repository.ClosingCauseRepository;
import fr.insee.pearljam.api.repository.ContactOutcomeRepository;
import fr.insee.pearljam.api.repository.InterviewerRepository;
import fr.insee.pearljam.api.repository.MessageRepository;
import fr.insee.pearljam.api.repository.OrganizationUnitRepository;
import fr.insee.pearljam.api.repository.ReferentRepository;
import fr.insee.pearljam.api.repository.StateRepository;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.api.repository.UserRepository;
import fr.insee.pearljam.api.repository.VisibilityRepository;
import fr.insee.pearljam.api.service.CampaignService;
import fr.insee.pearljam.api.service.PreferenceService;
import fr.insee.pearljam.api.service.SurveyUnitService;
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
public class CampaignServiceImpl implements CampaignService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CampaignServiceImpl.class);
	private static final String USER_CAMP_CONST_MSG = "No campaign with id %s  associated to the user %s";

	@Autowired
	CampaignRepository campaignRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ContactOutcomeRepository contactOutcomeRepository;

	@Autowired
	StateRepository stateRepository;
	
	@Autowired
	ClosingCauseRepository closingCauseRepository;

	@Autowired
	InterviewerRepository interviewerRepository;

	@Autowired
	SurveyUnitRepository surveyUnitRepository;

	@Autowired
	VisibilityRepository visibilityRepository;

	@Autowired
	OrganizationUnitRepository organizationUnitRepository;
	
	@Autowired
	MessageRepository messageRepository;

	@Autowired
	ReferentRepository referentRepository;
	
	@Autowired
	UserService userService;

	@Autowired
	UtilsService utilsService;
	
	@Autowired
	SurveyUnitService surveyUnitService;
	
	@Autowired
	PreferenceService preferenceService;

	@Override
	public List<CampaignDto> getListCampaign(String userId) {
		List<CampaignDto> campaignDtoReturned = new ArrayList<>();
		List<String> campaignDtoIds = new ArrayList<>();
		List<OrganizationUnitDto> organizationUnits = userService.getUserOUs(userId, true);

		for (OrganizationUnitDto orgaUser : organizationUnits) {
			List<String> ids = campaignRepository.findIdsByOuId(orgaUser.getId());
			for (String id : ids) {
				if (!campaignDtoIds.contains(id)) {
					campaignDtoIds.add(id);
				}
			}
		}
		List<String> lstOuId = new ArrayList<>();

		if (!userId.equals(Constants.GUEST)) {
			lstOuId = organizationUnits.stream().map(OrganizationUnitDto::getId).collect(Collectors.toList());
		} else {
			lstOuId.add(Constants.GUEST);
		}
		for (String idCampaign : campaignDtoIds) {
			CampaignDto campaign = campaignRepository.findDtoById(idCampaign);
			VisibilityDto visibility = visibilityRepository.findVisibilityByCampaignId(idCampaign, lstOuId);
			campaign.setManagementStartDate(visibility.getManagementStartDate());
			campaign.setInterviewerStartDate(visibility.getInterviewerStartDate());
			campaign.setIdentificationPhaseStartDate(visibility.getIdentificationPhaseStartDate());
			campaign.setCollectionStartDate(visibility.getCollectionStartDate());
			campaign.setCollectionEndDate(visibility.getCollectionEndDate());
			campaign.setEndDate(visibility.getEndDate());
			campaign.setCampaignStats(surveyUnitRepository.getCampaignStats(idCampaign, lstOuId));
			campaign.setPreference(isUserPreference(userId, idCampaign));
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
				.collect(Collectors.toList());

		for (String orgId : campaignRepository.findAllOrganistionUnitIdByCampaignId(campaignId)) {
			if (userOrgUnitIds.contains(orgId)) {
				interviewersDtoReturned.addAll(
						campaignRepository.findInterviewersDtoByCampaignIdAndOrganisationUnitId(campaignId, orgId));
			}
		}
		if (interviewersDtoReturned.isEmpty()) {
			LOGGER.warn("No interviewers found for the campaign {}", campaignId);
		}
		return interviewersDtoReturned;
	}

	@Override
	public HttpStatus updateVisibility(String idCampaign, String idOu, VisibilityDto updatedVisibility) {
		if (idCampaign == null || idOu == null || !updatedVisibility.isOneDateFilled()) {
			LOGGER.error("Required fields missing in input body");
			return HttpStatus.BAD_REQUEST;
		}
		Optional<Visibility> visibility = visibilityRepository.findVisibilityByCampaignIdAndOuId(idCampaign, idOu);
		if (!visibility.isPresent()) {
			LOGGER.error("No visibility found for campaign {}", idCampaign);
			return HttpStatus.NOT_FOUND;
		}
		VisibilityDto expectedVisibility = mergeVisibilities(visibility.get(), updatedVisibility);
		if (!checkDateConsistency(expectedVisibility)) {
			LOGGER.warn("Invalid Visibility dates : should be strictly increasing");
			return HttpStatus.CONFLICT;
		}
		setVisibilityDates(visibility.get(), updatedVisibility, idOu, idCampaign);
		return HttpStatus.OK;
	}

	private VisibilityDto mergeVisibilities(Visibility initialVisibility, VisibilityDto updatedVisibility) {
		VisibilityDto expectedVisibility = new VisibilityDto(initialVisibility.getManagementStartDate(),
				initialVisibility.getInterviewerStartDate(),
				initialVisibility.getIdentificationPhaseStartDate(),
				initialVisibility.getCollectionStartDate(),
				initialVisibility.getCollectionEndDate(),
				initialVisibility.getEndDate());

		if (updatedVisibility.getManagementStartDate() != null) {
			expectedVisibility.setManagementStartDate(updatedVisibility.getManagementStartDate());
		}
		if (updatedVisibility.getInterviewerStartDate() != null) {
			expectedVisibility.setInterviewerStartDate(updatedVisibility.getInterviewerStartDate());
		}
		if (updatedVisibility.getIdentificationPhaseStartDate() != null) {
			expectedVisibility.setIdentificationPhaseStartDate(updatedVisibility.getIdentificationPhaseStartDate());
		}
		if (updatedVisibility.getCollectionStartDate() != null) {
			expectedVisibility.setCollectionStartDate(updatedVisibility.getCollectionStartDate());
		}
		if (updatedVisibility.getCollectionEndDate() != null) {
			expectedVisibility.setCollectionEndDate(updatedVisibility.getCollectionEndDate());
		}
		if (updatedVisibility.getEndDate() != null) {
			expectedVisibility.setEndDate(updatedVisibility.getEndDate());
		}
		return expectedVisibility;
	}

	private boolean checkDateConsistency(VisibilityDto visibility) {
		return visibility.getManagementStartDate() < visibility.getInterviewerStartDate()
				&& visibility.getInterviewerStartDate() < visibility.getIdentificationPhaseStartDate()
				&& visibility.getIdentificationPhaseStartDate() < visibility.getCollectionStartDate()
				&& visibility.getCollectionStartDate() < visibility.getCollectionEndDate()
				&& visibility.getCollectionEndDate() < visibility.getEndDate();
	}

	private void setVisibilityDates(Visibility visibility, VisibilityDto updatedVisibility, String idOu, String idCampaign) {
		if (updatedVisibility.getManagementStartDate() != null) {
			LOGGER.info("Updating management start date for campaign {} and Organizational Unit {}", idCampaign,
					idOu);
			visibility.setManagementStartDate(updatedVisibility.getManagementStartDate());
		}
		if (updatedVisibility.getInterviewerStartDate() != null) {
			LOGGER.info("Updating interviewer start date for campaign {} and Organizational Unit {}",
					idCampaign, idOu);
			visibility.setInterviewerStartDate(updatedVisibility.getInterviewerStartDate());
		}
		if (updatedVisibility.getIdentificationPhaseStartDate() != null) {
			LOGGER.info("Updating identification phase start date for campaign {} and Organizational Unit {}",
					idCampaign, idOu);
			visibility.setIdentificationPhaseStartDate(updatedVisibility.getIdentificationPhaseStartDate());
		}
		if (updatedVisibility.getCollectionStartDate() != null) {
			LOGGER.info("Updating collection start date for campaign {} and Organizational Unit {}", idCampaign, idOu);
			visibility.setCollectionStartDate(updatedVisibility.getCollectionStartDate());
		}
		if (updatedVisibility.getCollectionEndDate() != null) {
			LOGGER.info("Updating collection end date for campaign {} and Organizational Unit {}", idCampaign,
					idOu);
			visibility.setCollectionEndDate(updatedVisibility.getCollectionEndDate());
		}
		if (updatedVisibility.getEndDate() != null) {
			LOGGER.info("Updating end date for campaign {} and Organizational Unit {}", idCampaign, idOu);
			visibility.setEndDate(updatedVisibility.getEndDate());
		}
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
	@Transactional(rollbackFor=Exception.class)
	public Response postCampaign(CampaignContextDto campaignDto) throws NoOrganizationUnitException, VisibilityException {
		if(campaignDto.getCampaign() == null) {
			return new Response("Campaign id is missing in request", HttpStatus.BAD_REQUEST);
		}
		if(campaignDto.getCampaignLabel() == null) {
			return new Response("Campaign label is missing in request", HttpStatus.BAD_REQUEST);
		}
		if(campaignDto.getVisibilities() == null) {
			return new Response("Campaign visibilities are missing in request", HttpStatus.BAD_REQUEST);
		}
		String campaignId = campaignDto.getCampaign().toUpperCase();
		Optional<Campaign> campOpt = campaignRepository.findById(campaignId);
		if(campOpt.isPresent()) {
			return  new Response("Campaign with id '" + campaignId + "' already exists", HttpStatus.BAD_REQUEST);
		}
		// Creating campaign
		Campaign campaign = new Campaign(campaignId, campaignDto.getCampaignLabel(),
				campaignDto.getIdentificationConfiguration(), campaignDto.getContactOutcomeConfiguration(),
				campaignDto.getContactAttemptConfiguration(),campaignDto.getEmail());
		campaignRepository.save(campaign);

		for(VisibilityContextDto dto : campaignDto.getVisibilities()) {
			if(!verifyVisibilityContextDto(dto)) {
				throw new VisibilityException("Some of the fields of a visibility are missing");
			}
			Optional<OrganizationUnit> ouOpt = organizationUnitRepository.findById(dto.getOrganizationalUnit());
			if(ouOpt.isPresent()) {
				Visibility visi = new Visibility();
				visi.setVisibilityId(new VisibilityId(dto.getOrganizationalUnit(), campaign.getId()));
				visi.setCampaign(campaign);
				visi.setOrganizationUnit(ouOpt.get());
				visi.setCollectionStartDate(dto.getCollectionStartDate());
				visi.setCollectionEndDate(dto.getCollectionEndDate());
				visi.setIdentificationPhaseStartDate(dto.getIdentificationPhaseStartDate());
				visi.setInterviewerStartDate(dto.getInterviewerStartDate());
				visi.setManagementStartDate(dto.getManagementStartDate());
				visi.setEndDate(dto.getEndDate());
				
				visibilityRepository.save(visi);
			}
			else {
				throw new NoOrganizationUnitException("Organizational unit '" + dto.getOrganizationalUnit()+ "' does not exist");
			}
		}
		persistReferents(campaignDto, campaign);

		return new Response("", HttpStatus.OK);
	}
	
	public boolean verifyVisibilityContextDto(VisibilityContextDto dto) {		
		return dto.getOrganizationalUnit() != null && dto.getCollectionStartDate() != null
				&& dto.getCollectionEndDate() != null && dto.getIdentificationPhaseStartDate() != null
				&& dto.getInterviewerStartDate() != null && dto.getManagementStartDate() != null
				&& dto.getEndDate() != null;
	}

	@Override
	public Optional<Campaign> findById(String id) {
		return campaignRepository.findById(id);
	}

	@Override
	public void delete(Campaign campaign) {
		surveyUnitRepository.findByCampaignId(campaign.getId()).stream().forEach(su -> surveyUnitService.delete(su));
		userRepository.findAll().stream()
				.forEach(user -> {
					List<String> lstCampaignId = user.getCampaigns().stream().map(Campaign::getId).collect(Collectors.toList());
					if(lstCampaignId.contains(campaign.getId())) {
						lstCampaignId.remove(lstCampaignId.indexOf(campaign.getId()));
						preferenceService.setPreferences(lstCampaignId, user.getId());
					}
				});
		messageRepository.deleteCampaignMessageRecipientByCampaignId(campaign.getId());
		campaignRepository.delete(campaign);
	}

	@Override
	public HttpStatus updateCampaign(String id, CampaignContextDto campaign) {
		Optional<Campaign> camp = campaignRepository.findByIdIgnoreCase(id);
		if (!camp.isPresent()) {
			LOGGER.error("Campaign {} does not exist in db", id);
			return HttpStatus.NOT_FOUND;
		}

		if (campaign.getCampaignLabel() == null || campaign.getCampaignLabel().isEmpty()
				|| campaign.getCampaignLabel().isBlank()
				|| !campaign.getVisibilities().stream().allMatch(VisibilityDto::isOneDateFilled)) {
			LOGGER.warn("Can't update campaign {} : invalid input", id);
			return HttpStatus.BAD_REQUEST;
		}

		boolean visibilitiesArePresent = campaign.getVisibilities().stream()
				.map(VisibilityContextDto::getOrganizationalUnit)
				.allMatch(ouId -> visibilityRepository.findByCampaignIdIgnoreCaseAndOrganizationUnitIdIgnoreCase(id, ouId).isPresent());
		if (!visibilitiesArePresent) {
			LOGGER.warn("Can't update missing visibility for campaign {}", id);
			return HttpStatus.NOT_FOUND;
		}

		boolean visibilitiesAreValid = campaign.getVisibilities().stream().allMatch(v -> checkDateConsistency(v));
		if (!visibilitiesAreValid) {
			LOGGER.warn("Invalid Visibility dates : should be strictly increasing");
			return HttpStatus.CONFLICT;
		}

		Campaign currentCampaign = camp.get();
		currentCampaign.setLabel(campaign.getCampaignLabel());
		if (!StringUtils.isBlank(campaign.getEmail())) {
			currentCampaign.setEmail(campaign.getEmail());
		}
		updateConfiguration(currentCampaign,campaign);
		updateReferents(currentCampaign,campaign);
		campaign.getVisibilities().stream().forEach(v -> this.updateVisibility(
				campaign.getCampaign(),
				v.getOrganizationalUnit(),
				new VisibilityDto(
						v.getManagementStartDate(),
						v.getInterviewerStartDate(),
						v.getIdentificationPhaseStartDate(),
						v.getCollectionStartDate(),
						v.getCollectionEndDate(),
						v.getEndDate())));
		return HttpStatus.OK;
	}

	private void updateReferents(Campaign currentCampaign, CampaignContextDto campDto) {
		if (campDto.getReferents() != null) {
			referentRepository.deleteAll(currentCampaign.getReferents());
			persistReferents(campDto, currentCampaign);
		}
	}

	private void updateConfiguration(Campaign currentCampaign, CampaignContextDto campDto) {

		if (campDto.getIdentificationConfiguration() != null) {
			currentCampaign.setIdentificationConfiguration(campDto.getIdentificationConfiguration());
		}
		if (campDto.getContactOutcomeConfiguration() != null) {
			currentCampaign.setContactOutcomeConfiguration(campDto.getContactOutcomeConfiguration());
		}
		if (campDto.getContactAttemptConfiguration() != null) {
			currentCampaign.setContactAttemptConfiguration(campDto.getContactAttemptConfiguration());
		}
	}

	@Override
	public List<CampaignDto> getAllCampaigns() {
		List<String> lstOuId = organizationUnitRepository.findAllId();
		return campaignRepository.findAllDto().stream().map(camp -> {
			camp.setCampaignStats(surveyUnitRepository.getCampaignStats(camp.getId(), lstOuId));
			return camp;
		}).collect(Collectors.toList());
	}

	@Override
	public List<CampaignDto> getInterviewerCampaigns(String userId) {

		Map<String, String> map = surveyUnitRepository.findByInterviewerIdIgnoreCase(userId).stream()
				.collect(Collectors.toMap(su -> su.getCampaign().getId(), SurveyUnit::getId,
						(existing, replacement) -> existing));

		List<CampaignDto> foundCampaigns = map.entrySet().stream()
				.filter(entry -> surveyUnitService.canBeSeenByInterviewer(entry.getValue()))
				.map(entry -> campaignRepository.findDtoById(entry.getKey())).collect((Collectors.toList()));

		return foundCampaigns;
	}

	@Override
	public boolean isCampaignOngoing(String campaignId) {
		List<Visibility> visibilities = visibilityRepository.findByCampaignId(campaignId);
		return visibilities.stream().anyMatch(visibility -> visibility.getEndDate()>Instant.now().toEpochMilli());
	}

	@Override
	public List<VisibilityContextDto> findAllVisiblitiesByCampaign(String campaignId) {
		return visibilityRepository.findByCampaignId(campaignId).stream().map(v -> {
			VisibilityContextDto vcd = new VisibilityContextDto();
			vcd.setCollectionEndDate(v.getCollectionEndDate());
			vcd.setCollectionStartDate(v.getCollectionStartDate());
			vcd.setEndDate(v.getEndDate());
			vcd.setInterviewerStartDate(v.getInterviewerStartDate());
			vcd.setIdentificationPhaseStartDate(v.getIdentificationPhaseStartDate());
			vcd.setManagementStartDate(v.getManagementStartDate());
			vcd.setOrganizationalUnit(v.getOrganizationUnit().getId());

			return vcd;
		}).collect(Collectors.toList());
	}

	@Override
	public void persistReferents(CampaignContextDto campaignDto, Campaign campaign){
		campaignDto.getReferents().stream().forEach(refDto -> {
			Referent ref = new Referent();
			ref.setCampaign(campaign);
			ref.setFirstName(refDto.getFirstName());
			ref.setLastName(refDto.getLastName());
			ref.setPhoneNumber(refDto.getPhoneNumber());
			ref.setRole(refDto.getRole());
			referentRepository.save(ref);
		});
	}

}
 