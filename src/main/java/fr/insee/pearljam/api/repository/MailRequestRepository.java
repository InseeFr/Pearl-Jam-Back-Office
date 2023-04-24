package fr.insee.pearljam.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import fr.insee.pearljam.api.domain.MailRequest;
import fr.insee.pearljam.api.domain.MailRequestStatus;
import fr.insee.pearljam.api.dto.mailrequest.MailRequestDto;

@Repository
public interface MailRequestRepository extends JpaRepository<MailRequest, Long> {

    @Query("SELECT new fr.insee.pearljam.api.dto.mailrequest.MailRequestDto(mr) " +
            "FROM MailRequest mr WHERE mr.id=?1")
    MailRequestDto findDtoById(Long id);

    @Query("SELECT new fr.insee.pearljam.api.dto.mailrequest.MailRequestDto(mr) " +
            "FROM MailRequest mr WHERE mr.surveyUnit.id=?1 ")
    List<MailRequestDto> findDtoBySurveyUnitId(String suId);

    @Query("UPDATE MailRequest SET status=?1 WHERE id=?2")
    void updateStatus(MailRequestStatus newStatus, Long id);

}
