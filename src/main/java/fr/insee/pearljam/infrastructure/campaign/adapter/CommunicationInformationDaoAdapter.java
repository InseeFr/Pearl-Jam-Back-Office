package fr.insee.pearljam.infrastructure.campaign.adapter;

import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.api.domain.OrganizationUnit;
import fr.insee.pearljam.api.repository.CampaignRepository;
import fr.insee.pearljam.api.repository.OrganizationUnitRepository;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationInformation;
import fr.insee.pearljam.domain.campaign.port.serverside.CommunicationInformationRepository;
import fr.insee.pearljam.domain.exception.CommunicationInformationNotFoundException;
import fr.insee.pearljam.domain.exception.OrganizationalUnitNotFoundException;
import fr.insee.pearljam.infrastructure.campaign.entity.CommunicationInformationDB;
import fr.insee.pearljam.infrastructure.campaign.jpa.CommunicationInformationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

 import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class CommunicationInformationDaoAdapter implements CommunicationInformationRepository {
    private final CommunicationInformationJpaRepository crudRepository;
    private final OrganizationUnitRepository organizationUnitRepository;
    private final CampaignRepository campaignRepository;

    @Override
    public Optional<CommunicationInformation> findCommunicationInformation(String campaignId, String organizationalUnitId) {
        return crudRepository
                .findCommunicationInformation(campaignId, organizationalUnitId)
                .map(CommunicationInformationDB::toModel);
    }

    @Override
    public List<CommunicationInformation> findCommunicationInformations(String campaignId) {
        return CommunicationInformationDB.toModel(
                crudRepository.findByCampaignId(campaignId)
        );
    }

    @Override
    public void update(CommunicationInformation communicationInformationToUpdate) {
        CommunicationInformationDB currentCommunicationInformationDB = crudRepository.findCommunicationInformation(
                        communicationInformationToUpdate.campaignId(), communicationInformationToUpdate.organizationalUnitId())
                .orElseThrow(CommunicationInformationNotFoundException::new);
        currentCommunicationInformationDB.setMail(communicationInformationToUpdate.mail());
        currentCommunicationInformationDB.setTel(communicationInformationToUpdate.tel());
        currentCommunicationInformationDB.setAddress(communicationInformationToUpdate.address());
        crudRepository.save(currentCommunicationInformationDB);
    }

    @Override
    public void setCommunicationInformations(List<CommunicationInformation> communicationInformations, Campaign campaign) throws OrganizationalUnitNotFoundException {
        List<CommunicationInformationDB> currentCommunicationInformations = campaign.getCommunicationInformations();
        currentCommunicationInformations.clear();
        for(CommunicationInformation communicationInformationToUpdate : communicationInformations) {
            OrganizationUnit organizationUnit = organizationUnitRepository
                    .findById(communicationInformationToUpdate.organizationalUnitId())
                    .orElseThrow(OrganizationalUnitNotFoundException::new);

            CommunicationInformationDB communicationConfigurationDB =
                    crudRepository
                            .findCommunicationInformation(
                                    communicationInformationToUpdate.campaignId(),
                                    communicationInformationToUpdate.organizationalUnitId()
                            )
                            .map(communicationInformationDB -> {
                                communicationInformationDB.setMail(communicationInformationToUpdate.mail());
                                communicationInformationDB.setTel(communicationInformationToUpdate.tel());
                                communicationInformationDB.setAddress(communicationInformationToUpdate.address());
                                return communicationInformationDB;
                            })
                            .orElse(CommunicationInformationDB.fromModel(communicationInformationToUpdate, campaign, organizationUnit));

            currentCommunicationInformations.add(communicationConfigurationDB);
        }
        campaignRepository.save(campaign);
    }
}
