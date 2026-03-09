package fr.insee.pearljam.infrastructure.campaign.adapter;

import fr.insee.pearljam.domain.campaign.model.Referent;
import fr.insee.pearljam.domain.campaign.port.serverside.ReferentRepository;
import fr.insee.pearljam.infrastructure.campaign.jpa.ReferentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReferentDaoAdapter implements ReferentRepository {
    private final ReferentJpaRepository referentRepository;

    @Override
    public List<Referent> findByCampaignId(String id) {
        return referentRepository.findByCampaignId(id);
    }
}
