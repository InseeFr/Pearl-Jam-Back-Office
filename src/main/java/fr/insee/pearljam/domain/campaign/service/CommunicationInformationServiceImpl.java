package fr.insee.pearljam.domain.campaign.service;

import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationInformation;
import fr.insee.pearljam.domain.campaign.port.serverside.CommunicationInformationRepository;
import fr.insee.pearljam.domain.campaign.port.userside.CommunicationInformationService;
import fr.insee.pearljam.domain.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.exception.OrganizationalUnitNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class CommunicationInformationServiceImpl implements CommunicationInformationService {

    private final CommunicationInformationRepository communicationInformationRepository;

    @Override
    public List<CommunicationInformation> findCommunicationInformations(String campaignId) {
        return communicationInformationRepository.findCommunicationInformations(campaignId);
    }

    @Override
    public void setCommunicationInformations(List<CommunicationInformation> communicationInformations, Campaign campaign) throws OrganizationalUnitNotFoundException, CampaignNotFoundException {
        communicationInformationRepository.setCommunicationInformations(communicationInformations, campaign);
    }

    @Override
    public void updateCommunicationInformation(CommunicationInformation communicationInformationToUpdate) {
        communicationInformationRepository.update(communicationInformationToUpdate);
    }
}
