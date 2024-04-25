package ru.urfu.bot.CommandsTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.urfu.bot.db.entities.Book;
import ru.urfu.bot.handlers.commands.PrintBooksHandler;
import ru.urfu.bot.handlers.commands.StartBotHandler;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PrintBooksCommandTest {

    String username = "username";

    long chatId = 1;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    Update update;

    PrintBooksHandler printBooksHandler;

    @Mock
    UserBookService userBookService;

    @Mock
    Book book1;

    @Mock
    Book book2;

    @BeforeEach
    void init() {
        printBooksHandler = new PrintBooksHandler(userBookService);
    }

    private void mockUpdate(String username, Long chatId) {
        when(update.getMessage().getChatId()).thenReturn(chatId);
        when(update.getMessage().getChat().getUserName()).thenReturn(username);
    }

    private void mockUserBookList() {
        long isbn1 = 1;
        String title1 = "title1";
        String author1 = "author1";
        LocalDate publishedDate1 = LocalDate.of(2000, 1, 1);

        when(book1.getIsbn13()).thenReturn(isbn1);
        when(book1.getTitle()).thenReturn(title1);
        when(book1.getAuthors()).thenReturn(author1);
        when(book1.getPublishedDate()).thenReturn(publishedDate1);

        long isbn2 = 2;
        String title2 = "title2";
        String author2 = "author2";
        LocalDate publishedDate2 = LocalDate.of(2000, 1, 2);

        when(book2.getIsbn13()).thenReturn(isbn2);
        when(book2.getTitle()).thenReturn(title2);
        when(book2.getAuthors()).thenReturn(author2);
        when(book2.getPublishedDate()).thenReturn(publishedDate2);

        when(userBookService.getUserBooks(eq(username))).thenReturn(List.of(book1, book2));
    }

    private SendMessage createSendMessage(Long chatId, Book book) {
        InlineKeyboardButton removeButton = new InlineKeyboardButton();
        removeButton.setText(MessageConst.REMOVE_BUTTON_TEXT);
        removeButton.setCallbackData(MessageConst.REMOVE_BUTTON_CALLBACK.formatted(book.getIsbn13()));

        InlineKeyboardButton infoButton = new InlineKeyboardButton();
        infoButton.setText(MessageConst.INFO_BUTTON_TEXT);
        infoButton.setCallbackData(MessageConst.INFO_BUTTON_CALLBACK.formatted(book.getIsbn13()));

        return SendMessage.builder()
                .chatId(chatId)
                .text(MessageConst.BOOK_INFO_SHORT.formatted(book.getIsbn13(), book.getTitle(),
                        book.getAuthors(), book.getPublishedDate()))
                .replyMarkup(new InlineKeyboardMarkup(List.of(List.of(removeButton, infoButton))))
                .build();
    }

    @Test
    void printBooksTest() {
        // Arrange
        mockUserBookList();
        mockUpdate(username, chatId);

        List<SendMessage> expected = List.of(
                createSendMessage(chatId, book1),
                createSendMessage(chatId, book2)
        );

        Command command = new Command(CommandType.PRINT, "");

        // Act
        List<SendMessage> actual_msg = printBooksHandler.handle(command, update);

        // Assert
        assertFalse(actual_msg.isEmpty());
        assertEquals(expected, actual_msg);
    }

    @Test
    void userNotFoundTest() {
        // Arrange
        String unavailable_user = "new_user";

        mockUpdate(unavailable_user, chatId);
        doThrow(NoSuchElementException.class).when(userBookService).getUserBooks(eq(unavailable_user));

        Command command = new Command(CommandType.PRINT, "");

        // Act
        List<SendMessage> actual_msg = printBooksHandler.handle(command, update);

        // Assert
        assertTrue(actual_msg.isEmpty());
    }
}
