package ru.urfu.bot.app.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.urfu.bot.app.domain.entities.Chat;

@Repository
public interface JpaChatRepository extends JpaRepository<Chat, Long> {
}
