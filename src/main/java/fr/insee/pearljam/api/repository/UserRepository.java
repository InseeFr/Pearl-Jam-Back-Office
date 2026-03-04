package fr.insee.pearljam.api.repository;

import fr.insee.pearljam.api.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

	Optional<User> findByIdIgnoreCase(String userId);

	List<User> findAllByOrganizationUnitId(String id);

}
