package ru.urfu.bot.CommandsTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.urfu.bot.controllers.GoogleBooksApiClient;
import ru.urfu.bot.db.entities.Book;
import ru.urfu.bot.services.handlers.commands.SearchBookService;
import ru.urfu.bot.utils.MessageConst;
import ru.urfu.bot.utils.dto.Command;
import ru.urfu.bot.utils.dto.CommandType;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SearchBookServiceTest {

    private final String username = "username";

    private final String chatId = "1";

    private final long isbn = 1;

    private SearchBookService searchBookService;

    @Mock
    private GoogleBooksApiClient booksApiClient;

    @Mock
    private Book book;

    @BeforeEach
    void init() {
        searchBookService = new SearchBookService(booksApiClient);
    }

    @Test
    void sendCorrectInfoTest() {
        // Arrange
        String title = "title";
        String author = "author";
        LocalDate publishedDate = LocalDate.of(2000, 1, 1);

        when(book.getIsbn13()).thenReturn(isbn);
        when(book.getTitle()).thenReturn(title);
        when(book.getAuthors()).thenReturn(author);
        when(book.getPublishedDate()).thenReturn(publishedDate);

        when(booksApiClient.findBooksByTitle(eq(title))).thenReturn(List.of(book));

        Command command = new Command(CommandType.INFO, title);
        SendMessage expected = SendMessage.builder()
                .chatId(chatId)
                .text(MessageConst.BOOK_INFO_SHORT.formatted(isbn, title, author, publishedDate))
                .build();

        // Act
        List<SendMessage> actual_msg = searchBookService.handle(command, username, chatId);

        // Assert
        verify(booksApiClient).findBooksByTitle(title);
        assertFalse(actual_msg.isEmpty());
        assertEquals(expected.getText(), actual_msg.getFirst().getText());
    }
}
