package fr.insee.pearljam.infrastructure.persistence.surveyunit.adapter;

import fr.insee.pearljam.api.surveyunit.dto.interviewer.InterviewerContextDto;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.InterviewerDB;
import fr.insee.pearljam.domain.surveyunit.port.out.InterviewerRepository;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.jpa.InterviewerJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class InterviewerDaoAdapter implements InterviewerRepository {
    private final InterviewerJpaRepository interviewerJpaRepository;

    @Override
    public Optional<InterviewerDB> findByIdIgnoreCase(String interviewerId) {
        return interviewerJpaRepository.findByIdIgnoreCase(interviewerId);
    }

    @Override
    public Optional<InterviewerDB> findById(String interviewerId) {
        return interviewerJpaRepository.findById(interviewerId);
    }

    @Override
    public List<InterviewerDB> findInterviewersWorkingOnCampaign(String campaignId, List<String> ouIds) {
        return interviewerJpaRepository.findInterviewersWorkingOnCampaign(campaignId, ouIds);
    }

    @Override
    public List<InterviewerDB> findInterviewersByOrganizationUnits(List<String> ouIds) {
        return interviewerJpaRepository.findInterviewersByOrganizationUnits(ouIds);
    }

    @Override
    public Set<String> findIdsByOrganizationUnitsAndCampaignId(List<String> ouIds, List<String> campaignIds) {
        return interviewerJpaRepository.findIdsByOrganizationUnitsAndCampaignId(ouIds, campaignIds);
    }

    @Override
    public InterviewerContextDto findDtoById(String id) {
        return interviewerJpaRepository.findDtoById(id);
    }

    @Override
    public List<String> findAllIds() {
        return interviewerJpaRepository.findAllIds();
    }

    @Override
    public List<InterviewerDB> saveAll(List<InterviewerDB> interviewers) {
        return interviewerJpaRepository.saveAll(interviewers);
    }

    @Override
    public void deleteById(String id) {
        interviewerJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return interviewerJpaRepository.existsById(id);
    }

    @Override
    public List<InterviewerDB> findAll() {
        return interviewerJpaRepository.findAll();
    }

    @Override
    public List<InterviewerDB> findAllById(Iterable<String> ids) {
        return interviewerJpaRepository.findAllById(ids);
    }
}
