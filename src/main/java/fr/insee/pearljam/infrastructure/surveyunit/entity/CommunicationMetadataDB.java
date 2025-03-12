package fr.insee.pearljam.infrastructure.surveyunit.entity;

import fr.insee.pearljam.api.domain.SurveyUnit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "communication_metadata")
@Table
@Getter
@Setter
@NoArgsConstructor
public class CommunicationMetadataDB implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	private SurveyUnit surveyUnit;
	@Column(name = "metadata_key")
	private String key;
	@Column(name = "metadata_value")
	private String value;
	@Column(name = "campaign_id")
	private String campaignId;
	@Column(name = "meshuggah_id")
	private String meshuggahId;
}
