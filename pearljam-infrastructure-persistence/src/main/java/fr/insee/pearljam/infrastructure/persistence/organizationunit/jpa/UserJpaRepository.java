package fr.insee.pearljam.infrastructure.persistence.organizationunit.jpa;

import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.UserDB;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserDB, String> {

	Optional<UserDB> findByIdIgnoreCase(String userId);

	List<UserDB> findAllByOrganizationUnitId(String id);

}
