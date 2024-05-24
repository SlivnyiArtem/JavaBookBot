package ru.urfu.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.urfu.bot.domain.User;

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
