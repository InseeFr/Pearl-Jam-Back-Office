package fr.insee.pearljam.api.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.insee.pearljam.api.domain.Interviewer;
import fr.insee.pearljam.api.domain.Response;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.domain.VisibilityDB;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerContextDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import fr.insee.pearljam.api.repository.InterviewerRepository;
import fr.insee.pearljam.infrastructure.campaign.jpa.VisibilityJpaRepository;
import fr.insee.pearljam.api.service.InterviewerService;
import fr.insee.pearljam.api.service.SurveyUnitService;
import fr.insee.pearljam.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
	private final VisibilityJpaRepository visibilityRepository;
	private final UserService userService;
	private final SurveyUnitService surveyUnitService;

	public Optional<List<CampaignDto>> findCampaignsOfInterviewer(String interviewerId) {
		Optional<Interviewer> intwOpt = interviewerRepository.findById(interviewerId);
		if (!intwOpt.isPresent()) {
			return Optional.empty();
		}
		Interviewer intw = intwOpt.get();
		List<String> suIds = intw.getSurveyUnits().stream().map(SurveyUnit::getId).collect(Collectors.toList());
		List<VisibilityDB> visibilities = visibilityRepository.findAllVisibilityBySurveyUnitIds(suIds);
		List<CampaignDto> dtos = new ArrayList<>();
		for (VisibilityDB vi : visibilities) {
			Optional<CampaignDto> dtoOpt = dtos.stream()
					.filter(dto -> dto.getId().equals(vi.getCampaign().getId()))
					.findFirst();
			if (!dtoOpt.isPresent()) {
				dtos.add(new CampaignDto(
						vi.getCampaign().getId(),
						vi.getCampaign().getLabel(),
						vi.getManagementStartDate(),
						vi.getEndDate()));
			} else {
				if (dtoOpt.get().getManagementStartDate() > vi.getManagementStartDate()) {
					dtoOpt.get().setManagementStartDate(vi.getManagementStartDate());
				}
				if (dtoOpt.get().getEndDate() < vi.getEndDate()) {
					dtoOpt.get().setEndDate(vi.getEndDate());
				}
			}
		}
		return Optional.of(dtos);

	}

	@Override
	public Response createInterviewers(List<InterviewerContextDto> interviewers) {
		// Check duplicate line in interviewers to create
		Map<String, Integer> duplicates = new HashMap<>();
		List<String> interviewerErrors = new ArrayList<>();
		List<Interviewer> listInterviewers = new ArrayList<>();
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
			listInterviewers.add(new Interviewer(itwr));
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
	public Set<InterviewerDto> getListInterviewers(String userId) {
		List<String> lstOuId = userService.getUserOUs(userId, true).stream().map(OrganizationUnitDto::getId)
				.collect(Collectors.toList());
		return surveyUnitService.getSurveyUnitIdByOrganizationUnits(lstOuId).stream()
				.map(SurveyUnit::getInterviewer)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet())
				.stream()
				.map(InterviewerDto::new)
				.collect(Collectors.toSet());
	}

	@Override
	public boolean isPresent(String interviewerId) {
		return interviewerRepository.existsById(interviewerId);
	}

	@Override
	public boolean delete(String id) {
		Optional<Interviewer> optInterviewer = interviewerRepository.findById(id);
		if (!optInterviewer.isPresent()) {
			return false;
		}
		List<String> ids = surveyUnitService.getAllIdsByInterviewerId(id);
		if (!ids.isEmpty()) {
			surveyUnitService.removeInterviewerLink(ids);
		}
		interviewerRepository.deleteById(id);
		return true;
	}

	@Override
	public Optional<InterviewerContextDto> update(String id, InterviewerContextDto interviewer) {

		Optional<Interviewer> optInterviewer = interviewerRepository.findById(id);
		if (!optInterviewer.isPresent()) {
			return Optional.empty();
		}
		Interviewer interviewerToUpdate = optInterviewer.get();
		interviewerToUpdate.setEmail(interviewer.getEmail());
		interviewerToUpdate.setFirstName(interviewer.getFirstName());
		interviewerToUpdate.setLastName(interviewer.getLastName());
		interviewerToUpdate.setPhoneNumber(interviewer.getPhoneNumber());
		interviewerToUpdate.setTitle(interviewer.getTitle());

		return Optional.of(interviewerRepository.findDtoById(id));
	}

	@Override
	public Optional<InterviewerContextDto> findDtoById(String id) {
		if (!interviewerRepository.existsById(id))
			return Optional.empty();
		return Optional.of(interviewerRepository.findDtoById(id));
	}

	@Override
	public List<InterviewerContextDto> getCompleteListInterviewers() {

		return interviewerRepository.findAll().stream().map(InterviewerContextDto::new).collect(Collectors.toList());
	}

}
