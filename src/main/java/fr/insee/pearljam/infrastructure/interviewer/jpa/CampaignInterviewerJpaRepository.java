package fr.insee.pearljam.infrastructure.interviewer.jpa;

import fr.insee.pearljam.domain.interviewer.model.CampaignInterviewer;
import fr.insee.pearljam.api.domain.Interviewer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CampaignInterviewerJpaRepository extends CrudRepository<Interviewer, String> {

        @Query("""
                        SELECT new fr.insee.pearljam.domain.interviewer.model.CampaignInterviewer(
                                        interv.id,
                                        interv.firstName,
                                        interv.lastName,
                                        interv.email,
                                        interv.phoneNumber,
                                        COUNT(su.interviewer))
                        FROM SurveyUnit su
                        INNER JOIN su.interviewer interv
                        WHERE su.campaign.id=:campaignId
                        AND (su.organizationUnit.id IN :organizationUnitIds OR 'GUEST' IN :organizationUnitIds)
                        GROUP BY interv.id, interv.firstName, interv.lastName, interv.email, interv.phoneNumber
                        """)
	List<CampaignInterviewer> findCampaignInterviewers(
			@Param("campaignId") String campaignId,
			@Param("organizationUnitIds") List<String> organizationUnitIds);
}
