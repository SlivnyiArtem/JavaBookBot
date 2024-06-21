package edu.telegram.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import edu.telegram.domain.BotData;

@Repository
public interface BotDataRepository extends MongoRepository<BotData, Long> {
}
