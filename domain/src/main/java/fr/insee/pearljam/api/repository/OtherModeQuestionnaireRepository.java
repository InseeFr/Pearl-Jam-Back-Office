package fr.insee.pearljam.api.repository;

import fr.insee.pearljam.api.domain.OtherModeQuestionnaireState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtherModeQuestionnaireRepository extends JpaRepository<OtherModeQuestionnaireState, String> {
}