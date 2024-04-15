package ru.urfu.bot.app.port;

import ru.urfu.bot.domain.entities.Chat;

import java.util.Optional;

public interface ChatRepository {

    Chat save(Chat chat);

    Optional<Chat> findById(Long id);
}
