package fr.insee.pearljam.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.domain.Interviewer;
import fr.insee.pearljam.api.domain.User;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.count.CountDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import fr.insee.pearljam.api.dto.state.StateCountCampaignDto;
import fr.insee.pearljam.api.dto.state.StateCountDto;
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
		List<OrganizationUnitDto> organizationUnits = new ArrayList<>();
		if (userId.equals(Constants.GUEST)) {
			campaignDtoIds = campaignRepository.findAllIds();
		} else {
			Optional<User> user = userRepository.findByIdIgnoreCase(userId);
			if (user.isPresent()) {
				userService.getOrganizationUnits(organizationUnits, user.get().getOrganizationUnit(), true);
				if (!organizationUnits.isEmpty()) {
					for (OrganizationUnitDto orgaUser : organizationUnits) {
						List<String> ids = campaignRepository.findIdsByOuId(orgaUser.getId());
						for (String id : ids) {
							if (!campaignDtoIds.contains(id)) {
								campaignDtoIds.add(id);
							}
						}
					}
				} else {
					LOGGER.error("No campaign found for user {}", userId);
					return List.of();
				}
			} else {
				LOGGER.error("User {} does not exist", userId);
				return List.of();
			}
		}
		if (campaignDtoIds.isEmpty()) {
			LOGGER.error("No campaign found for user {}", userId);
			return List.of();
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
			campaign.setVisibilityStartDate(visibilityRepository.findVisibilityStartDateByCampaignId(idCampaign, lstOuId));
			campaign.setTreatmentEndDate(visibilityRepository.findTreatmentEndDateByCampaignId(idCampaign, lstOuId));
			campaign.setCampaignStats(surveyUnitRepository.getCampaignStats(idCampaign, lstOuId));
			campaign.setPreference(isUserPreference(userId, idCampaign));
			campaignDtoReturned.add(campaign);
		}
		return campaignDtoReturned;
	}

	public List<InterviewerDto> getListInterviewers(String userId, String campaignId) {
		List<InterviewerDto> interviewersDtoReturned = new ArrayList<>();
		if (!utilsService.checkUserCampaignOUConstraints(userId, campaignId)) {
			return List.of();
		}
		for (String orgId : campaignRepository.findAllOrganistionUnitIdByCampaignId(campaignId)) {
			interviewersDtoReturned
					.addAll(campaignRepository.findInterviewersDtoByCampaignIdAndOrganisationUnitId(campaignId, orgId));
		}
		if (interviewersDtoReturned.isEmpty()) {
			LOGGER.error("No interviewers found for the campaign {}", campaignId);
			return List.of();
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
				&& (associatedOrgUnits.contains(interv.get().organizationUnit.id) || userId.equals(Constants.GUEST))) {
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

	public boolean isUserPreference(String userId, String campaignId) {
		return !(campaignRepository.checkCampaignPreferences(userId, campaignId).isEmpty()) || userId == "GUEST";
	}

	@Override
	public CountDto getNbSUAbandonedByCampaign(String userId, String campaignId) {
		if (!utilsService.checkUserCampaignOUConstraints(userId, campaignId)) {
			return null;
		}
		return new CountDto(0);
	}

	@Override
	public CountDto getNbSUNotAttributedByCampaign(String userId, String campaignId) {
		if (!utilsService.checkUserCampaignOUConstraints(userId, campaignId)) {
			return null;
		}
		return new CountDto(0);
	}
}
