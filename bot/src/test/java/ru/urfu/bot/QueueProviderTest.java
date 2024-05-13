package ru.urfu.bot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.urfu.bot.services.QueueProvider;
import ru.urfu.bot.utils.dto.SendScheduledMessage;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QueueProviderTest {

    private QueueProvider queueProvider;

    @BeforeEach
    public void init() {
        queueProvider = new QueueProvider();
    }

    @Test
    public void correctTest() throws InterruptedException {
        OffsetDateTime baseTime = OffsetDateTime.of(
                2000, 1, 15, 10, 0, 0, 0, ZoneOffset.ofHours(0));

        SendMessage message1 = new SendScheduledMessage(baseTime);
        SendMessage message2 = new SendScheduledMessage(baseTime.plus(Duration.ofSeconds(10)));
        SendMessage message3 = new SendMessage();
        SendMessage message4 = new SendScheduledMessage(baseTime.minus(Duration.ofDays(5)));
        SendMessage message5 = new SendScheduledMessage(baseTime.plus(Duration.ofDays(15)));

        queueProvider.getSendQueue().put(message1);
        queueProvider.getSendQueue().put(message2);
        queueProvider.getSendQueue().put(message3);
        queueProvider.getSendQueue().put(message4);
        queueProvider.getSendQueue().put(message5);

        assertEquals(message5, queueProvider.getSendQueue().take());
        assertEquals(message3, queueProvider.getSendQueue().take());
        assertEquals(message2, queueProvider.getSendQueue().take());
        assertEquals(message1, queueProvider.getSendQueue().take());
        assertEquals(message4, queueProvider.getSendQueue().take());
    }
}
