package ru.urfu.bot.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.domain.Book;
import ru.urfu.bot.command.callback.BookInfoCallbackCommand;
import ru.urfu.bot.service.BookTrackingService;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * Тест на класс {@link BookInfoCallbackCommand}
 */
@ExtendWith(MockitoExtension.class)
public class BookInfoCallbackCommandTest {

    @Mock
    private BookTrackingService bookTrackingService;

    @InjectMocks
    private BookInfoCallbackCommand bookInfoCallbackCommand;

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
        when(update.getCallbackQuery().getData()).thenReturn("/book_inf " + isbn);
        when(update.getCallbackQuery().getMessage().getChatId()).thenReturn(chatId);
        when(update.getCallbackQuery().getFrom().getUserName()).thenReturn(username);

        String title = "title";
        String description = "description";
        String author = "author1";
        String publisher = "publisher";
        LocalDate publishedDate = LocalDate.of(2000, 1, 1);

        Book book = new Book();
        book.setIsbn(isbn);
        book.setTitle(title);
        book.setDescription(description);
        book.setAuthors(author);
        book.setPublisher(publisher);
        book.setPublishedDate(publishedDate);
        when(bookTrackingService.getBook(eq(isbn))).thenReturn(book);

        String message = "ISBN: %d\nНазвание: %s\nОписание: %s\nАвторы: %s\nИздатель: %s\nДата издания: %s".formatted(
                isbn, title, description, author, publisher, publishedDate
        );
        assertTrue(bookInfoCallbackCommand.canHandle(update));
        List<SendMessage> actual = bookInfoCallbackCommand.process(update);
        assertEquals(List.of(new SendMessage(chatId.toString(), message)), actual);
    }

    /**
     * Сущность не найденна в бд
     */
    @Test
    public void entityNotFoundTest() {
        when(update.hasCallbackQuery()).thenReturn(true);
        when(update.getCallbackQuery().getData()).thenReturn("/book_inf " + isbn);
        when(update.getCallbackQuery().getMessage().getChatId()).thenReturn(chatId);
        when(update.getCallbackQuery().getFrom().getUserName()).thenReturn(username);

        doThrow(NoSuchElementException.class).when(bookTrackingService).getBook(eq(isbn));

        assertTrue(bookInfoCallbackCommand.canHandle(update));
        List<SendMessage> actual = bookInfoCallbackCommand.process(update);
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

        assertFalse(bookInfoCallbackCommand.canHandle(update));
        List<SendMessage> actual = bookInfoCallbackCommand.process(update);
        assertTrue(actual.isEmpty());
    }

    /**
     * Update не является коллбэком
     */
    @Test
    public void notCallbackUpdateTest() {
        when(update.hasCallbackQuery()).thenReturn(false);

        assertFalse(bookInfoCallbackCommand.canHandle(update));
        List<SendMessage> actual = bookInfoCallbackCommand.process(update);
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

        assertFalse(bookInfoCallbackCommand.canHandle(update));
        List<SendMessage> actual = bookInfoCallbackCommand.process(update);
        assertTrue(actual.isEmpty());
    }

    /**
     * Частично не иницализированный update
     */
    @Test
    public void notCompleteUpdateTest() {
        when(update.hasCallbackQuery()).thenReturn(true);
        when(update.getCallbackQuery().getData()).thenReturn(null);
        when(update.getCallbackQuery().getMessage()).thenReturn(null);
        when(update.getCallbackQuery().getFrom()).thenReturn(null);

        assertFalse(bookInfoCallbackCommand.canHandle(update));
        List<SendMessage> actual = bookInfoCallbackCommand.process(update);
        assertTrue(actual.isEmpty());
    }
}
