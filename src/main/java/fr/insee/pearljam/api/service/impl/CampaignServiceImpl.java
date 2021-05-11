package fr.insee.pearljam.api.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import fr.insee.pearljam.api.dto.contactoutcome.ContactOutcomeTypeCountCampaignDto;
import fr.insee.pearljam.api.dto.contactoutcome.ContactOutcomeTypeCountDto;
import fr.insee.pearljam.api.dto.count.CountDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import fr.insee.pearljam.api.dto.state.StateCountCampaignDto;
import fr.insee.pearljam.api.dto.state.StateCountDto;
import fr.insee.pearljam.api.dto.visibility.VisibilityContextDto;
import fr.insee.pearljam.api.dto.visibility.VisibilityDto;
import fr.insee.pearljam.api.exception.NoOrganizationUnitException;
import fr.insee.pearljam.api.exception.VisibilityException;
import fr.insee.pearljam.api.repository.CampaignRepository;
import fr.insee.pearljam.api.repository.ContactOutcomeRepository;
import fr.insee.pearljam.api.repository.InterviewerRepository;
import fr.insee.pearljam.api.repository.OrganizationUnitRepository;
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

	public StateCountDto getStateCount(String userId, String campaignId, String interviewerId, Long date,
			List<String> associatedOrgUnits) {
		StateCountDto stateCountDto = new StateCountDto();
		if (!utilsService.checkUserCampaignOUConstraints(userId, campaignId)) {
			return null;
		}
		if (!interviewerRepository.findById(interviewerId).isPresent()) {
			LOGGER.error("No interviewer found for the id {}", interviewerId);
			return null;
		}
		List<String> userOuIds;
		if(!userId.equals(Constants.GUEST)) {
			userOuIds = utilsService.getRelatedOrganizationUnits(userId);
		} else {
			userOuIds = organizationUnitRepository.findAllId();
		}
		
		List<String> intervIds = interviewerRepository.findInterviewersByOrganizationUnits(associatedOrgUnits);
		Long dateToUse = date;
		if (dateToUse == null) {
			dateToUse = System.currentTimeMillis();
		}
		if (!intervIds.isEmpty() && (intervIds.contains(interviewerId)) || userId.equals(Constants.GUEST)) {
			stateCountDto = new StateCountDto(campaignRepository.getStateCount(campaignId, interviewerId, userOuIds, dateToUse));
			stateCountDto.addClosingCauseCount(campaignRepository.getClosingCauseCount(campaignId, interviewerId, userOuIds, dateToUse));
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
			dateToUse = System.currentTimeMillis();
		}
		for (String id : organizationUnitRepository.findAllId()) {
			if(organizationUnitRepository.findChildren(id).isEmpty()
					&& visibilityRepository.findVisibilityInCollectionPeriod(campaignId, id, dateToUse).isPresent()) {
		        StateCountDto dto = new StateCountDto(id, organizationUnitRepository.findLabel(id),
		          campaignRepository.getStateCountByCampaignAndOU(campaignId, id, dateToUse));
				dto.addClosingCauseCount(campaignRepository.getClosingCauseCountByCampaignAndOU(campaignId, id, dateToUse));
				stateCountList.add(dto);
			}
		}
		stateCountCampaignDto.setOrganizationUnits(stateCountList);
		
		StateCountDto dtoFrance = new StateCountDto(campaignRepository.getStateCountByCampaignId(campaignId, dateToUse));
		dtoFrance.addClosingCauseCount(campaignRepository.getClosingCauseCountByCampaignId(campaignId, dateToUse));
		stateCountCampaignDto.setFrance(dtoFrance);
		if (stateCountCampaignDto.getFrance() == null || stateCountCampaignDto.getOrganizationUnits() == null
				) {
			LOGGER.error("No matching survey units states were found for the user {} and the campaign {}", userId,
					campaignId);
			return null;
		}
		return stateCountCampaignDto;
	}

	public ContactOutcomeTypeCountCampaignDto getContactOutcomeCountTypeByCampaign(String userId, String campaignId,
			Long date) {
		ContactOutcomeTypeCountCampaignDto stateCountCampaignDto = new ContactOutcomeTypeCountCampaignDto();
		if (!utilsService.checkUserCampaignOUConstraints(userId, campaignId)) {
			return null;
		}
		List<ContactOutcomeTypeCountDto> stateCountList = new ArrayList<>();
		Long dateToUse = date;
		if (dateToUse == null) {
			dateToUse = System.currentTimeMillis();
		}
		for (String id : organizationUnitRepository.findAllId()) {
			if (organizationUnitRepository.findChildren(id).isEmpty()
					&& visibilityRepository.findVisibilityInCollectionPeriod(campaignId, id, dateToUse).isPresent()) {
				stateCountList.add(new ContactOutcomeTypeCountDto(id, organizationUnitRepository.findLabel(id),
						contactOutcomeRepository.getContactOutcomeTypeCountByCampaignAndOU(campaignId, id, dateToUse)));
			}
		}
		stateCountCampaignDto.setOrganizationUnits(stateCountList);
		stateCountCampaignDto.setFrance(new ContactOutcomeTypeCountDto(
				contactOutcomeRepository.getContactOutcomeTypeCountByCampaignId(campaignId, dateToUse)));
		if (stateCountCampaignDto.getFrance() == null || stateCountCampaignDto.getOrganizationUnits() == null
				) {
			LOGGER.error("No matching survey units states were found for the user {} and the campaign {}", userId,
					campaignId);
			return null;
		}
		return stateCountCampaignDto;
	}

	public List<ContactOutcomeTypeCountDto> getContactOutcomeTypeCountByCampaign(String userId, Long date) {
		List<String> userOuIds;
		if(!userId.equals(Constants.GUEST)) {
			userOuIds = utilsService.getRelatedOrganizationUnits(userId);
		} else {
			userOuIds = organizationUnitRepository.findAllId();
		}
		final Long dateToUse = date==null?System.currentTimeMillis():date;
		List<String> lstCampaignUser = campaignRepository.findAllIdsVisible(userOuIds, dateToUse);
		return lstCampaignUser.stream()
				.map(idCampaign -> new ContactOutcomeTypeCountDto(
						contactOutcomeRepository.getContactOutcomeTypeCountByCampaignId(idCampaign, dateToUse),
						campaignRepository.findDtoById(idCampaign)))
				.collect(Collectors.toList());
	}

	public List<StateCountDto> getStateCountByCampaigns(String userId, Long date) {
		List<StateCountDto> returnList = new ArrayList<>();
		List<OrganizationUnitDto> organizationUnits = userService.getUserOUs(userId, true);
		for (OrganizationUnitDto dto : organizationUnits) {
			LOGGER.info(dto.getId());
		}
		List<String> userOrgUnitIds = organizationUnits.stream().map(OrganizationUnitDto::getId)
				.collect(Collectors.toList());
		Long dateToUse = date;
		if (dateToUse == null) {
			dateToUse = System.currentTimeMillis();
		}
		List<String> campaignIds = campaignRepository.findAllIdsVisible(userOrgUnitIds, dateToUse);
		for(String id : campaignIds) {
			StateCountDto campaignSum = new StateCountDto(
        campaignRepository.getStateCountSumByCampaign(id, userOrgUnitIds, dateToUse)
      );
			campaignSum.addClosingCauseCount(campaignRepository.getClosingCauseCountSumByCampaign(id, userOrgUnitIds, dateToUse));
			if(campaignSum.getTotal() != null) {
				CampaignDto dto = campaignRepository.findDtoById(id);
				campaignSum.setCampaign(dto);
				returnList.add(campaignSum);
			}
		}
		return returnList;
	}

	public List<StateCountDto> getStateCountByInterviewer(String userId, Long date) {
		List<StateCountDto> returnList = new ArrayList<>();
		List<OrganizationUnitDto> organizationUnits = userService.getUserOUs(userId, true);
		List<String> userOrgUnitIds = organizationUnits.stream().map(OrganizationUnitDto::getId)
				.collect(Collectors.toList());
		Long dateToUse = date;
		if (dateToUse == null) {
			dateToUse = System.currentTimeMillis();
		}
		Set<String> interviewerIds = interviewerRepository.findIdsByOrganizationUnits(userOrgUnitIds);
		List<String> campaignIds = campaignRepository.findAllIdsVisible(userOrgUnitIds, dateToUse);
		for(String id : interviewerIds) {
			StateCountDto interviewerSum = new StateCountDto(
		        campaignRepository.getStateCountSumByInterviewer(campaignIds, id, userOrgUnitIds, dateToUse)
		      );
			interviewerSum.addClosingCauseCount(campaignRepository.getClosingCauseCountSumByInterviewer(campaignIds, id, userOrgUnitIds, dateToUse));
			if(interviewerSum.getTotal() != null) {
				interviewerSum.setInterviewer(interviewerRepository.findDtoById(id));
				returnList.add(interviewerSum);
			}
		}
		return returnList;
	}
	
	@Override
	public StateCountDto getNbSUNotAttributedStateCount(String userId, String id, Long date) {
		List<String> organizationUnits = userService.getUserOUs(userId, true)
				.stream().map(OrganizationUnitDto::getId)
				.collect(Collectors.toList());
		Long dateToUse = date;
		if (dateToUse == null) {
			dateToUse = System.currentTimeMillis();
		}
		
		if(visibilityRepository.findVisibilityInCollectionPeriodForOUs(id, organizationUnits, dateToUse).isEmpty()) {
			return null;
		}
		
		StateCountDto interviewerSum = new StateCountDto(
	        campaignRepository.getStateCountNotAttributed(id, organizationUnits, dateToUse)
	      );
		interviewerSum.addClosingCauseCount(campaignRepository.getClosingCauseCountNotAttributed(id, organizationUnits, dateToUse));
		
		return interviewerSum;
	}
	
	@Override
	public ContactOutcomeTypeCountDto getNbSUNotAttributedContactOutcomes(String userId, String id, Long date) {
		List<String> organizationUnits = userService.getUserOUs(userId, true)
				.stream().map(OrganizationUnitDto::getId)
				.collect(Collectors.toList());
		Long dateToUse = date;
		if (dateToUse == null) {
			dateToUse = System.currentTimeMillis();
		}
		
		if(visibilityRepository.findVisibilityInCollectionPeriodForOUs(id, organizationUnits, dateToUse).isEmpty()) {
			return null;
		}
		
		return new ContactOutcomeTypeCountDto(
	        contactOutcomeRepository.findContactOutcomeTypeNotAttributed(id, organizationUnits, dateToUse)
	      );
		
	}

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
