package fr.insee.pearljam.infrastructure.surveyunit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author camille corbin
 */
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CommunicationRequestDBId implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * The meshuggah Id
	 */
	@Column(name = "meshuggah_id")
	private String meshuggahId;

	/**
	 * The campaign Id
	 */
	@Column(name = "campaign_id")
	private String campaignId;

}
