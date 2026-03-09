package fr.insee.pearljam.infrastructure.user.jpa;

import fr.insee.pearljam.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, String> {

	Optional<User> findByIdIgnoreCase(String userId);

	List<User> findAllByOrganizationUnitId(String id);

}
