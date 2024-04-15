package ru.urfu.bot.app;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.urfu.bot.app.port.BookApiClient;
import ru.urfu.bot.app.port.BookRepository;
import ru.urfu.bot.app.port.ChatRepository;
import ru.urfu.bot.app.port.UserRepository;
import ru.urfu.bot.domain.entities.Book;
import ru.urfu.bot.domain.entities.Chat;
import ru.urfu.bot.domain.entities.User;
import ru.urfu.bot.domain.port.UserBookService;

import java.util.List;

/**
 * Сервис для взаимодействия с dao и api клиентом
 */
@Service
public class UserBookServiceImpl implements UserBookService {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final BookRepository bookRepository;
    private final BookApiClient bookApiClient;

    public UserBookServiceImpl(
            UserRepository userRepository,
            ChatRepository chatRepository,
            BookRepository bookRepository,
            BookApiClient bookApiClient) {

        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.bookApiClient = bookApiClient;
    }

    /**
     * Сохраняет чат и пользователя в бд
     */
    @Transactional(propagation=Propagation.REQUIRED, noRollbackFor=Exception.class)
    public void addChat(String userName, Long chatId) {
        if (chatRepository.findById(chatId).isEmpty()) {
            User user = userRepository.findByUserName(userName).orElse(new User(userName));
            Chat chat = new Chat(chatId);

            user.getChats().add(chat);
            chat.setUser(user);

            userRepository.save(user);
            chatRepository.save(chat);
        }
    }

    /**
     * Возвращает список книг по названию (из api)
     */
    public List<Book> findBooksByTitle(String title) {
        return bookApiClient.findBooksByName(title);
    }

    /**
     * Возращает книгу по isbn коду (из api)
     */
    public Book findBookByIsbn(Long isbn) {
        return bookApiClient.findBookByIsbn(isbn);
    }

    /**
     * Сохраняет книгу в бд для пользователя
     */
    @Transactional(propagation= Propagation.REQUIRED, noRollbackFor=Exception.class)
    public void addBook(String username, Book book) {
        User user = userRepository.findByUserName(username).orElseThrow();
        book.getUsers().add(user);
        user.getBooks().add(book);

        bookRepository.save(book);
        userRepository.save(user);
    }

    /**
     * Удаляет книгу из бд для пользователя
     */
    @Transactional(propagation= Propagation.REQUIRED, noRollbackFor=Exception.class)
    public void removeBook(String username, Book book) {
        User user = userRepository.findByUserName(username).orElseThrow();
        book.getUsers().remove(user);
        user.getBooks().remove(book);

        bookRepository.delete(book);
        userRepository.save(user);
    }

    /**
     * Возвращает список книг пользователя
     */
    @Transactional(propagation=Propagation.REQUIRED, noRollbackFor=Exception.class)
    public List<Book> getUserBooks(String username) {
        return userRepository.findByUserName(username)
                .orElseThrow().getBooks().stream().toList();
    }
}
