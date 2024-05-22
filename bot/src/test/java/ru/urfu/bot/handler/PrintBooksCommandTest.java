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
import ru.urfu.bot.handler.commands.PrintBooksCommandHandler;
import ru.urfu.bot.service.BookTrackingService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Тест на класс {@link PrintBooksCommandHandler}
 */
@ExtendWith(MockitoExtension.class)
public class PrintBooksCommandTest {

    @Mock
    private BookTrackingService bookTrackingService;

    @InjectMocks
    private PrintBooksCommandHandler printBooksCommandHandler;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Update update;

    private final String username = "username";

    private final Long chatId = 1L;

    private final Long isbn = 1L;

    private final String title = "book";

    /**
     * Правильный update
     */
    @Test
    public void canHandleCorrectUpdateTest() {
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage().hasText()).thenReturn(true);
        when(update.getMessage().getText()).thenReturn("/my_books");
        when(update.getMessage().getChat().getId()).thenReturn(chatId);
        when(update.getMessage().getChat().getUserName()).thenReturn(username);

        String author = "author1";
        LocalDate publishedDate = LocalDate.of(2000, 1, 1);

        Book book = new Book();
        book.setIsbn(isbn);
        book.setTitle(title);
        book.setAuthors(author);
        book.setPublishedDate(publishedDate);
        when(bookTrackingService.getUserBooks(eq(username))).thenReturn(List.of(book));

        String message = "ISBN: %d\nНазвание: %s\nАвторы: %s\nДата издания: %s".formatted(
                isbn, title, author, publishedDate
        );
        assertTrue(printBooksCommandHandler.canHandle(update));
        List<SendMessage> actual = printBooksCommandHandler.process(update);
        assertEquals(message, actual.getFirst().getText());
    }

    /**
     * Результаты поиска - книги не найдены
     */
    @Test
    public void emptyListTest() {
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage().hasText()).thenReturn(true);
        when(update.getMessage().getText()).thenReturn("/my_books");
        when(update.getMessage().getChat().getId()).thenReturn(chatId);
        when(update.getMessage().getChat().getUserName()).thenReturn(username);

        when(bookTrackingService.getUserBooks(eq(username))).thenReturn(List.of());

        assertTrue(printBooksCommandHandler.canHandle(update));
        List<SendMessage> actual = printBooksCommandHandler.process(update);
        assertEquals(List.of(new SendMessage(chatId.toString(), "Книги не найдены")), actual);
    }

    /**
     * Неправильная команда
     */
    @Test
    public void incorrectCommandTest() {
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage().hasText()).thenReturn(true);
        when(update.getMessage().getText()).thenReturn("/do_something " + title);
        when(update.getMessage().getChat().getId()).thenReturn(chatId);
        when(update.getMessage().getChat().getUserName()).thenReturn(username);

        assertFalse(printBooksCommandHandler.canHandle(update));
        List<SendMessage> actual = printBooksCommandHandler.process(update);
        assertTrue(actual.isEmpty());
    }

    /**
     * Update не является командой
     */
    @Test
    public void notCommandUpdateTest() {
        when(update.hasMessage()).thenReturn(false);

        assertFalse(printBooksCommandHandler.canHandle(update));
        List<SendMessage> actual = printBooksCommandHandler.process(update);
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

        assertFalse(printBooksCommandHandler.canHandle(update));
        List<SendMessage> actual = printBooksCommandHandler.process(update);
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

        assertFalse(printBooksCommandHandler.canHandle(update));
        List<SendMessage> actual = printBooksCommandHandler.process(update);
        assertTrue(actual.isEmpty());
    }
}