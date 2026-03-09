package fr.insee.pearljam.infrastructure.persistence.surveyunit.entity;

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

@Entity
@Table(name = "communication_metadata", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class CommunicationMetadataDB implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	private SurveyUnitDB surveyUnit;
	@Column(name = "metadata_key")
	private String key;
	@Column(name = "metadata_value")
	private String value;
	@Column(name = "campaign_id")
	private String campaignId;
	@Column(name = "meshuggah_id")
	private String meshuggahId;
}
