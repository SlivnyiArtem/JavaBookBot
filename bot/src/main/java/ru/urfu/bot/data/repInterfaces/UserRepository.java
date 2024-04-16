package ru.urfu.bot.data.repInterfaces;

import ru.urfu.bot.domain.entities.User;

import java.util.Optional;

/**
 * Интерфейс хранилища юзеров
 */
public interface UserRepository {

    Optional<User> findByUserName(String userName);

    User save(User user);
}
