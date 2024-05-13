package ru.urfu.bot.CommandsTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.urfu.bot.db.entities.Book;
import ru.urfu.bot.db.exceptions.BookNotFoundException;
import ru.urfu.bot.db.repositories.JpaBookRepository;
import ru.urfu.bot.services.handlers.callbacks.BookInfoService;
import ru.urfu.bot.utils.MessageConst;
import ru.urfu.bot.utils.dto.Command;
import ru.urfu.bot.utils.dto.CommandType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookInfoServiceTest {

    private final String username = "username";

    private final String chatId = "1";

    private final long isbn = 1;

    private BookInfoService bookInfoService;

    @Mock
    private JpaBookRepository bookRepository;

    @Mock
    private Book book;

    @BeforeEach
    void init() {
        bookInfoService = new BookInfoService(bookRepository);
    }

    @Test
    void sendCorrectInfoTest() {
        // Arrange
        String title = "title";
        String description = "description";
        String author = "author";
        String publisher = "publisher";
        LocalDate publishedDate = LocalDate.of(2000, 1, 1);

        when(book.getIsbn13()).thenReturn(isbn);
        when(book.getTitle()).thenReturn(title);
        when(book.getDescription()).thenReturn(description);
        when(book.getAuthors()).thenReturn(author);
        when(book.getPublisher()).thenReturn(publisher);
        when(book.getPublishedDate()).thenReturn(publishedDate);

        when(bookRepository.findByIsbn13AndUsers_UserName(eq(isbn), eq(username))).thenReturn(Optional.of(book));

        Command command = new Command(CommandType.INFO, Long.toString(isbn));
        SendMessage expected = SendMessage.builder()
                .chatId(chatId)
                .text(MessageConst.BOOK_INFO.formatted(isbn, title, description, author, publisher, publishedDate))
                .build();

        // Act
        List<SendMessage> actual_msg = bookInfoService.handle(command, username, chatId);

        // Assert
        verify(bookRepository).findByIsbn13AndUsers_UserName(isbn, username);
        assertFalse(actual_msg.isEmpty());
        assertEquals(expected, actual_msg.getFirst());
    }

    @Test
    void bookNotFoundTest() {
        // Arrange
        when(bookRepository.findByIsbn13AndUsers_UserName(eq(isbn), eq(username))).thenReturn(Optional.empty());

        Command command = new Command(CommandType.INFO, Long.toString(isbn));
        SendMessage expected = SendMessage.builder()
                .chatId(chatId).text("Книга не найденна в коллекции пользователя")
                .build();

        // Act
        List<SendMessage> actual_msg = bookInfoService.handle(command, username, chatId);

        // Assert
        verify(bookRepository).findByIsbn13AndUsers_UserName(isbn, username);
        assertFalse(actual_msg.isEmpty());
        assertEquals(expected, actual_msg.getFirst());
    }

    @Test
    void userNotFoundTest() {
        // Arrange
        doThrow(BookNotFoundException.class)
                .when(bookRepository)
                .findByIsbn13AndUsers_UserName(eq(isbn), eq(username));

        Command command = new Command(CommandType.INFO, Long.toString(isbn));
        SendMessage expected = SendMessage.builder()
                .chatId(chatId).text("Книга не найденна в коллекции пользователя")
                .build();

        // Act
        List<SendMessage> actual_msg = bookInfoService.handle(command, username, chatId);

        // Assert
        verify(bookRepository).findByIsbn13AndUsers_UserName(isbn, username);
        assertFalse(actual_msg.isEmpty());
        assertEquals(expected, actual_msg.getFirst());
    }
}
