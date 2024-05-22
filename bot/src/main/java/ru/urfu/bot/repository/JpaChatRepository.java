package ru.urfu.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.urfu.bot.domain.Chat;

/**
 * Репозиторий базы данных для сохраненных телеграм-чатов
 */
@Repository
public interface JpaChatRepository extends JpaRepository<Chat, Long> { }
