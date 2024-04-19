package ru.urfu.bot.domain.port.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.urfu.bot.domain.entities.Chat;

import java.util.Optional;

@Repository
public interface JpaChatRepository extends JpaRepository<Chat, Long> {
    Chat save(Chat chat);
    Optional<Chat> findById(Long id);
}
