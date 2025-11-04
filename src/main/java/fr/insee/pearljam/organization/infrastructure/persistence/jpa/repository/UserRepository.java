package fr.insee.pearljam.organization.infrastructure.persistence.jpa.repository;

import java.util.List;
import java.util.Optional;

import fr.insee.pearljam.organization.infrastructure.persistence.jpa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

	Optional<User> findByIdIgnoreCase(String userId);


	boolean existsByIdIgnoreCase(String userId);

	List<User> findAllByOrganizationUnitId(String id);

}
