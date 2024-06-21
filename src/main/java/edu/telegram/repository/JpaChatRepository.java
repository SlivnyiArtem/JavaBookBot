package edu.telegram.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu.telegram.domain.Chat;

/**
 * Репозиторий базы данных для сохраненных телеграм-чатов
 */
@Repository
public interface JpaChatRepository extends JpaRepository<Chat, Long> { }
