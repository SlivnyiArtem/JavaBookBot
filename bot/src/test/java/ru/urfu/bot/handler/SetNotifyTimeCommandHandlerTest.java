package ru.urfu.bot.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.handler.commands.SetNotifyTimeCommandHandler;
import ru.urfu.bot.service.ChatUserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Тест на класс {@link ru.urfu.bot.handler.commands.SetNotifyTimeCommandHandler}
 */
@ExtendWith(MockitoExtension.class)
public class SetNotifyTimeCommandHandlerTest {

    @Mock
    private ChatUserService chatUserService;

    @InjectMocks
    private SetNotifyTimeCommandHandler setNotifyTimeCommandHandler;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Update update;

    private final String username = "username";

    private final Long chatId = 1L;

    private final static String INTERNAL_SERVER_ERROR = "Внутренняя ошибка сервера";

    /**
     * Правильный update
     */
    @Test
    public void canHandleCorrectUpdateTest() {
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage().hasText()).thenReturn(true);
        when(update.getMessage().getText()).thenReturn("/set_time 10:00:59+01:00");
        when(update.getMessage().getChat().getId()).thenReturn(chatId);
        when(update.getMessage().getChat().getUserName()).thenReturn(username);

        assertTrue(setNotifyTimeCommandHandler.canHandle(update));
        List<SendMessage> actual = setNotifyTimeCommandHandler.process(update);
        assertEquals("Установленно время для получения уведомлений", actual.getFirst().getText());

        when(update.getMessage().getText()).thenReturn("/set_time 00:00+01:00");
        assertTrue(setNotifyTimeCommandHandler.canHandle(update));

        when(update.getMessage().getText()).thenReturn("/set_time 00:00-18:00");
        assertTrue(setNotifyTimeCommandHandler.canHandle(update));
    }

    /**
     * Неправильная команда
     */
    @Test
    public void incorrectCommandTest() {
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage().hasText()).thenReturn(true);
        when(update.getMessage().getText()).thenReturn("/do_something 00:00:00+00:00");
        when(update.getMessage().getChat().getId()).thenReturn(chatId);
        when(update.getMessage().getChat().getUserName()).thenReturn(username);

        assertFalse(setNotifyTimeCommandHandler.canHandle(update));
        assertTrue(setNotifyTimeCommandHandler.process(update).isEmpty());

        when(update.getMessage().getText()).thenReturn("/set_time 24:00:59+01:00");
        assertEquals(INTERNAL_SERVER_ERROR, setNotifyTimeCommandHandler.process(update).getFirst().getText());

        when(update.getMessage().getText()).thenReturn("/set_time 23:80:59+01:00");
        assertEquals(INTERNAL_SERVER_ERROR, setNotifyTimeCommandHandler.process(update).getFirst().getText());

        when(update.getMessage().getText()).thenReturn("/set_time 23:50:70+01:00");
        assertEquals(INTERNAL_SERVER_ERROR, setNotifyTimeCommandHandler.process(update).getFirst().getText());

        when(update.getMessage().getText()).thenReturn("/set_time 00:00:00");
        assertEquals(INTERNAL_SERVER_ERROR, setNotifyTimeCommandHandler.process(update).getFirst().getText());

        when(update.getMessage().getText()).thenReturn("/set_time 00:00+20:00");
        assertEquals(INTERNAL_SERVER_ERROR, setNotifyTimeCommandHandler.process(update).getFirst().getText());

        when(update.getMessage().getText()).thenReturn("/set_time ffffff");
        assertEquals(INTERNAL_SERVER_ERROR, setNotifyTimeCommandHandler.process(update).getFirst().getText());
    }

    /**
     * Update не является командой
     */
    @Test
    public void notCommandUpdateTest() {
        when(update.hasMessage()).thenReturn(false);

        assertFalse(setNotifyTimeCommandHandler.canHandle(update));
        List<SendMessage> actual = setNotifyTimeCommandHandler.process(update);
        assertTrue(actual.isEmpty());
    }

    /**
     * Update с неправильными полями
     */
    @Test
    public void constrainsUpdateViolationTest() {
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage().hasText()).thenReturn(true);
        when(update.getMessage().getText()).thenReturn("");
        when(update.getMessage().getChat().getId()).thenReturn(null);
        when(update.getMessage().getChat().getUserName()).thenReturn("");

        assertFalse(setNotifyTimeCommandHandler.canHandle(update));
        List<SendMessage> actual = setNotifyTimeCommandHandler.process(update);
        assertTrue(actual.isEmpty());
    }

    /**
     * Частично не иницализированный update
     */
    @Test
    public void notCompleteUpdateTest() {
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage().hasText()).thenReturn(true);
        when(update.getMessage().getText()).thenReturn(null);
        when(update.getMessage().getChat()).thenReturn(null);

        assertFalse(setNotifyTimeCommandHandler.canHandle(update));
        List<SendMessage> actual = setNotifyTimeCommandHandler.process(update);
        assertTrue(actual.isEmpty());
    }
}
