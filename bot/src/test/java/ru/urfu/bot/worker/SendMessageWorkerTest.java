package ru.urfu.bot.worker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.urfu.bot.bot.BookTrackingBot;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

/**
 * Тесты на класс {@link SendMessageWorker}
 */
@ExtendWith(MockitoExtension.class)
public class SendMessageWorkerTest {

    private BlockingQueue<BotApiMethodMessage> sendQueue;

    @Mock
    private BookTrackingBot bookTrackingBot;

    private SendMessageWorker sendMessageWorker;

    @Mock
    private SendMessage sendMessage;

    @BeforeEach
    public void init() {
        sendQueue = new LinkedBlockingQueue<>();
        sendMessageWorker = new SendMessageWorker(sendQueue, bookTrackingBot);
    }

    /**
     * Проверяется:
     * <ul>
     *     <li>Если текущее время больше запланированного времени отправки, то сообщение отправляется</li>
     *     <li>Если текущее время меньше запланированного времени отправки, то сообщение не отправляется</li>
     *     <li>Если сообщение не имеет запланированного времени, то оно отправляется</li>
     * </ul>
     */
    @Test
    public void addScheduledMessageTest() throws InterruptedException, TelegramApiException {
        OffsetDateTime scheduledTime = OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        SendScheduledMessage sendScheduledMessage = new SendScheduledMessage(sendMessage, scheduledTime);

        try (MockedStatic<OffsetDateTime> offsetDateTime = Mockito.mockStatic(OffsetDateTime.class)) {
            offsetDateTime.when(OffsetDateTime::now).thenReturn(scheduledTime.plusSeconds(10L));
            sendQueue.put(sendScheduledMessage);
            sendMessageWorker.sendMessage();
            verify(bookTrackingBot, only()).execute(eq(sendScheduledMessage));
            assertTrue(sendQueue.isEmpty());

            offsetDateTime.when(OffsetDateTime::now).thenReturn(scheduledTime.minusSeconds(10L));
            sendQueue.put(sendScheduledMessage);
            sendMessageWorker.sendMessage();
            assertEquals(sendScheduledMessage, sendQueue.take());
        }

        sendQueue.put(sendMessage);
        sendMessageWorker.sendMessage();
        verify(bookTrackingBot).execute(eq(sendMessage));
        assertTrue(sendQueue.isEmpty());
    }
}
