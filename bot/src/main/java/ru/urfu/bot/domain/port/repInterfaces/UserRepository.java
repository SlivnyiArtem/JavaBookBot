package ru.urfu.bot.domain.port.repInterfaces;

import ru.urfu.bot.domain.port.repositories.JpaUserRepository;
import ru.urfu.bot.domain.entities.User;

import java.util.Optional;

/**
 * Интерфейс хранилища юзеров
 */
public interface UserRepository extends JpaUserRepository {

    Optional<User> findByUserName(String userName);

    User save(User user);
}
