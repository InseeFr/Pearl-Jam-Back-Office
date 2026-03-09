package fr.insee.pearljam.domain.campaign.service.dummy;

import fr.insee.pearljam.domain.user.model.User;
import fr.insee.pearljam.domain.user.port.serverside.UserRepository;

import java.util.List;
import java.util.Optional;

public class UserFakeRepository implements UserRepository {
    @Override
    public Optional<User> findByIdIgnoreCase(String userId) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(String userId) {
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public List<User> findAllByOrganizationUnitId(String organizationUnitId) {
        return List.of();
    }

    @Override
    public User save(User user) {
        return user;
    }

    @Override
    public void delete(User user) {
        // not used at this moment
    }
}
