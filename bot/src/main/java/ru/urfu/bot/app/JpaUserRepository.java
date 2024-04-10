package ru.urfu.bot.app;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.urfu.bot.domain.User;

import java.util.Optional;

@Repository
public interface JpaUserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserName(String userName);
}
