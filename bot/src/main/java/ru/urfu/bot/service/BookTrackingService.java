package ru.urfu.bot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.urfu.bot.client.GoogleBooksApiClient;
import ru.urfu.bot.domain.Book;
import ru.urfu.bot.domain.User;
import ru.urfu.bot.repository.JpaBookRepository;
import ru.urfu.bot.repository.JpaUserRepository;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Сервис отвечает за операции, связанные с отслеживанием книг пользователями.
 */
@Service
public class BookTrackingService {

    @Autowired
    public BookTrackingService(GoogleBooksApiClient booksApiClient, JpaBookRepository bookRepository,
                               JpaUserRepository userRepository) {
        this.booksApiClient = booksApiClient;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    private final GoogleBooksApiClient booksApiClient;

    private final JpaBookRepository bookRepository;

    private final JpaUserRepository userRepository;

    private static final String USER_NOT_FOUND_MSG = "user %s not found in DB";

    private static final String BOOK_NOT_FOUND_MSG = "book %d not found in DB and API";

    /**
     * Добавляет книгу в коллекцию пользователя; сохраняет книгу в базу данных, если та ещё не добавлена
     * @param isbn код книги
     * @param username имя пользователя
     * @throws NoSuchElementException если книга не найдена ни в базе данных, ни в API;
     * если пользователь не найден в базе данных
     */
    @Transactional
    public void trackBook(Long isbn, String username) throws NoSuchElementException {

        Book book = bookRepository.findById(isbn)
                .or(() -> booksApiClient.findBookByIsbn(isbn))
                .orElseThrow(() -> new NoSuchElementException(BOOK_NOT_FOUND_MSG.formatted(isbn)));
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND_MSG.formatted(username)));

        user.getBooks().add(book);
        book.getUsers().add(user);

        bookRepository.save(book);
        userRepository.save(user);
    }

    /**
     * Удаляет книгу из коллекции пользователя; если книгу не отслеживает ни один пользователь,
     * то полностью удаляет её из базы данных.
     * @param isbn код книги
     * @param username имя пользователя
     * @throws NoSuchElementException если книга не найдена в базе данных;
     * если пользователь не найден в базе данных
     */
    @Transactional
    public void untrackBook(Long isbn, String username) throws NoSuchElementException {

        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND_MSG.formatted(username)));
        Book book = bookRepository.findByIsbnAndUsers_UserName(isbn, username)
                .orElseThrow(() -> new NoSuchElementException("book %d not found in DB or user %s doesn't track book"
                        .formatted(isbn, username)));

        book.getUsers().remove(user);
        user.getBooks().remove(book);

        if (book.getUsers().isEmpty()) {
            bookRepository.delete(book);
        } else {
            bookRepository.save(book);
        }
        userRepository.save(user);
    }

    /**
     * Получает книгу из базы данных либо из внешнего API.
     * @param isbn код книги
     * @return книга
     * @throws NoSuchElementException если книга не найдена ни в базе данных, ни в API
     */
    @Transactional
    public Book getBook(Long isbn) throws NoSuchElementException {
        return bookRepository.findById(isbn)
                .or(() -> booksApiClient.findBookByIsbn(isbn))
                .orElseThrow(() -> new NoSuchElementException(BOOK_NOT_FOUND_MSG.formatted(isbn)));
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
}
