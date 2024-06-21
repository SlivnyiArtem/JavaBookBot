package edu.telegram.service;

import edu.telegram.domain.User;
import edu.telegram.exception.DataNotFoundException;
import edu.telegram.extern.client.GoogleBooksApiClient;
import edu.telegram.repository.JpaBookRepository;
import edu.telegram.repository.JpaUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.telegram.domain.Book;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Сервис отвечает за операции, связанные с отслеживанием книг пользователями.
 */
@Service
public class BookTrackingService {

    private final GoogleBooksApiClient booksApiClient;

    private final JpaBookRepository bookRepository;

    private final JpaUserRepository userRepository;

    private static final String USER_NOT_FOUND_EXCEPTION_MSG = "user '%s' not found in DB";

    private static final String BOOK_NOT_FOUND_EXCEPTION_MSG = "book '%d' not found in DB and API";

    private static final String BOOK_NOT_TRACKED_EXCEPTION_MSG = "user '%s' doesn't track book '%d'";

    private static final String BOOK_TRACKED_BY_USER_LOG_MSG = "user '{}' track book '{}'";

    private static final String BOOK_DELETED_LOG_MSG = "book '{}' deleted";

    private static final String BOOK_UNTRACKED_BY_USER_LOG_MSG = "user '{}' untrack book '{}'";

    private static final Logger log = LoggerFactory.getLogger(BookTrackingService.class);

    @Autowired
    public BookTrackingService(GoogleBooksApiClient booksApiClient, JpaBookRepository bookRepository,
                               JpaUserRepository userRepository) {
        this.booksApiClient = booksApiClient;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    /**
     * Добавляет книгу в коллекцию пользователя; сохраняет книгу в базу данных, если та ещё не добавлена
     * @param isbn код книги
     * @param username имя пользователя
     * @throws DataNotFoundException если книга не найдена ни в базе данных, ни в API;
     * если пользователь не найден в базе данных
     */
    @Transactional
    public void trackBook(Long isbn, String username) throws DataNotFoundException {
        Book book = bookRepository.findById(isbn)
                .or(() -> booksApiClient.findBookByIsbn(isbn))
                .orElseThrow(() -> new DataNotFoundException(BOOK_NOT_FOUND_EXCEPTION_MSG.formatted(isbn)));
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new DataNotFoundException(USER_NOT_FOUND_EXCEPTION_MSG.formatted(username)));

        user.getBooks().add(book);
        book.getUsers().add(user);

        bookRepository.save(book);
        userRepository.save(user);
        log.info(BOOK_TRACKED_BY_USER_LOG_MSG, username, isbn);
    }

    /**
     * Удаляет книгу из коллекции пользователя; если книгу не отслеживает ни один пользователь,
     * то полностью удаляет её из базы данных.
     * @param isbn код книги
     * @param username имя пользователя
     * @throws DataNotFoundException если книга не найдена в базе данных;
     * если пользователь не найден в базе данных
     */
    @Transactional
    public void untrackBook(Long isbn, String username) throws DataNotFoundException {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new DataNotFoundException(USER_NOT_FOUND_EXCEPTION_MSG.formatted(username)));
        Book book = bookRepository.findByIsbnAndUsers_UserName(isbn, username)
                .orElseThrow(() -> new DataNotFoundException(BOOK_NOT_TRACKED_EXCEPTION_MSG.formatted(username, isbn)));

        book.getUsers().remove(user);
        user.getBooks().remove(book);

        if (book.getUsers().isEmpty()) {
            bookRepository.delete(book);
            log.info(BOOK_DELETED_LOG_MSG, isbn);
        } else {
            bookRepository.save(book);
        }
        userRepository.save(user);
        log.info(BOOK_UNTRACKED_BY_USER_LOG_MSG, username, isbn);
    }

    /**
     * Получает книгу из базы данных либо из внешнего API.
     * @param isbn код книги
     * @return книга
     * @throws DataNotFoundException если книга не найдена ни в базе данных, ни в API
     */
    @Transactional
    public Book getBook(Long isbn) throws DataNotFoundException {
        return bookRepository.findById(isbn)
                .or(() -> booksApiClient.findBookByIsbn(isbn))
                .orElseThrow(() -> new DataNotFoundException(BOOK_NOT_FOUND_EXCEPTION_MSG.formatted(isbn)));
    }

    /**
     * Получает книги из API по названию
     * @param text часть названия книги
     * @return список найденных книг
     */
    public List<Book> getBooksByTitle(String text) {
        return booksApiClient.findBooksByTitle(text);
    }

    /**
     * Получает книги, отслеживаемые пользователем
     * @param username имя пользователя
     * @return список книг
     */
    @Transactional
    public List<Book> getUserBooks(String username) {
        return bookRepository.findAllByUsers_UserName(username);
    }

    /**
     * Получает книги, вышедшие в указанную дату
     * @param publishedDate дата выхода
     * @return список книг
     */
    @Transactional
    public List<Book> getReleasedBook(LocalDate publishedDate) {
        return bookRepository.findAllReleasedBooks(publishedDate);
    }

    /**
     * Получает книги и обновляет книги, информация о которых изменилась с момента
     * последнего обновления
     * @return список книг
     */
    @Transactional
    public List<Book> getUpdatedBook() {
        return bookRepository.findAll().stream()
                .filter(book -> {
                    Optional<Book> newBook = booksApiClient.findBookByIsbn(book.getIsbn());
                    if (newBook.isPresent() && !book.equals(newBook.get())) {
                        bookRepository.save(newBook.get());
                        return true;
                    }
                    return false;
                })
                .toList();
    }
}
