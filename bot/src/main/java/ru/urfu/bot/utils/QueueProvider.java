package ru.urfu.bot.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Предоставляет две блокирующие очереди: для отправляемых сообщений и
 * получаемых обновлений
 */
@Component
public class QueueProvider {

    /**
     * Максимальный размер очередей
     */
    public static final int INITIAL_CAPACITY = 10;

    private final BlockingQueue<BotApiMethodMessage> sendQueue;

    private final BlockingQueue<Update> receivedQueue;

    /**
     * @return очередь с сообщениями для отправки
     */
    public BlockingQueue<BotApiMethodMessage> getSendQueue() {
        return sendQueue;
    }

    /**
     * @return очередь с обновлениями для обработки
     */
    public BlockingQueue<Update> getReceivedQueue() {
        return receivedQueue;
    }

    /**
     * Инициализирует две блокирующие очереди. Для отправки используется очередь с
     * приоритетами. Наивысшим приоритетом обладают сообщения с ближайшим временем
     * отправки, а также сообщения без указанного времени.
     */
    public QueueProvider() {
        sendQueue = new PriorityBlockingQueue<>(INITIAL_CAPACITY, (o1, o2) -> {
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
        receivedQueue = new LinkedBlockingQueue<>(INITIAL_CAPACITY);
    }
}
