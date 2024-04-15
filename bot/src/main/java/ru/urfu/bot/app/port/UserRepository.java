package ru.urfu.bot.app.port;

import ru.urfu.bot.domain.entities.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByUserName(String userName);

    User save(User user);
}
