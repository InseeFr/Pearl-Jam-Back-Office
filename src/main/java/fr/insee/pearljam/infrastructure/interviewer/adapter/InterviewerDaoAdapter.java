package fr.insee.pearljam.infrastructure.interviewer.adapter;

import fr.insee.pearljam.api.interviewer.dto.InterviewerContextDto;
import fr.insee.pearljam.domain.interviewer.model.Interviewer;
import fr.insee.pearljam.domain.interviewer.port.serverside.InterviewerRepository;
import fr.insee.pearljam.infrastructure.interviewer.jpa.InterviewerJpaRepository;
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
    public Optional<Interviewer> findByIdIgnoreCase(String interviewerId) {
        return interviewerJpaRepository.findByIdIgnoreCase(interviewerId);
    }

    @Override
    public Optional<Interviewer> findById(String interviewerId) {
        return interviewerJpaRepository.findById(interviewerId);
    }

    @Override
    public List<Interviewer> findInterviewersWorkingOnCampaign(String campaignId, List<String> ouIds) {
        return interviewerJpaRepository.findInterviewersWorkingOnCampaign(campaignId, ouIds);
    }

    @Override
    public List<Interviewer> findInterviewersByOrganizationUnits(List<String> ouIds) {
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
    public List<Interviewer> saveAll(List<Interviewer> interviewers) {
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
    public List<Interviewer> findAll() {
        return interviewerJpaRepository.findAll();
    }

    @Override
    public List<Interviewer> findAllById(Iterable<String> ids) {
        return interviewerJpaRepository.findAllById(ids);
    }
}
