package fr.insee.pearljam.domain.campaign.service.dummy;

import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.UserDB;
import fr.insee.pearljam.domain.organizationunit.port.out.UserRepository;

import java.util.List;
import java.util.Optional;

public class UserFakeRepository implements UserRepository {
    @Override
    public Optional<UserDB> findByIdIgnoreCase(String userId) {
        return Optional.empty();
    }

    @Override
    public Optional<UserDB> findById(String userId) {
        return Optional.empty();
    }

    @Override
    public List<UserDB> findAll() {
        return List.of();
    }

    @Override
    public List<UserDB> findAllByOrganizationUnitId(String organizationUnitId) {
        return List.of();
    }

    @Override
    public UserDB save(UserDB user) {
        return user;
    }

    @Override
    public void delete(UserDB user) {
        // not used at this moment
    }
}
