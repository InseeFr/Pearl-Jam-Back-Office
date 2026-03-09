package fr.insee.pearljam.domain.interviewer.port.serverside;

import fr.insee.pearljam.api.interviewer.dto.InterviewerContextDto;
import fr.insee.pearljam.domain.interviewer.model.Interviewer;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface InterviewerRepository {
    Optional<Interviewer> findByIdIgnoreCase(String interviewerId);

    Optional<Interviewer> findById(String interviewerId);

    List<Interviewer> findInterviewersWorkingOnCampaign(String campaignId, List<String> ouIds);

    List<Interviewer> findInterviewersByOrganizationUnits(List<String> ouIds);

    Set<String> findIdsByOrganizationUnitsAndCampaignId(List<String> ouIds, List<String> campaignIds);

    InterviewerContextDto findDtoById(String id);

    List<String> findAllIds();

    List<Interviewer> saveAll(List<Interviewer> interviewers);

    void deleteById(String id);

    boolean existsById(String id);

    List<Interviewer> findAll();

    List<Interviewer> findAllById(Iterable<String> ids);
}
