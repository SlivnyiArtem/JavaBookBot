package edu.telegram.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.urfu.bot.client.GoogleBooksApiClient;
import edu.telegram.domain.Book;
import edu.telegram.domain.User;
import edu.telegram.repository.JpaBookRepository;
import edu.telegram.repository.JpaUserRepository;

import java.time.LocalDate;
import java.util.List;
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

    /**
     * Выбираются только книги, у которых дата публикации равна заданной
     */
    @Test
    public void checkReleaseTest() {
        Book book1 = new Book();
        book1.setIsbn(1L);
        book1.setTitle("book1");
        book1.setPublishedDate(LocalDate.of(2000, 1, 2));
        Book book2 = new Book();
        book2.setIsbn(2L);
        book2.setTitle("book2");
        book2.setPublishedDate(LocalDate.of(2000, 1, 3));
        Book book3 = new Book();
        book3.setIsbn(3L);
        book3.setTitle("book3");
        book3.setPublishedDate(LocalDate.of(2000, 1, 1));
        Book book4 = new Book();
        book4.setIsbn(4L);
        book4.setTitle("book4");

        when(bookRepository.findAll()).thenReturn(List.of(book1, book2, book3, book4));

        LocalDate now = LocalDate.of(2000, 1, 2);

        List<Book> actual = bookTrackingService.getReleasedBook(now);
        assertEquals(List.of(book1), actual);
    }

    /**
     * <ul>
     *     <li>если у книги изменилось поле, то обновляем и возвращаем книгу</li>
     *     <li>если у книги поля не изменились или книга не возвращается из API, то не возвращаем</li>
     * </ul>
     */
    @Test
    public void checkUpdateInfoTest() {
        Book book1 = new Book();
        book1.setIsbn(1L);
        book1.setTitle("book1");
        Book book2 = new Book();
        book2.setIsbn(2L);
        book2.setTitle("book2");
        Book book3 = new Book();
        book3.setIsbn(3L);
        book3.setTitle("book3");
        Book book4 = new Book();
        book4.setIsbn(4L);
        book4.setTitle("book4");

        when(bookRepository.findAll()).thenReturn(List.of(book1, book2, book3, book4));

        Book book11 = new Book();
        book11.setIsbn(1L);
        book11.setTitle("new title 1");
        Book book12 = new Book();
        book12.setIsbn(2L);
        when(booksApiClient.findBookByIsbn(1L)).thenReturn(Optional.of(book11));
        when(booksApiClient.findBookByIsbn(2L)).thenReturn(Optional.of(book12));
        when(booksApiClient.findBookByIsbn(3L)).thenReturn(Optional.of(book3));
        when(booksApiClient.findBookByIsbn(4L)).thenReturn(Optional.empty());

        List<Book> actual = bookTrackingService.getUpdatedBook();
        assertEquals(List.of(book1, book2), actual);
    }
}
