package ru.urfu.bot.domain.port.repInterfaces;

import ru.urfu.bot.domain.port.repositories.JpaChatRepository;
import ru.urfu.bot.domain.entities.Chat;

import java.util.Optional;

/**
 * интерфейс хранилища чатов юзеров
 */
public interface ChatRepository extends JpaChatRepository {

    Chat save(Chat chat);

    Optional<Chat> findById(Long id);
}
