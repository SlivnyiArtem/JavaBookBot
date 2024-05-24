package ru.urfu.bot.worker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.handler.UpdateHandler;
import ru.urfu.bot.handler.callbacks.CallbackUpdateHandler;
import ru.urfu.bot.handler.commands.CommandUpdateHandler;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Тест на класс {@link DispatchUpdateWorker}
 */
@ExtendWith(MockitoExtension.class)
public class DispatchUpdateWorkerTest {

    @Mock private UpdateHandler handler1;
    @Mock private CallbackUpdateHandler handler2;
    @Mock private CommandUpdateHandler handler3;

    private DispatchUpdateWorker dispatchUpdateWorker;

    private BlockingQueue<BotApiMethodMessage> sendQueue;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Update update;

    /**
     * Создает экземпляр {@link DispatchUpdateWorker} с тестовыми обработчикаи и очередями
     */
    @BeforeEach
    public void init() throws InterruptedException {
        sendQueue = new LinkedBlockingQueue<>();
        BlockingQueue<Update> receivedQueue = new LinkedBlockingQueue<>();
        receivedQueue.put(update);
        dispatchUpdateWorker = new DispatchUpdateWorker(
                List.of(handler1, handler2, handler3), sendQueue, receivedQueue);
    }


    /**
     * Вызываетя обработчик #1
     */
    @Test
    public void dispatchUpdateToHandler1Test() throws InterruptedException {
        List<SendMessage> expectedResponse = List.of(new SendMessage("1", "text"));

        when(handler1.canHandle(eq(update))).thenReturn(true);
        when(handler1.process(eq(update))).thenReturn(expectedResponse);

        dispatchUpdateWorker.dispatch();
        SendMessage actual = (SendMessage) sendQueue.take();
        assertEquals(expectedResponse.getFirst(), actual);
        verify(handler1).process(eq(update));
        verify(handler2, never()).process(any());
        verify(handler3, never()).process(any());
    }

    /**
     * Вызываетя обработчик #2
     */
    @Test
    public void dispatchUpdateToHandler2Test() throws InterruptedException {
        List<SendMessage> expectedResponse = List.of(new SendMessage("1", "text"));

        when(handler2.canHandle(eq(update))).thenReturn(true);
        when(handler2.process(eq(update))).thenReturn(expectedResponse);

        dispatchUpdateWorker.dispatch();
        SendMessage actual = (SendMessage) sendQueue.take();
        assertEquals(expectedResponse.getFirst(), actual);
        verify(handler1, never()).process(any());
        verify(handler2).process(eq(update));
        verify(handler3, never()).process(any());
    }

    /**
     * Вызываетя обработчик #3
     */
    @Test
    public void dispatchUpdateToHandler3Test() throws InterruptedException {
        List<SendMessage> expectedResponse = List.of(new SendMessage("1", "text"));

        when(handler3.canHandle(eq(update))).thenReturn(true);
        when(handler3.process(eq(update))).thenReturn(expectedResponse);

        dispatchUpdateWorker.dispatch();
        SendMessage actual = (SendMessage) sendQueue.take();
        assertEquals(expectedResponse.getFirst(), actual);
        verify(handler1, never()).process(any());
        verify(handler2, never()).process(any());
        verify(handler3).process(eq(update));
    }

    /**
     * Не возвращает ответ при нестандартном обновлении
     */
    @Test
    public void incorrectUpdateTest() {
        dispatchUpdateWorker.dispatch();
        assertTrue(sendQueue.isEmpty());
        verify(handler1, never()).process(any());
        verify(handler2, never()).process(any());
        verify(handler3, never()).process(any());
    }
}
