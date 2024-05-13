package ru.urfu.bot.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.urfu.bot.bot.Bot;
import ru.urfu.bot.utils.dto.SendScheduledMessage;

import java.time.OffsetDateTime;
import java.util.concurrent.BlockingQueue;

/**
 * Сервис для асинхронной отправки сообщений.
 */
@Service
public class MessageSender {

    private final static Logger LOG = LoggerFactory.getLogger(MessageSender.class);

    private final BlockingQueue<SendMessage> sendQueue;

    private final Bot bot;

    /**
     * @param queueProvider предоставляет очереди сообщений
     * @param bot телеграм клиент
     */
    public MessageSender(QueueProvider queueProvider, Bot bot) {
        this.sendQueue = queueProvider.getSendQueue();
        this.bot = bot;
    }

    /**
     * Достает сообщение из очереди и отправляет пользователю. Если сообщение
     * имеет дату отправки, то не отправляет его пока не подошло время.
     */
    @Scheduled(fixedRate = 500)
    public void sendMessage() {
        try {
            SendMessage sendMessage = sendQueue.take();
            if (sendMessage instanceof SendScheduledMessage scheduled
                    && OffsetDateTime.now().isBefore(scheduled.getReceivingTime())) {
                sendQueue.put(sendMessage);
            } else {
                LOG.trace("send message {}", sendMessage.getText());
                bot.execute(sendMessage);
            }
        } catch (InterruptedException | TelegramApiException e) {
            LOG.error(e.getMessage());
        }
    }
}
