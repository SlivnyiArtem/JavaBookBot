package ru.urfu.bot;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Тесты на класс {@link QueueProvider}
 */
public class QueueProviderTest {

    private final QueueProvider queueProvider = new QueueProvider();

    /**
     * Проверяется правильный порядок в очереди на отправку.
     * Порядок такой:
     * <ol>
     *     <li>Сообщения без времени отправления</li>
     *     <li>Сообщения с наименьшим временем отправления</li>
     * </ol>
     */
    @Test
    public void correctOrderTest() throws InterruptedException {
        SendMessage sendMessage = new SendMessage();
        OffsetDateTime baseTime = OffsetDateTime.of(
                2000, 1, 15, 10, 0, 0, 0, ZoneOffset.ofHours(0));

        BotApiMethodMessage message1 = new SendScheduledMessage(sendMessage, baseTime);
        BotApiMethodMessage message2 = new SendScheduledMessage(sendMessage, baseTime.plus(Duration.ofSeconds(10)));
        BotApiMethodMessage message3 = new SendMessage();
        BotApiMethodMessage message4 = new SendScheduledMessage(sendMessage, baseTime.minus(Duration.ofDays(5)));
        BotApiMethodMessage message5 = new SendScheduledMessage(sendMessage, baseTime.plus(Duration.ofDays(15)));

        queueProvider.getSendQueue().put(message1);
        queueProvider.getSendQueue().put(message2);
        queueProvider.getSendQueue().put(message3);
        queueProvider.getSendQueue().put(message4);
        queueProvider.getSendQueue().put(message5);

        assertEquals(message3, queueProvider.getSendQueue().take());
        assertEquals(message4, queueProvider.getSendQueue().take());
        assertEquals(message1, queueProvider.getSendQueue().take());
        assertEquals(message2, queueProvider.getSendQueue().take());
        assertEquals(message5, queueProvider.getSendQueue().take());
    }
}
