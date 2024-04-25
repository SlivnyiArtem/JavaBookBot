package ru.urfu.bot.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.urfu.bot.db.entities.Chat;

@Repository
public interface JpaChatRepository extends JpaRepository<Chat, Long> { }
