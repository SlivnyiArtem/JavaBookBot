package ru.urfu.bot.infrastructure.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.urfu.bot.app.port.ChatRepository;
import ru.urfu.bot.domain.entities.Chat;

@Repository
public interface JpaChatRepository extends JpaRepository<Chat, Long>, ChatRepository {
}
