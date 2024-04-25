package ru.urfu.bot.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.urfu.bot.controllers.GoogleBooksApiClient;
import ru.urfu.bot.db.entities.Book;
import ru.urfu.bot.db.entities.Chat;
import ru.urfu.bot.db.entities.User;
import ru.urfu.bot.db.repositories.JpaBookRepository;
import ru.urfu.bot.db.repositories.JpaChatRepository;
import ru.urfu.bot.db.repositories.JpaUserRepository;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Сервис для взаимодействия с dao и api клиентом.
 */
@Service
public class UserBookService {

    private final JpaUserRepository userRepository;
    private final JpaChatRepository chatRepository;
    private final JpaBookRepository bookRepository;
    private final GoogleBooksApiClient bookApiClient;

    public UserBookService(
            JpaUserRepository userRepository,
            JpaChatRepository chatRepository,
            JpaBookRepository bookRepository,
            GoogleBooksApiClient bookApiClient) {

        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.bookApiClient = bookApiClient;
    }

    /**
     * Сохраняет чат и пользователя в бд.
     * @param username username телеграм пользователя
     * @param chatId id телеграм чата
     */
    @Transactional
    public void addChat(String username, Long chatId) {
        if (chatRepository.findById(chatId).isEmpty()) {
            User user = userRepository.findByUserName(username).orElse(new User(username));
            Chat chat = new Chat(chatId);

            user.getChats().add(chat);
            chat.setUser(user);

            userRepository.save(user);
            chatRepository.save(chat);
        }
    }

    /**
     * Проверяет, зарегистрирован ли чат.
     * @param id id телеграм чата
     * @return true если чат содержится, иначе false
     */
    @Transactional
    public boolean containsChat(Long id) {
        return chatRepository.findById(id).isPresent();
    }

    /**
     * Возвращает список книг по названию (из api).
     * @param title название книги
     * @return список подходящих книг
     */
    public List<Book> findBooksByTitle(String title) {
        return bookApiClient.findBooksByName(title);
    }

    /**
     * Возращает книгу по isbn коду (из api).
     * @param isbn isbn код книги
     * @return найденная книга
     */
    public Optional<Book> findBookByIsbn(Long isbn) {
        return bookApiClient.findBookByIsbn(isbn);
    }

    /**
     * Возвращает книгу по isbn коду (из коллекции пользователя).
     * @param username username телеграм пользователя
     * @param isbn isbn код книги
     * @return найденная книга
     */
    @Transactional
    public Optional<Book> findBookByIsbn(String username, Long isbn) {
        User user = userRepository.findByUserName(username).orElseThrow();
        return user.getBooks().stream()
                .filter(book -> Objects.equals(book.getIsbn13(), isbn))
                .findFirst();
    }

    /**
     * Сохраняет книгу в бд для пользователя.
     * @param username username телеграм пользователя
     * @param book книга
     */
    @Transactional
    public void addBook(String username, Book book) {
        User user = userRepository.findByUserName(username).orElseThrow();
        book.getUsers().add(user);
        user.getBooks().add(book);

        bookRepository.save(book);
        userRepository.save(user);
    }

    /**
     * Удаляет книгу из бд для пользователя. Удаляется только связь с конкретным пользователем.
     * @param username username телеграм пользователя
     * @param book книга
     */
    @Transactional
    public void removeBook(String username, Book book) {
        User user = userRepository.findByUserName(username).orElseThrow();
        book.getUsers().remove(user);
        user.getBooks().remove(book);

        bookRepository.save(book);
        userRepository.save(user);
    }

    /**
     * Возвращает список книг пользователя.
     * @param username username телеграм пользователя
     * @return список книг пользователя
     */
    @Transactional
    public List<Book> getUserBooks(String username) {
        return userRepository.findByUserName(username)
                .orElseThrow().getBooks().stream().toList();
    }
}
