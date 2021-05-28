package fr.insee.pearljam.api.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.api.domain.OrganizationUnit;
import fr.insee.pearljam.api.domain.Response;
import fr.insee.pearljam.api.domain.Visibility;
import fr.insee.pearljam.api.domain.VisibilityId;
import fr.insee.pearljam.api.dto.campaign.CampaignContextDto;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.campaign.CollectionDatesDto;
import fr.insee.pearljam.api.dto.count.CountDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import fr.insee.pearljam.api.dto.visibility.VisibilityContextDto;
import fr.insee.pearljam.api.dto.visibility.VisibilityDto;
import fr.insee.pearljam.api.exception.NoOrganizationUnitException;
import fr.insee.pearljam.api.exception.VisibilityException;
import fr.insee.pearljam.api.repository.CampaignRepository;
import fr.insee.pearljam.api.repository.ClosingCauseRepository;
import fr.insee.pearljam.api.repository.ContactOutcomeRepository;
import fr.insee.pearljam.api.repository.InterviewerRepository;
import fr.insee.pearljam.api.repository.OrganizationUnitRepository;
import fr.insee.pearljam.api.repository.StateRepository;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.api.repository.UserRepository;
import fr.insee.pearljam.api.repository.VisibilityRepository;
import fr.insee.pearljam.api.service.CampaignService;
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
	UserService userService;

	@Autowired
	UtilsService utilsService;

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
	public List<InterviewerDto> getListInterviewers(String userId, String campaignId) {
		List<InterviewerDto> interviewersDtoReturned = new ArrayList<>();
		if (!utilsService.checkUserCampaignOUConstraints(userId, campaignId)) {
			return null;
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
			LOGGER.error("No interviewers found for the campaign {}", campaignId);
		}
		return interviewersDtoReturned;
	}

	@Override
	public HttpStatus updateVisibility(String idCampaign, String idOu, VisibilityDto updatedVisibility) {
		HttpStatus returnCode = HttpStatus.BAD_REQUEST;
		if (idCampaign != null && idOu != null && updatedVisibility.isOneDateFilled()) {
			Optional<Visibility> visibility = visibilityRepository.findVisibilityByCampaignIdAndOuId(idCampaign, idOu);
			if (visibility.isPresent()) {
				setVisibilityDates(visibility.get(), updatedVisibility, idOu, idCampaign);
				if (visibility.get().checkDateConsistency()) {
					visibilityRepository.save(visibility.get());
					returnCode = HttpStatus.OK;
				}
			} else {
				returnCode = HttpStatus.NOT_FOUND;
			}
		}
		return returnCode;
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
		return !(campaignRepository.checkCampaignPreferences(userId, campaignId).isEmpty()) || "GUEST".equals(userId);
	}

	@Override
	public HttpStatus updateDates(String userId, String id, CollectionDatesDto campaign) {
		HttpStatus returnStatus = HttpStatus.BAD_REQUEST;
		Optional<Campaign> camp = campaignRepository.findByIdIgnoreCase(id);
		if (camp.isPresent()) {
			Campaign currentCampaign = camp.get();
			if (campaign.getEndDate() != null) {
				LOGGER.info("Updating collection end date for campaign {}", id);
				currentCampaign.setEndDate(campaign.getEndDate());
				returnStatus = HttpStatus.OK;
			}
			if (campaign.getStartDate() != null) {
				LOGGER.info("Updating collection start date for campaign {}", id);
				currentCampaign.setStartDate(campaign.getStartDate());
				returnStatus = HttpStatus.OK;
			}
			campaignRepository.save(currentCampaign);
		} else {
			LOGGER.info("Campaign {} does not exist", id);
			returnStatus = HttpStatus.NOT_FOUND;
		}

		return returnStatus;
	}

	@Override
	public CountDto getNbSUAbandonedByCampaign(String userId, String campaignId) {
		int nbSUAbandoned = 0;
		if (!utilsService.checkUserCampaignOUConstraints(userId, campaignId)) {
			return null;
		}
		return new CountDto(nbSUAbandoned);
	}

	@Override
	public CountDto getNbSUNotAttributedByCampaign(String userId, String campaignId) {
		int nbSUNotAttributed = 0;
		if (!utilsService.checkUserCampaignOUConstraints(userId, campaignId)) {
			return null;
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
		Optional<Campaign> campOpt = campaignRepository.findById(campaignDto.getCampaign());
		if(campOpt.isPresent()) {
			return  new Response("Campaign with id '" + campaignDto.getCampaign() + "' already exists", HttpStatus.BAD_REQUEST);
		}
		// Creating campaign
		Campaign campaign = new Campaign(campaignDto.getCampaign(), campaignDto.getCampaignLabel());
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
		
		return new Response("", HttpStatus.OK);
	}
	
	public boolean verifyVisibilityContextDto(VisibilityContextDto dto) {		
		return dto.getOrganizationalUnit() != null && dto.getCollectionStartDate() != null
				&& dto.getCollectionEndDate() != null && dto.getIdentificationPhaseStartDate() != null
				&& dto.getInterviewerStartDate() != null && dto.getManagementStartDate() != null
				&& dto.getEndDate() != null;
	}



}
