package ru.urfu.bot.services;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.utils.dto.SendScheduledMessage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Сервис предоставляет две блокирующие очереди: для отправляемых сообщений и
 * получаемых обновлений
 */
@Service
public class QueueProvider {

    private final BlockingQueue<SendMessage> sendQueue;

    private final BlockingQueue<Update> receiveQueue;

    /**
     * @return очередь с сообщениями для отправки
     */
    public BlockingQueue<SendMessage> getSendQueue() {
        return sendQueue;
    }

    /**
     * @return очередь с обновлениями для обработки
     */
    public BlockingQueue<Update> getReceiveQueue() {
        return receiveQueue;
    }

    /**
     * Инициализирует две блокирующие очереди. Для отправки используется очередь с
     * приоритетами. Наивысшим приоритетом обладают сообщения с ближайшим временем
     * отправки, а также сообщения без указанного времени.
     */
    public QueueProvider() {
        sendQueue = new PriorityBlockingQueue<>(10, (o1, o2) -> {
            if (o1.equals(o2)) {
                return 0;
            }
            if (o1 instanceof SendScheduledMessage message1) {
                if (o2 instanceof SendScheduledMessage message2) {
                    return message1.getReceivingTime().compareTo(message2.getReceivingTime());
                }
                return 1;
            }
            return -1;
        });
        receiveQueue = new LinkedBlockingQueue<>();
    }
}
