package ru.urfu.bot.services.handlers.callbacks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.urfu.bot.controllers.GoogleBooksApiClient;
import ru.urfu.bot.db.entities.Book;
import ru.urfu.bot.db.entities.User;
import ru.urfu.bot.db.exceptions.BookNotFoundException;
import ru.urfu.bot.db.exceptions.UserNotFoundException;
import ru.urfu.bot.db.repositories.JpaBookRepository;
import ru.urfu.bot.db.repositories.JpaUserRepository;
import ru.urfu.bot.services.handlers.CommandHandler;
import ru.urfu.bot.utils.MessageConst;
import ru.urfu.bot.utils.dto.Command;

import java.util.List;

/**
 * Сохраняет книгу по isbn коду в коллекцию конретного пользователя.
 */
@Service
public class AddBookService implements CommandHandler {

    private final JpaUserRepository userRepository;

    private final JpaBookRepository bookRepository;

    private final GoogleBooksApiClient bookApiClient;

    private static final Logger LOG = LoggerFactory.getLogger(AddBookService.class);

    public AddBookService(
            JpaUserRepository userRepository, JpaBookRepository bookRepository,
            GoogleBooksApiClient bookApiClient) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.bookApiClient = bookApiClient;
    }

    @Override
    @Transactional
    public List<SendMessage> handle(Command command, String username, String chatId) {
        Long isbn = Long.parseLong(command.data());

        try {
            Book book = bookRepository.findById(isbn)
                    .or(() -> bookApiClient.findBookByIsbn(isbn))
                    .orElseThrow(BookNotFoundException::new);
            User user = userRepository.findByUserName(username).orElseThrow(UserNotFoundException::new);

            user.addBook(book);
            book.addUser(user);

            bookRepository.save(book);
            return List.of(new SendMessage(chatId, MessageConst.ADD_BOOK));
        } catch (BookNotFoundException e) {
            LOG.error("book isbn not found in db and source", e);
            return List.of(new SendMessage(chatId, MessageConst.INTERNAL_ERROR));
        } catch (UserNotFoundException e) {
            LOG.error("user not found");
            return List.of(new SendMessage(chatId, MessageConst.INTERNAL_ERROR));
        }
    }
}
