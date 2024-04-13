package ru.urfu.bot.app;

import org.springframework.stereotype.Service;
import ru.urfu.bot.domain.BookApiClient;
import ru.urfu.bot.domain.entities.Book;
import ru.urfu.bot.domain.entities.Chat;
import ru.urfu.bot.domain.entities.User;
import ru.urfu.bot.infrastructure.db.repositories.JpaBookRepository;
import ru.urfu.bot.infrastructure.db.repositories.JpaChatRepository;
import ru.urfu.bot.infrastructure.db.repositories.JpaUserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserBookService {

    private final JpaUserRepository userRepository;
    private final JpaChatRepository chatRepository;
    private final JpaBookRepository bookRepository;

    private final BookApiClient bookApiClient;

    public UserBookService(
            JpaUserRepository userRepository,
            JpaChatRepository chatRepository,
            JpaBookRepository bookRepository,
            BookApiClient bookApiClient) {

        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.bookApiClient = bookApiClient;
    }

    public void registerChat(String userName, Long chatId) {
        if (chatRepository.findById(chatId).isEmpty()) {
            User user = userRepository.findByUserName(userName).orElse(new User(userName));
            Chat chat = new Chat(chatId);

            user.getChats().add(chat);
            chat.setUser(user);

            userRepository.save(user);
            chatRepository.save(chat);
        }
    }

    public List<Book> getBooksByTitle(String title) {
        return bookApiClient.findBooksByName(title);
    }

    public Book addBookByIsbn(String username, Long isbn) {
        Book book = bookApiClient.findBookByIsbn(isbn);

        User user = userRepository.findByUserName(username).orElseThrow();
        book.getUsers().add(user);
        user.getBooks().add(book);

        userRepository.save(user);
        bookRepository.save(book);
        return book;
    }

    public List<Book> getUserBooks(String username) {
        return userRepository.findByUserName(username)
                .orElseThrow().getBooks().stream().toList();
    }
}
