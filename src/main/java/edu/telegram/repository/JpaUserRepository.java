package edu.telegram.repository;

import edu.telegram.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий базы данных для сохраненных пользователей
 */
@Repository
public interface JpaUserRepository extends JpaRepository<User, Long> {

    /**
     * Ищет пользователя по имени
     * @param userName имя пользователя
     * @return пользователь
     */
    Optional<User> findByUserName(String userName);
}
