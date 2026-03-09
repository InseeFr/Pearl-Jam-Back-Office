package fr.insee.pearljam.infrastructure.persistence.organizationunit.adapter;

import fr.insee.pearljam.domain.organizationunit.port.out.UserRepository;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.UserDB;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserDaoAdapter implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<UserDB> findByIdIgnoreCase(String userId) {
        return userJpaRepository.findByIdIgnoreCase(userId);
    }

    @Override
    public Optional<UserDB> findById(String userId) {
        return userJpaRepository.findById(userId);
    }

    @Override
    public List<UserDB> findAll() {
        return userJpaRepository.findAll();
    }

    @Override
    public List<UserDB> findAllByOrganizationUnitId(String organizationUnitId) {
        return userJpaRepository.findAllByOrganizationUnitId(organizationUnitId);
    }

    @Override
    public UserDB save(UserDB user) {
        return userJpaRepository.save(user);
    }

    @Override
    public void delete(UserDB user) {
        userJpaRepository.delete(user);
    }
}
