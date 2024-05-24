package ru.urfu.bot.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.urfu.bot.bot.Bot;
import ru.urfu.bot.utils.SendScheduledMessage;

import java.time.OffsetDateTime;
import java.util.concurrent.BlockingQueue;

/**
 * Воркер для отправки сообщений.
 */
@Component
public class SendMessageWorker {

    private final Logger logger = LoggerFactory.getLogger(SendMessageWorker.class);

    private final BlockingQueue<BotApiMethodMessage> sendQueue;

    private final Bot bot;

    /**
     * @param sendQueue очередь сообщений
     * @param bot телеграм клиент
     */
    public SendMessageWorker(BlockingQueue<BotApiMethodMessage> sendQueue, Bot bot) {
        this.sendQueue = sendQueue;
        this.bot = bot;
    }

    /**
     * Достает сообщение из очереди и отправляет пользователю. Если сообщение
     * имеет дату отправки, то не отправляет его пока не подошло время.
     */
    @Scheduled(fixedDelayString = "#{ @scheduler.sendDelay() }")
    @Transactional
    public void sendMessage() {
        try {
            BotApiMethodMessage sendMessage = sendQueue.take();
            if (sendMessage instanceof SendScheduledMessage scheduled
                    && OffsetDateTime.now().isBefore(scheduled.getReceivingTime())) {
                sendQueue.put(sendMessage);
            } else {
                bot.execute(sendMessage);
            }
        } catch (InterruptedException | TelegramApiException e) {
            logger.error(e.getMessage());
        }
    }
}
