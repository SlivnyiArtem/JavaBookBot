package ru.urfu.bot.CommandsTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.urfu.bot.controllers.GoogleBooksApiClient;
import ru.urfu.bot.db.entities.Book;
import ru.urfu.bot.db.entities.User;
import ru.urfu.bot.db.repositories.JpaBookRepository;
import ru.urfu.bot.db.repositories.JpaUserRepository;
import ru.urfu.bot.services.handlers.callbacks.AddBookService;
import ru.urfu.bot.utils.dto.Command;
import ru.urfu.bot.utils.dto.CommandType;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddBookServiceTest {

    private final String username = "username";

    private final String chatId = "1";

    private final long isbn = 1;

    private AddBookService addBookService;

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private JpaBookRepository bookRepository;

    @Mock
    private GoogleBooksApiClient booksApiClient;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Book book;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private User user;

    @BeforeEach
    void init() {
        addBookService = new AddBookService(userRepository, bookRepository, booksApiClient);
    }

    @Test
    void addBookTest() {
        // Arrange
        when(bookRepository.findById(eq(isbn))).thenReturn(Optional.of(book));
        when(userRepository.findByUserName(username)).thenReturn(Optional.of(user));

        Command command = new Command(CommandType.ADD, Long.toString(isbn));

        // Act
        addBookService.handle(command, username, chatId);

        // Assert
        verify(user.getBooks()).add(book);
        verify(userRepository).save(user);
        verify(bookRepository).save(book);
    }

    @Test
    void bookNotFoundInDbTest() {
        // Arrange
        when(bookRepository.findById(eq(isbn))).thenReturn(Optional.empty());
        when(booksApiClient.findBookByIsbn(eq(isbn))).thenReturn(Optional.of(book));
        when(userRepository.findByUserName(username)).thenReturn(Optional.of(user));

        Command command = new Command(CommandType.ADD, Long.toString(isbn));

        // Act
        addBookService.handle(command, username, chatId);

        // Assert
        verify(user.getBooks()).add(book);
        verify(userRepository).save(user);
        verify(bookRepository).save(book);
    }

    @Test
    void bookNotFoundTest() {
        // Arrange
        when(bookRepository.findById(eq(isbn))).thenReturn(Optional.empty());
        when(booksApiClient.findBookByIsbn(eq(isbn))).thenReturn(Optional.empty());

        Command command = new Command(CommandType.ADD, Long.toString(isbn));

        // Act
        addBookService.handle(command, username, chatId);

        // Assert
        verify(userRepository, times(0)).save(user);
        verify(bookRepository, times(0)).save(book);
    }

    @Test
    void userNotFoundTest() {
        // Arrange
        when(bookRepository.findById(eq(isbn))).thenReturn(Optional.of(book));
        when(userRepository.findByUserName(username)).thenReturn(Optional.empty());

        Command command = new Command(CommandType.ADD, Long.toString(isbn));

        // Act
        addBookService.handle(command, username, chatId);

        // Assert
        verify(userRepository, times(0)).save(user);
        verify(bookRepository, times(0)).save(book);
    }
}
