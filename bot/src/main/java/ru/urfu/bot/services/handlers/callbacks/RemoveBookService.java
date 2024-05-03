package ru.urfu.bot.services.handlers.callbacks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
 * Удаляет книгу по isbn коду из коллекции конретного пользователя.
 */
@Service
public class RemoveBookService implements CommandHandler {

    private final JpaUserRepository userRepository;

    private final JpaBookRepository bookRepository;

    private static final Logger LOG = LoggerFactory.getLogger(RemoveBookService.class);

    public RemoveBookService(JpaUserRepository userRepository, JpaBookRepository bookRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional
    public List<SendMessage> handle(Command command, String username, String chatId) {
        Long isbn = Long.parseLong(command.data());

        try {
            User user = userRepository.findByUserName(username).orElseThrow(UserNotFoundException::new);
            Book book = bookRepository.findByIsbn13AndUsers_UserName(isbn, username)
                    .orElseThrow(BookNotFoundException::new);

            book.getUsers().remove(user);
            user.getBooks().remove(book);

            if (book.getUsers().isEmpty()) {
                bookRepository.delete(book);
            } else {
                bookRepository.save(book);
            }
            userRepository.save(user);

            return List.of(new SendMessage(chatId, MessageConst.REMOVE_BOOK));
        } catch (BookNotFoundException e) {
            LOG.warn("book isbn not found in db", e);
            return List.of(new SendMessage(chatId, MessageConst.BOOK_NOT_FOUND));
        } catch (UserNotFoundException e) {
            LOG.error("user not found");
            return List.of(new SendMessage(chatId, MessageConst.INTERNAL_ERROR));
        }
    }
}
