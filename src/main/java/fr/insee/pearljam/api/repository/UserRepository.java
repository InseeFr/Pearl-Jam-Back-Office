package fr.insee.pearljam.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.pearljam.api.domain.User;

public interface UserRepository extends JpaRepository<User, String> {

	Optional<User> findByIdIgnoreCase(String userId);

}
