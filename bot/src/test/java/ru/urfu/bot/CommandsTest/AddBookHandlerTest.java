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
import ru.urfu.bot.handlers.callbacks.AddBookHandler;
import ru.urfu.bot.services.UserBookService;
import ru.urfu.bot.utils.MessageConst;
import ru.urfu.bot.utils.dto.Command;
import ru.urfu.bot.utils.dto.CommandType;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddBookHandlerTest {

    String username = "username";

    long chatId = 1;

    AddBookHandler addBookHandler;

    @Mock
    UserBookService userBookService;

    @Mock
    Book book;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    Update update;

    @BeforeEach
    void init() {
        addBookHandler = new AddBookHandler(userBookService);
    }

    private void mockUpdate(String username, Long chatId) {
        Mockito.when(update.getCallbackQuery().getMessage().getChatId()).thenReturn(chatId);
        Mockito.when(update.getCallbackQuery().getFrom().getUserName()).thenReturn(username);
    }

    @Test
    void addBookTest() {
        // Arrange
        long isbn = 1;
        when(userBookService.findBookByIsbn(eq(isbn))).thenReturn(Optional.of(book));
        mockUpdate(username, chatId);
        Command command = new Command(CommandType.ADD, Long.toString(isbn));
        SendMessage expected = SendMessage.builder()
                .chatId(chatId).text(MessageConst.ADD_BOOK)
                .build();

        // Act
        List<SendMessage> actual_msg = addBookHandler.handle(command, update);

        // Assert
        verify(userBookService).addBook(username, book);
        assertFalse(actual_msg.isEmpty());
        assertEquals(expected, actual_msg.getFirst());
    }

    @Test
    void bookNotFoundTest() {
        // Arrange
        long isbn = 2;
        when(userBookService.findBookByIsbn(eq(isbn))).thenReturn(Optional.empty());
        mockUpdate(username, chatId);
        Command command = new Command(CommandType.ADD, Long.toString(isbn));
        SendMessage expected = SendMessage.builder()
                .chatId(chatId).text(MessageConst.BOOK_NOT_FOUND_IN_SOURCE.formatted(isbn))
                .build();

        // Act
        List<SendMessage> actual_msg = addBookHandler.handle(command, update);

        // Assert
        verify(userBookService, times(0)).addBook(username, book);
        assertFalse(actual_msg.isEmpty());
        assertEquals(expected, actual_msg.getFirst());
    }

    @Test
    void userNotFoundTest() {
        // Arrange
        long isbn = 1;
        String unavailable_user = "new_user";
        when(userBookService.findBookByIsbn(eq(isbn))).thenReturn(Optional.of(book));
        doThrow(NoSuchElementException.class).when(userBookService).addBook(eq(unavailable_user), eq(book));
        mockUpdate(unavailable_user, chatId);
        Command command = new Command(CommandType.ADD, Long.toString(isbn));

        // Act
        List<SendMessage> actual_msg = addBookHandler.handle(command, update);

        // Assert
        verify(userBookService).addBook(unavailable_user, book);
        assertTrue(actual_msg.isEmpty());
    }
}
