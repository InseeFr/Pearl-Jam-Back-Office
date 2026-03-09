package fr.insee.pearljam.infrastructure.user.adapter;

import fr.insee.pearljam.domain.user.model.User;
import fr.insee.pearljam.infrastructure.user.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserDaoAdapter implements fr.insee.pearljam.domain.user.port.serverside.UserRepository {
    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findByIdIgnoreCase(String userId) {
        return userJpaRepository.findByIdIgnoreCase(userId);
    }

    @Override
    public Optional<User> findById(String userId) {
        return userJpaRepository.findById(userId);
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll();
    }

    @Override
    public List<User> findAllByOrganizationUnitId(String organizationUnitId) {
        return userJpaRepository.findAllByOrganizationUnitId(organizationUnitId);
    }

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public void delete(User user) {
        userJpaRepository.delete(user);
    }
}
