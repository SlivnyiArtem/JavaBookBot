package ru.urfu.bot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.urfu.bot.domain.BotData;

@Repository
public interface BotDataRepository extends MongoRepository<BotData, Long> {
}
