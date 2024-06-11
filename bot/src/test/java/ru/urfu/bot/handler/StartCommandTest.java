package ru.urfu.bot.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.command.StartCommand;
import ru.urfu.bot.service.ChatUserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Тест на класс {@link StartCommand}
 */
@ExtendWith(MockitoExtension.class)
public class StartCommandTest {

    @Mock
    private ChatUserService chatUserService;

    @InjectMocks
    private StartCommand startCommand;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Update update;

    private final String username = "username";

    private final Long chatId = 1L;

    private static final String HELP_MESSAGE = """
                /start - начать работать с ботом
                /search {title} - поиск книги по названию
                /my_books - вывести список книг
                /set_time {time} - установить время для получения уведомлений (например '10:15+01:00' или '10:15:30+01:00')
                /help - помощь
                """;

    /**
     * Update - команда
     */
    @Test
    public void createNewUserCommandTest() {
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage().hasText()).thenReturn(true);
        when(update.getMessage().getText()).thenReturn("/start");
        when(update.getMessage().getChat().getId()).thenReturn(chatId);
        when(update.getMessage().getChat().getUserName()).thenReturn(username);

        when(chatUserService.addUserChatIfAbsent(eq(username), eq(chatId))).thenReturn(true);
        assertTrue(startCommand.canHandle(update));
        List<SendMessage> actual = startCommand.process(update);
        assertEquals(HELP_MESSAGE, actual.getFirst().getText());

        when(chatUserService.addUserChatIfAbsent(eq(username), eq(chatId))).thenReturn(false);
        assertTrue(startCommand.canHandle(update));
        actual = startCommand.process(update);
        assertTrue(actual.isEmpty());
    }

    /**
     * Update не является командой
     */
    @Test
    public void notCommandUpdateTest() {
        when(update.hasMessage()).thenReturn(false);

        assertFalse(startCommand.canHandle(update));
        List<SendMessage> actual = startCommand.process(update);
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

        assertFalse(startCommand.canHandle(update));
        List<SendMessage> actual = startCommand.process(update);
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

        assertFalse(startCommand.canHandle(update));
        List<SendMessage> actual = startCommand.process(update);
        assertTrue(actual.isEmpty());
    }
}
