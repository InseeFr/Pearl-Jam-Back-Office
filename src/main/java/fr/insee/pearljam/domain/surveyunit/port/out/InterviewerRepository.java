package fr.insee.pearljam.domain.surveyunit.port.out;

import fr.insee.pearljam.api.surveyunit.dto.interviewer.InterviewerContextDto;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.InterviewerDB;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface InterviewerRepository {
    Optional<InterviewerDB> findByIdIgnoreCase(String interviewerId);

    Optional<InterviewerDB> findById(String interviewerId);

    List<InterviewerDB> findInterviewersWorkingOnCampaign(String campaignId, List<String> ouIds);

    List<InterviewerDB> findInterviewersByOrganizationUnits(List<String> ouIds);

    Set<String> findIdsByOrganizationUnitsAndCampaignId(List<String> ouIds, List<String> campaignIds);

    InterviewerContextDto findDtoById(String id);

    List<String> findAllIds();

    List<InterviewerDB> saveAll(List<InterviewerDB> interviewers);

    void deleteById(String id);

    boolean existsById(String id);

    List<InterviewerDB> findAll();

    List<InterviewerDB> findAllById(Iterable<String> ids);
}
