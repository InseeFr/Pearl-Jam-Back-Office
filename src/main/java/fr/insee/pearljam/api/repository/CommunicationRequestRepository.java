package fr.insee.pearljam.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import fr.insee.pearljam.api.domain.communication.CommunicationRequest;
import fr.insee.pearljam.api.dto.communication.CommunicationRequestDto;

@Repository
public interface CommunicationRequestRepository extends JpaRepository<CommunicationRequest, Long> {

        @Query(value = "SELECT new fr.insee.pearljam.api.dto.communication.CommunicationRequestDto(cr) " +
                        "FROM CommunicationRequest cr WHERE cr.id=?1", nativeQuery = true)
        CommunicationRequestDto findDtoById(Long id);

        @Query(value = "SELECT new fr.insee.pearljam.api.dto.communication.CommunicationRequestDto(cr) " +
                        "FROM MailRequest cr WHERE cr.surveyUnit.id=?1 ", nativeQuery = true)
        List<CommunicationRequestDto> findDtoBySurveyUnitId(String suId);

        @Query(value = "DELETE FROM CommunicationRequest cr WHERE mr.surveyUnit.id=?1 ", nativeQuery = true)
        void deleteBySurveyUnitId(String id);

}
