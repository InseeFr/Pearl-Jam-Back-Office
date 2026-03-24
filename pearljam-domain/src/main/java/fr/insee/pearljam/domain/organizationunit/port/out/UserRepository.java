package fr.insee.pearljam.domain.organizationunit.port.out;

import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.UserDB;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<UserDB> findByIdIgnoreCase(String userId);

    Optional<UserDB> findById(String userId);

    List<UserDB> findAll();

    List<UserDB> findAllByOrganizationUnitId(String organizationUnitId);

    UserDB save(UserDB user);

    void delete(UserDB user);
}
