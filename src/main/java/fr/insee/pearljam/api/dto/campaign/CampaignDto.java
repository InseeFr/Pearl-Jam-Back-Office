package fr.insee.pearljam.api.dto.campaign;

public interface CampaignDto {
	String getId();
	String getLabel();
	Long getCollectionStartDate();
	Long getCollectionEndDate();
}
