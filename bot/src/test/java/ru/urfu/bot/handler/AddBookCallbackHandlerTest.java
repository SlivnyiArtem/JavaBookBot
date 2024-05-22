package ru.urfu.bot.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.handler.callbacks.AddBookCallbackHandler;
import ru.urfu.bot.service.BookTrackingService;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Тест на класс {@link AddBookCallbackHandler}
 */
@ExtendWith(MockitoExtension.class)
public class AddBookCallbackHandlerTest {

    @Mock
    private BookTrackingService bookTrackingService;

    @InjectMocks
    private AddBookCallbackHandler addBookCallbackHandler;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Update update;

    private final String username = "username";

    private final Long chatId = 1L;

    private final Long isbn = 1L;

    private static final String ERROR_MESSAGE = "Внутренняя ошибка сервера";

    /**
     * Правильный update
     */
    @Test
    public void canHandleCorrectUpdateTest() {
        when(update.hasCallbackQuery()).thenReturn(true);
        when(update.getCallbackQuery().getData()).thenReturn("/add_book " + isbn);
        when(update.getCallbackQuery().getMessage().getChatId()).thenReturn(chatId);
        when(update.getCallbackQuery().getFrom().getUserName()).thenReturn(username);

        assertTrue(addBookCallbackHandler.canHandle(update));
        List<SendMessage> actual = addBookCallbackHandler.process(update);
        assertEquals(List.of(new SendMessage(chatId.toString(), "Книга добавлена в избранное")), actual);
        verify(bookTrackingService).trackBook(isbn, username);
    }

    /**
     * Сущность не найденна в бд
     */
    @Test
    public void entityNotFoundTest() {
        when(update.hasCallbackQuery()).thenReturn(true);
        when(update.getCallbackQuery().getData()).thenReturn("/add_book " + isbn);
        when(update.getCallbackQuery().getMessage().getChatId()).thenReturn(chatId);
        when(update.getCallbackQuery().getFrom().getUserName()).thenReturn(username);

        doThrow(NoSuchElementException.class).when(bookTrackingService).trackBook(eq(isbn), eq(username));

        assertTrue(addBookCallbackHandler.canHandle(update));
        List<SendMessage> actual = addBookCallbackHandler.process(update);
        assertEquals(List.of(new SendMessage(chatId.toString(), ERROR_MESSAGE)), actual);
    }

    /**
     * Неправильная команда
     */
    @Test
    public void incorrectCommandTest() {
        when(update.hasCallbackQuery()).thenReturn(true);
        when(update.getCallbackQuery().getData()).thenReturn("/save_book one");
        when(update.getCallbackQuery().getMessage().getChatId()).thenReturn(chatId);
        when(update.getCallbackQuery().getFrom().getUserName()).thenReturn(username);

        assertFalse(addBookCallbackHandler.canHandle(update));
        List<SendMessage> actual = addBookCallbackHandler.process(update);
        assertTrue(actual.isEmpty());
    }

    /**
     * Update не является коллбэком
     */
    @Test
    public void notCallbackUpdateTest() {
        when(update.hasCallbackQuery()).thenReturn(false);

        assertFalse(addBookCallbackHandler.canHandle(update));
        List<SendMessage> actual = addBookCallbackHandler.process(update);
        assertTrue(actual.isEmpty());
    }

    /**
     * Update с неправильными полями
     */
    @Test
    public void constrainsUpdateViolationTest() {
        when(update.hasCallbackQuery()).thenReturn(true);
        when(update.getCallbackQuery().getFrom().getUserName()).thenReturn("");
        when(update.getCallbackQuery().getMessage().getChatId()).thenReturn(null);
        when(update.getCallbackQuery().getData()).thenReturn("");

        assertFalse(addBookCallbackHandler.canHandle(update));
        List<SendMessage> actual = addBookCallbackHandler.process(update);
        assertTrue(actual.isEmpty());
    }

    /**
     * Частично не иницализированный update
     */
    @Test
    public void notCompleteUpdateTest() {
        when(update.hasCallbackQuery()).thenReturn(true);
        when(update.getCallbackQuery().getData()).thenReturn("/add_book " + isbn);
        when(update.getCallbackQuery().getMessage()).thenReturn(null);
        when(update.getCallbackQuery().getFrom()).thenReturn(null);

        assertFalse(addBookCallbackHandler.canHandle(update));
        List<SendMessage> actual = addBookCallbackHandler.process(update);
        assertTrue(actual.isEmpty());
    }
}
