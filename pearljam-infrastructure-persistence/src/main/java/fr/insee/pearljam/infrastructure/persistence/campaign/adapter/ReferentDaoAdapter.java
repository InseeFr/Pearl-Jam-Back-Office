package fr.insee.pearljam.infrastructure.persistence.campaign.adapter;

import fr.insee.pearljam.infrastructure.persistence.campaign.entity.ReferentDB;
import fr.insee.pearljam.domain.campaign.port.out.ReferentRepository;
import fr.insee.pearljam.infrastructure.persistence.campaign.jpa.ReferentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Deprecated(forRemoval = true)
public class ReferentDaoAdapter implements ReferentRepository {
    private final ReferentJpaRepository referentRepository;

    @Override
    public List<ReferentDB> findByCampaignId(String id) {
        return referentRepository.findByCampaignId(id);
    }
}
