package ru.urfu.bot.domain.port.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.urfu.bot.domain.port.repInterfaces.ChatRepository;
import ru.urfu.bot.domain.entities.Chat;

@Repository
public interface JpaChatRepository extends JpaRepository<Chat, Long> {
}
