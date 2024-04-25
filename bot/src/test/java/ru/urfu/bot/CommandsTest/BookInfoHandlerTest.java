package ru.urfu.bot.CommandsTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.db.entities.Book;
import ru.urfu.bot.handlers.callbacks.BookInfoHandler;
import ru.urfu.bot.services.UserBookService;
import ru.urfu.bot.utils.MessageConst;
import ru.urfu.bot.utils.dto.Command;
import ru.urfu.bot.utils.dto.CommandType;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookInfoHandlerTest {

    String username = "username";

    long chatId = 1;

    BookInfoHandler bookInfoHandler;

    @Mock
    UserBookService userBookService;

    @Mock
    Book book;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    Update update;

    @BeforeEach
    void init() {
        bookInfoHandler = new BookInfoHandler(userBookService);
    }

    private void mockUpdate(String username, Long chatId) {
        Mockito.when(update.getCallbackQuery().getMessage().getChatId()).thenReturn(chatId);
        Mockito.when(update.getCallbackQuery().getFrom().getUserName()).thenReturn(username);
    }

    @Test
    void sendCorrectInfoTest() {
        // Arrange
        long isbn = 1;
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

        when(userBookService.findBookByIsbn(eq(username), eq(isbn))).thenReturn(Optional.of(book));

        mockUpdate(username, chatId);

        Command command = new Command(CommandType.INFO, Long.toString(isbn));
        SendMessage expected = SendMessage.builder()
                .chatId(chatId)
                .text(MessageConst.BOOK_INFO.formatted(isbn, title, description, author, publisher, publishedDate))
                .build();

        // Act
        List<SendMessage> actual_msg = bookInfoHandler.handle(command, update);

        // Assert
        verify(userBookService).findBookByIsbn(username, isbn);
        assertFalse(actual_msg.isEmpty());
        assertEquals(expected, actual_msg.getFirst());
    }

    @Test
    void bookNotFoundTest() {
        // Arrange
        long isbn = 2;
        when(userBookService.findBookByIsbn(eq(username), eq(isbn))).thenReturn(Optional.empty());
        mockUpdate(username, chatId);
        Command command = new Command(CommandType.INFO, Long.toString(isbn));
        SendMessage expected = SendMessage.builder()
                .chatId(chatId).text(MessageConst.BOOK_NOT_FOUND_IN_FAVORITE.formatted(isbn))
                .build();

        // Act
        List<SendMessage> actual_msg = bookInfoHandler.handle(command, update);

        // Assert
        verify(userBookService).findBookByIsbn(username, isbn);
        assertFalse(actual_msg.isEmpty());
        assertEquals(expected, actual_msg.getFirst());
    }

    @Test
    void userNotFoundTest() {
        // Arrange
        long isbn = 1;
        String unavailable_user = "new_user";

        doThrow(NoSuchElementException.class).when(userBookService).findBookByIsbn(eq(unavailable_user), eq(isbn));
        mockUpdate(unavailable_user, chatId);

        Command command = new Command(CommandType.INFO, Long.toString(isbn));

        // Act
        List<SendMessage> actual_msg = bookInfoHandler.handle(command, update);

        // Assert
        verify(userBookService).findBookByIsbn(unavailable_user, isbn);
        assertTrue(actual_msg.isEmpty());
    }
}
