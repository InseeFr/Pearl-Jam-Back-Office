package fr.insee.pearljam.infrastructure.surveyunit.entity;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.infrastructure.campaign.entity.CommunicationTemplateDB;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "communication_metadata")
@Table
@Getter
@Setter
@NoArgsConstructor
public class CommunicationMetadataDB {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	private SurveyUnit surveyUnit;
	@ManyToOne(fetch = FetchType.LAZY)
	private CommunicationTemplateDB communicationTemplate;
	@Column(name = "metadata_key")
	private String key;
	@Column(name = "metadata_value")
	private String value;
}
