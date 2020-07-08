package fr.insee.pearljam.api.projection;

import fr.insee.pearljam.api.domain.InseeSampleIdentifier;
import fr.insee.pearljam.api.domain.Interviewer;

public interface SurveyUnitCampaign {
	
	String getId();
	String getL6();
	Integer getSsech();
	
	//InseeSampleIdentifier getSsech();

	//Interviewer getId();
	
//	interface InseeSampleIdentifier {
//		Integer getSsech();
//	}
}
