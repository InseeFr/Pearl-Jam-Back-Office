package fr.insee.pearljam.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.api.domain.Interviewer;
import fr.insee.pearljam.api.domain.Visibility;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.campaign.CollectionDatesDto;
import fr.insee.pearljam.api.dto.count.CountDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import fr.insee.pearljam.api.dto.state.StateCountCampaignDto;
import fr.insee.pearljam.api.dto.state.StateCountDto;
import fr.insee.pearljam.api.dto.visibility.VisibilityDto;
import fr.insee.pearljam.api.repository.CampaignRepository;
import fr.insee.pearljam.api.repository.InterviewerRepository;
import fr.insee.pearljam.api.repository.OrganizationUnitRepository;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.api.repository.UserRepository;
import fr.insee.pearljam.api.repository.VisibilityRepository;

/**
 * Implementation of the Service for the Interviewer entity
 * 
 * @author scorcaud
 *
 */
@Service
public class CampaignServiceImpl implements CampaignService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CampaignServiceImpl.class);

	@Autowired
	CampaignRepository campaignRepository;

	@Autowired
	UserRepository userRepository;

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

	    if(!userId.equals(Constants.GUEST)){
	      lstOuId = organizationUnits.stream().map(OrganizationUnitDto::getId).collect(Collectors.toList());
	    }
	    else{
	      lstOuId.add(Constants.GUEST);
	    }
		for(String idCampaign : campaignDtoIds) {
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

	public List<InterviewerDto> getListInterviewers(String userId, String campaignId) {
		List<InterviewerDto> interviewersDtoReturned = new ArrayList<>();
		if (!utilsService.checkUserCampaignOUConstraints(userId, campaignId)) {
			return null;
		}
    
	    List<OrganizationUnitDto> organizationUnits = userService.getUserOUs(userId, false);
	    List<String> userOrgUnitIds = organizationUnits.stream()
	      .map(OrganizationUnitDto::getId)
	      .collect(Collectors.toList());

		for (String orgId : campaignRepository.findAllOrganistionUnitIdByCampaignId(campaignId)) {
	      if(userOrgUnitIds.contains(orgId)){
	        interviewersDtoReturned
	        .addAll(campaignRepository.findInterviewersDtoByCampaignIdAndOrganisationUnitId(campaignId, orgId));
	      }
		}
		if (interviewersDtoReturned.isEmpty()) {
			LOGGER.error("No interviewers found for the campaign {}", campaignId);
		}
		return interviewersDtoReturned;
	}

	public StateCountDto getStateCount(String userId, String campaignId, String interviewerId, Long date,
			List<String> associatedOrgUnits) {
		StateCountDto stateCountDto = new StateCountDto();
		if (!utilsService.checkUserCampaignOUConstraints(userId, campaignId)) {
			return null;
		}
		Optional<Interviewer> interv = interviewerRepository.findByIdIgnoreCase(interviewerId);
		Long dateToUse = date;
		if (dateToUse == null) {
			dateToUse = -1L;
		}
		if (interv.isPresent()
				&& (associatedOrgUnits.contains(interv.get().getOrganizationUnit().getId()) || userId.equals(Constants.GUEST))) {
			stateCountDto = new StateCountDto(campaignRepository.getStateCount(campaignId, interviewerId, dateToUse));
    }
		if (stateCountDto.getTotal() == null) {
			LOGGER.error("No matching interviewers {} were found for the user {} and the campaign {}", interviewerId,
					userId, campaignId);
			return null;
		}
		return stateCountDto;
	}

	public StateCountCampaignDto getStateCountByCampaign(String userId, String campaignId, Long date) {
		StateCountCampaignDto stateCountCampaignDto = new StateCountCampaignDto();
		if (!utilsService.checkUserCampaignOUConstraints(userId, campaignId)) {
			return null;
		}
		List<StateCountDto> stateCountList = new ArrayList<>();
		Long dateToUse = date;
		if (dateToUse == null) {
			dateToUse = -1L;
		}
		for (String id : organizationUnitRepository.findAllId()) {
			if(organizationUnitRepository.findChildren(id).isEmpty()) {
				stateCountList.add(new StateCountDto(id, organizationUnitRepository.findLabel(id), campaignRepository.getStateCountByCampaignAndOU(campaignId, id, dateToUse)));
			}
		}
		stateCountCampaignDto.setOrganizationUnits(stateCountList);
		stateCountCampaignDto.setFrance(new StateCountDto(campaignRepository.getStateCountByCampaignId(campaignId, dateToUse)));
		if (stateCountCampaignDto.getFrance() == null || stateCountCampaignDto.getOrganizationUnits() == null
				|| stateCountCampaignDto.getOrganizationUnits().isEmpty()) {
			LOGGER.error("No matching survey units states were found for the user {} and the campaign {}", userId, campaignId);
			return null;
		}
		return stateCountCampaignDto;
	}
	
	public List<StateCountDto> getStateCountByCampaigns(String userId, Long date){
		List<StateCountDto> returnList = new ArrayList<>();
		List<OrganizationUnitDto> organizationUnits = userService.getUserOUs(userId, true);
		for (OrganizationUnitDto dto : organizationUnits) {
			LOGGER.info(dto.getId());
		}
		List<String> userOrgUnitIds = organizationUnits.stream()
	      .map(OrganizationUnitDto::getId)
	      .collect(Collectors.toList());
		Long dateToUse = date;
		if (dateToUse == null) {
			dateToUse = System.currentTimeMillis();
		}
		List<String> campaignIds = campaignRepository.findAllIdsVisible(userOrgUnitIds, dateToUse);
		for(String id : campaignIds) {
			StateCountDto campaignSum = new StateCountDto(campaignRepository.getStateCountSumByCampaign(id, userOrgUnitIds, dateToUse));
			if(campaignSum.getTotal() != null) {
				CampaignDto dto = campaignRepository.findDtoById(id);
				campaignSum.setCampaign(dto);
				returnList.add(campaignSum);
			}
		}
		return returnList;
	}
	
	public List<StateCountDto> getStateCountByInterviewer(String userId, Long date){
		List<StateCountDto> returnList = new ArrayList<>();
		List<OrganizationUnitDto> organizationUnits = userService.getUserOUs(userId, true);
		List<String> userOrgUnitIds = organizationUnits.stream()
	      .map(OrganizationUnitDto::getId)
	      .collect(Collectors.toList());
		Long dateToUse = date;
		if (dateToUse == null) {
			dateToUse = System.currentTimeMillis();
		}
		List<String> interviewerIds =interviewerRepository.findIdsByOrganizationUnits(userOrgUnitIds);
		List<String> campaignIds = campaignRepository.findAllIdsVisible(userOrgUnitIds, dateToUse);
		for(String id : interviewerIds) {
			StateCountDto interviewerSum = new StateCountDto(campaignRepository.getStateCountSumByInterviewer(campaignIds, id, dateToUse));
			if(interviewerSum.getTotal() != null) {
				interviewerSum.setInterviewer(interviewerRepository.findDtoById(id));
				returnList.add(interviewerSum);
			}
		}
		return returnList;
	}
	
	public HttpStatus updateVisibility(String idCampaign, String idOu, VisibilityDto updatedVisibility) {
		HttpStatus returnCode = HttpStatus.BAD_REQUEST;
		if(idCampaign != null && idOu != null && (updatedVisibility.isOneDateFilled())) {
			Optional<Visibility> visibility = visibilityRepository.findVisibilityByCampaignIdAndOuId(idCampaign, idOu);
			if(visibility.isPresent()) {
				if(updatedVisibility.getManagementStartDate() != null) {
					LOGGER.info("Updating management start date for campaign {} and Organizational Unit {}", idCampaign, idOu);
					visibility.get().setManagementStartDate(updatedVisibility.getManagementStartDate());
				}
				if(updatedVisibility.getInterviewerStartDate() != null) {
					LOGGER.info("Updating interviewer start date for campaign {} and Organizational Unit {}", idCampaign, idOu);
					visibility.get().setInterviewerStartDate(updatedVisibility.getInterviewerStartDate());
				}
				if(updatedVisibility.getIdentificationPhaseStartDate() != null) {
					LOGGER.info("Updating identification phase start date for campaign {} and Organizational Unit {}", idCampaign, idOu);
					visibility.get().setIdentificationPhaseStartDate(updatedVisibility.getIdentificationPhaseStartDate());
				}
				if(updatedVisibility.getCollectionStartDate() != null) {
					LOGGER.info("Updating collection start date for campaign {} and Organizational Unit {}", idCampaign, idOu);
					visibility.get().setCollectionStartDate(updatedVisibility.getCollectionStartDate());
				}
				if(updatedVisibility.getCollectionEndDate() != null) {
					LOGGER.info("Updating collection end date for campaign {} and Organizational Unit {}", idCampaign, idOu);
					visibility.get().setCollectionEndDate(updatedVisibility.getCollectionEndDate());
				}
				if(updatedVisibility.getEndDate() != null) {
					LOGGER.info("Updating end date for campaign {} and Organizational Unit {}", idCampaign, idOu);
					visibility.get().setEndDate(updatedVisibility.getEndDate());
				}
				visibilityRepository.save(visibility.get());
				returnCode = HttpStatus.OK;
			}
		}
		return returnCode;
	}

	public boolean isUserPreference(String userId, String campaignId) {
		return !(campaignRepository.checkCampaignPreferences(userId, campaignId).isEmpty()) || "GUEST".equals(userId);
	}
	
	@Override
	public HttpStatus updateDates(String userId, String id, CollectionDatesDto campaign) {
		HttpStatus returnStatus = HttpStatus.BAD_REQUEST;
		Optional<Campaign> camp = campaignRepository.findByIdIgnoreCase(id);
		if (camp.isPresent()) {
			Campaign currentCampaign = camp.get();
			if(campaign.getEndDate() != null) {
				LOGGER.info("Updating collection end date for campaign {}", id);
				currentCampaign.setEndDate(campaign.getEndDate());
				returnStatus = HttpStatus.OK;
			}
			if(campaign.getStartDate() != null) {
				LOGGER.info("Updating collection start date for campaign {}", id);
				currentCampaign.setStartDate(campaign.getStartDate());
				returnStatus = HttpStatus.OK;
			}
			campaignRepository.save(currentCampaign);
		}
		else {
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


}
