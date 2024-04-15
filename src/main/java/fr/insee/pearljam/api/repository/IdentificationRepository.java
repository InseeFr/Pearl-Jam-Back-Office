package fr.insee.pearljam.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.insee.pearljam.api.domain.Identification;

@Repository
public interface IdentificationRepository extends JpaRepository<Identification, Long> {

    public Identification findBySurveyUnitId(String id);
}
