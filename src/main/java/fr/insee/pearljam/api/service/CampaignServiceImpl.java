package fr.insee.pearljam.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.api.domain.Interviewer;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;
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
 * @author scorcaud
 *
 */
@Service
public class CampaignServiceImpl implements CampaignService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CampaignServiceImpl.class);
	
	private static final String GUEST = "GUEST";
	
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
	UtilsService utilsService;
	
	public List<CampaignDto> getListCampaign(String userId) {
		List<CampaignDto> campaignDtoReturned = new ArrayList<>();
		List<String> campaignDtoIds = null;
		if(userId.equals(GUEST) || utilsService.existUser(userId, "user")) {
			campaignDtoIds = campaignRepository.findAllIds();
		} else {
			campaignDtoIds = campaignRepository.findIdsByUserId(userId);
		}
		if(campaignDtoIds.isEmpty()) {
			LOGGER.error("No campaign found for user {}", userId);
			return List.of();
		}
		for(String idCampaign : campaignDtoIds) {
			if(isUserAssociatedToTheCampaign(userId, idCampaign)) {
				CampaignDto campaign = campaignRepository.findDtoById(idCampaign);
				campaign.setVisibilityStartDate(visibilityRepository.findVisibilityStartDateByCampaignId(idCampaign, userId));
				campaign.setAffected(surveyUnitRepository.getNbrOfSuForCampaign(idCampaign));
				campaign.setInProgress(surveyUnitRepository.getSuInProgressForCampaign(idCampaign));
				campaign.setTerminated(surveyUnitRepository.getSuTerminatedByCampaign(idCampaign));
				campaign.setToAffect(0L);
				campaign.setToControl(0L);
				campaign.setToFollowUp(0L);
				campaign.setPreference(true);
				campaignDtoReturned.add(campaign);
			}
		}
		return campaignDtoReturned;
	}

  public List<InterviewerDto> getListInterviewers(String userId, String campaignId, List<String> associatedOrgUnits) {
		List<InterviewerDto> interviewersDtoReturned = new ArrayList<>();
    
		if(userId.equals(GUEST) || isUserAssociatedToTheCampaign(userId, campaignId)) {
      for(String orgId: associatedOrgUnits){
        interviewersDtoReturned.addAll(campaignRepository.findInterviewersDtoByCampaignId(campaignId, orgId));
      }
		}
		if(interviewersDtoReturned.isEmpty()) {
			LOGGER.error("No interviewers found for the user {} and the campaign {}", userId, campaignId);
			return List.of();
		}
		return interviewersDtoReturned;
	}

  public boolean isUserAssociatedToTheCampaign(String userId, String campaignId){
  	return !(campaignRepository.checkCampaignPreferences(userId, campaignId).isEmpty());
  }

  public StateCountDto getStateCount(String userId, String campaignId, String interviewerId, Long date, List<String> associatedOrgUnits) {
    StateCountDto stateCountDto = new StateCountDto();
    Optional<Interviewer> interv = interviewerRepository.findByIdIgnoreCase(interviewerId);

		if(userId.equals(GUEST) || isUserAssociatedToTheCampaign(userId, campaignId)) {
      Long dateToUse = date;
      if(dateToUse == null){
        dateToUse = -1L;
      }
      if(interv.isPresent() && (associatedOrgUnits.contains(interv.get().organizationUnit.id) || userId.equals(GUEST))){
        stateCountDto =  new StateCountDto(campaignRepository.getStateCount(campaignId, interviewerId, dateToUse));
      }
    }
    if(stateCountDto.getTotal() == null) {
			LOGGER.error("No matching interviewers {} were found for the user {} and the campaign {}", interviewerId, userId, campaignId);
			return null;
		}

		return stateCountDto;
	}
  
  public StateCountCampaignDto getStateCountByCampaign(String userId, String campaignId, Long date) {
	  	StateCountCampaignDto stateCountCampaignDto = new StateCountCampaignDto();
	  	List<StateCountDto> stateCountList = new ArrayList<>();
		if(userId.equals(GUEST) || isUserAssociatedToTheCampaign(userId, campaignId)) {
	      Long dateToUse = date;
	      if(dateToUse == null){
	        dateToUse = -1L;
	      }
	      for(String idOrganizationalUnit : organizationUnitRepository.findAllId()) {
		      stateCountList.add(new StateCountDto(idOrganizationalUnit, campaignRepository.getStateCountByCampaignAndOU(campaignId, idOrganizationalUnit, dateToUse)));
	      }
	      stateCountCampaignDto.setOrganizationUnits(stateCountList);
	      stateCountCampaignDto.setFrance(new StateCountDto(campaignRepository.getStateCountByCampaignId(campaignId, dateToUse)));
	    }
		if(stateCountCampaignDto.getOrganizationUnits().isEmpty() || stateCountCampaignDto.getFrance() == null) {
			LOGGER.error("No matching survey units states were found for the user {} and the campaign {}", userId, campaignId);
			return null;
		}
		return stateCountCampaignDto;
	}
}
