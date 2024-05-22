package ru.urfu.bot.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.urfu.bot.client.GoogleBooksApiClient;
import ru.urfu.bot.domain.Book;
import ru.urfu.bot.domain.User;
import ru.urfu.bot.repository.JpaBookRepository;
import ru.urfu.bot.repository.JpaUserRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тесты на клас {@link BookTrackingService}
 */
@ExtendWith(MockitoExtension.class)
public class BookTrackingServiceTest {

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private JpaBookRepository bookRepository;

    @Mock
    private GoogleBooksApiClient booksApiClient;

    @InjectMocks
    private BookTrackingService bookTrackingService;

    private final Long isbn = 1L;

    private final String username = "test";

    /**
     * {@link BookTrackingService#trackBook(Long, String)} сохраняет книгу и пользователя, если книга найдена в
     * базе данных.
     */
    @Test
    public void trackBookInDBTest() {
        User user = new User(username);
        Book book = new Book();
        book.setIsbn(isbn);

        when(bookRepository.findById(eq(isbn))).thenReturn(Optional.of(book));
        when(userRepository.findByUserName(username)).thenReturn(Optional.of(user));

        bookTrackingService.trackBook(isbn, username);
        verify(userRepository).save(user);
        verify(bookRepository).save(book);
    }

    /**
     * {@link BookTrackingService#trackBook(Long, String)} сохраняет книгу и пользователя, если книга найдена в
     * API.
     */
    @Test
    public void trackBookInAPITest() {
        User user = new User(username);
        Book book = new Book();
        book.setIsbn(isbn);

        when(bookRepository.findById(eq(isbn))).thenReturn(Optional.empty());
        when(booksApiClient.findBookByIsbn(eq(isbn))).thenReturn(Optional.of(book));
        when(userRepository.findByUserName(username)).thenReturn(Optional.of(user));

        bookTrackingService.trackBook(isbn, username);
        verify(userRepository).save(user);
        verify(bookRepository).save(book);
    }

    /**
     * {@link BookTrackingService#trackBook(Long, String)} бросает исключение, если книга не найдена.
     */
    @Test
    public void trackBookNotFoundTest() {
        when(bookRepository.findById(eq(isbn))).thenReturn(Optional.empty());
        when(booksApiClient.findBookByIsbn(eq(isbn))).thenReturn(Optional.empty());

        Exception e = assertThrows(NoSuchElementException.class, () -> bookTrackingService.trackBook(isbn, username));
        assertEquals("book %d not found in DB and API".formatted(isbn), e.getMessage());
    }

    /**
     * {@link BookTrackingService#trackBook(Long, String)} бросает исключение, если пользователь не найден.
     */
    @Test
    public void trackBookUserNotFoundTest() {
        Book book = new Book();
        book.setIsbn(isbn);

        when(bookRepository.findById(eq(isbn))).thenReturn(Optional.of(book));
        when(userRepository.findByUserName(username)).thenReturn(Optional.empty());

        Exception e = assertThrows(NoSuchElementException.class, () -> bookTrackingService.trackBook(isbn, username));
        assertEquals("user %s not found in DB".formatted(username), e.getMessage());
    }

    /**
     * Тест {@link BookTrackingService#untrackBook(Long, String)} на правильных данных
     * <ol>
     *     <li>Если книгу больше не отслеживают, то она удаляется</li>
     *     <li>Если у книги остались пользователи, то удаляется только связь</li>
     * </ol>
     */
    @Test
    public void untrackBookTest() {
        User user1 = new User(username);
        Book book1 = new Book();
        book1.getUsers().add(user1);
        book1.setIsbn(isbn);

        when(bookRepository.findByIsbnAndUsers_UserName(eq(isbn), eq(username))).thenReturn(Optional.of(book1));
        when(userRepository.findByUserName(username)).thenReturn(Optional.of(user1));

        bookTrackingService.untrackBook(isbn, username);
        verify(userRepository).save(user1);
        verify(bookRepository).delete(book1);

        Long isbn1 = 2L;
        User user2 = new User();
        Book book2 = new Book();
        book2.setIsbn(isbn1);
        book2.getUsers().add(user1);
        book2.getUsers().add(user2);

        when(bookRepository.findByIsbnAndUsers_UserName(eq(isbn1), eq(username))).thenReturn(Optional.of(book2));
        bookTrackingService.untrackBook(isbn1, username);
        verify(bookRepository).save(book2);
    }

    /**
     * Тест {@link BookTrackingService#untrackBook(Long, String)}. Если книга отсутствует в коллекции пользователя, то
     * бросается исключение.
     */
    @Test
    public void untrackBookNotFoundTest() {
        User user = new User(username);

        when(bookRepository.findByIsbnAndUsers_UserName(eq(isbn), eq(username))).thenReturn(Optional.empty());
        when(userRepository.findByUserName(username)).thenReturn(Optional.of(user));

        Exception e = assertThrows(NoSuchElementException.class, () -> bookTrackingService.untrackBook(isbn, username));
        assertEquals("book %d not found in DB or user %s doesn't track book".formatted(isbn, username), e.getMessage());
    }

    /**
     * Тест {@link BookTrackingService#untrackBook(Long, String)}. Если книга отсутствует в коллекции пользователя, то
     * бросается исключение.
     */
    @Test
    public void untrackBookUserNotFoundTest() {
        when(userRepository.findByUserName(username)).thenReturn(Optional.empty());

        Exception e = assertThrows(NoSuchElementException.class, () -> bookTrackingService.untrackBook(isbn, username));
        assertEquals("user %s not found in DB".formatted(username), e.getMessage());
    }
}
