package fr.insee.pearljam.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.pearljam.api.domain.User;

public interface UserRepository extends JpaRepository<User, String> {

	Optional<User> findByIdIgnoreCase(String userId);

	boolean existsByIdIgnoreCase(String userId);

	List<User> findAllByOrganizationUnitId(String id);

}
