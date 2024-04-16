package ru.urfu.bot.app.port;

import ru.urfu.bot.domain.entities.Chat;

import java.util.Optional;

/**
 * интерфейс хранилища чатов юзеров
 */
public interface ChatRepository {

    Chat save(Chat chat);

    Optional<Chat> findById(Long id);
}
