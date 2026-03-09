package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.api.campaign.dto.output.CampaignVisibilityPeriodDto;
import fr.insee.pearljam.domain.campaign.model.CampaignVisibilityPeriod;
import fr.insee.pearljam.domain.campaign.port.out.VisibilityRepository;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.InterviewerDB;
import fr.insee.pearljam.domain.shared.model.Response;
import fr.insee.pearljam.contracts.surveyunit.dto.interviewer.InterviewerContextDto;
import fr.insee.pearljam.api.surveyunit.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.contracts.organizationunit.dto.OrganizationUnitDto;
import fr.insee.pearljam.domain.surveyunit.port.out.InterviewerCountRepository;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.surveyunit.service.exception.InterviewerNotFoundException;
import fr.insee.pearljam.domain.surveyunit.port.out.InterviewerRepository;
import fr.insee.pearljam.domain.surveyunit.port.in.InterviewerService;
import fr.insee.pearljam.domain.security.port.in.AuthenticatedUserService;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitService;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the Service for the Interviewer entity
 * 
 * @author scorcaud
 *
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class InterviewerServiceImpl implements InterviewerService {

	private final InterviewerRepository interviewerRepository;
	private final VisibilityRepository visibilityRepository;
	private final UserService userService;
	private final SurveyUnitService surveyUnitService;
	private final InterviewerCountRepository campaignInterviewerRepository;
	private final AuthenticatedUserService authenticatedUserService;

	public List<CampaignVisibilityPeriodDto> findCampaignsOfInterviewer(String interviewerId) {
		interviewerRepository.findById(interviewerId).orElseThrow(() -> new InterviewerNotFoundException(interviewerId));

		List<String> suIds = surveyUnitService.getAllIdsByInterviewerId(interviewerId);
		if (suIds.isEmpty()) return List.of();

		List<CampaignVisibilityPeriod> campaignVisibilities = visibilityRepository.findCampaignsBySurveyUnitIds(suIds);

		return campaignVisibilities.stream()
				.map(CampaignVisibilityPeriodDto::fromDomain)
				.toList();
	}

	@Override
	public Response createInterviewers(List<InterviewerContextDto> interviewers) {
		// Check duplicate line in interviewers to create
		Map<String, Integer> duplicates = new HashMap<>();
		List<String> interviewerErrors = new ArrayList<>();
		List<InterviewerDB> listInterviewers = new ArrayList<>();
		List<String> interviewersDb = interviewerRepository.findAllIds();
		interviewers.stream().forEach(itwr -> {
			if (!duplicates.containsKey(itwr.getId())) {
				duplicates.put(itwr.getId(), 0);
			}
			duplicates.put(itwr.getId(), duplicates.get(itwr.getId()) + 1);
			if (interviewersDb.contains(itwr.getId())) {
				duplicates.put(itwr.getId(), duplicates.get(itwr.getId()) + 1);
			}
			if (!itwr.isValid()) {
				interviewerErrors.add(itwr.getId());
			}
			listInterviewers.add(new InterviewerDB(itwr));
		});
		// Check attributes are not null
		if (!interviewerErrors.isEmpty()) {
			String errorMessage = String.format("Invalid format : [%s]", String.join(", ", interviewerErrors));
			log.error(errorMessage);
			return new Response(String.format("Invalid format : [%s]", String.join(", ", interviewerErrors)),
					HttpStatus.BAD_REQUEST);
		}
		// Check duplicate lines

		if (!duplicates.keySet().stream().filter(id -> duplicates.get(id) > 1).collect(Collectors.toSet()).isEmpty()) {
			log.error("Duplicate entry : [{}]", String.join(", ", duplicates.keySet()));
			return new Response(String.format("Duplicate entries : [%s]", String.join(", ", duplicates.keySet())),
					HttpStatus.BAD_REQUEST);
		}
		interviewerRepository.saveAll(listInterviewers);
		log.info("{} interviewers created", listInterviewers.size());
		return new Response(String.format("%s interviewers created", listInterviewers.size()), HttpStatus.OK);
	}

	@Override
	public Set<InterviewerDto> getInterviewersForCurrentUser() {
		String userId = authenticatedUserService.getCurrentUserId();
		List<String> lstOuId = userService.getUserOUs(userId, true).stream().map(OrganizationUnitDto::getId)
				.toList();
		return interviewerRepository.findInterviewersByOrganizationUnits(lstOuId)
				.stream().map(interviewer -> new InterviewerDto(
						interviewer.getId(),
						interviewer.getFirstName(),
						interviewer.getLastName(),
						null))
				.collect(Collectors.toSet());
	}

	@Override
	public void delete(String id) {
		interviewerRepository.findById(id).orElseThrow(() -> new InterviewerNotFoundException(id));
		List<String> ids = surveyUnitService.getAllIdsByInterviewerId(id);
		if (!ids.isEmpty()) {
			surveyUnitService.removeInterviewerLink(ids);
		}
		interviewerRepository.deleteById(id);
	}

	@Override
	public InterviewerContextDto update(String id, InterviewerContextDto interviewer) {

		InterviewerDB interviewerToUpdate = interviewerRepository.findById(id)
				.orElseThrow(() -> new InterviewerNotFoundException(id));

		interviewerToUpdate.setEmail(interviewer.getEmail());
		interviewerToUpdate.setFirstName(interviewer.getFirstName());
		interviewerToUpdate.setLastName(interviewer.getLastName());
		interviewerToUpdate.setPhoneNumber(interviewer.getPhoneNumber());
		interviewerToUpdate.setTitle(interviewer.getTitle());

		return interviewerRepository.findDtoById(id);
	}

	@Override
	public Optional<InterviewerContextDto> findDtoById(String id) {
		if (!interviewerRepository.existsById(id))
			return Optional.empty();
		return Optional.of(interviewerRepository.findDtoById(id));
	}

	@Override
	public List<InterviewerContextDto> getCompleteListInterviewers() {

		return interviewerRepository.findAll().stream()
				.map(interviewer -> new InterviewerContextDto(interviewer.getId(), interviewer.getFirstName(),
						interviewer.getLastName(), interviewer.getEmail(), interviewer.getPhoneNumber(),
						interviewer.getTitle()))
				.toList();
	}

	@Override
	public List<InterviewerDto> getInterviewersByUserAndCampaign(String campaignId) throws CampaignNotFoundException {
		String userId = authenticatedUserService.getCurrentUserId();
		userService.checkUserAssociationToCampaign(campaignId, userId);

		List<String> userOrgUnitIds = userService.getUserOUs(userId, false).stream()
				.map(OrganizationUnitDto::getId)
				.toList();

		List<InterviewerDto> interviewersDtoReturned = campaignInterviewerRepository
				.findCampaignInterviewers(campaignId, userOrgUnitIds).stream()
				.map(InterviewerDto::fromModel)
				.toList();

		if (interviewersDtoReturned.isEmpty()) {
			log.warn("No interviewers found for the campaign {}", campaignId);
		}
		return interviewersDtoReturned;
	}

}
