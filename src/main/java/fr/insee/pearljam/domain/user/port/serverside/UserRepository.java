package fr.insee.pearljam.domain.user.port.serverside;

import fr.insee.pearljam.domain.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByIdIgnoreCase(String userId);

    Optional<User> findById(String userId);

    List<User> findAll();

    List<User> findAllByOrganizationUnitId(String organizationUnitId);

    User save(User user);

    void delete(User user);
}
