package ru.urfu.bot.app;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.urfu.bot.domain.Chat;

@Repository
public interface JpaChatRepository extends JpaRepository<Chat, Long> {
}
