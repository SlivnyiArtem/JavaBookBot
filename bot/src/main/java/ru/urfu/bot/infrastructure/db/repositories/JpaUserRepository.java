package ru.urfu.bot.infrastructure.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.urfu.bot.app.port.UserRepository;
import ru.urfu.bot.domain.entities.User;

import java.util.Optional;

@Repository
public interface JpaUserRepository extends JpaRepository<User, Long>, UserRepository {

    Optional<User> findByUserName(String userName);
}
