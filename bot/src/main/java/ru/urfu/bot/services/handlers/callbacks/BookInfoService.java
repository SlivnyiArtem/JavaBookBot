package ru.urfu.bot.services.handlers.callbacks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.urfu.bot.db.entities.Book;
import ru.urfu.bot.db.exceptions.BookNotFoundException;
import ru.urfu.bot.db.repositories.JpaBookRepository;
import ru.urfu.bot.services.handlers.CommandHandler;
import ru.urfu.bot.utils.MessageConst;
import ru.urfu.bot.utils.dto.Command;

import java.util.List;

/**
 * Выводит информацию о книге по isbn из коллекции пользователя.
 */
@Service
public class BookInfoService implements CommandHandler {

    private final JpaBookRepository bookRepository;
    private static final Logger LOG = LoggerFactory.getLogger(BookInfoService.class);

    public BookInfoService(JpaBookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional
    public List<SendMessage> handle(Command command, String username, String chatId) {
        Long isbn = Long.parseLong(command.data());

        try {
            Book book = bookRepository.findByIsbn13AndUsers_UserName(isbn, username)
                    .orElseThrow(BookNotFoundException::new);

            String message = MessageConst.BOOK_INFO.formatted(
                    book.getIsbn13(), book.getTitle(), book.getDescription(),
                    book.getAuthors(), book.getPublisher(), book.getPublishedDate()
            );
            return List.of(new SendMessage(chatId, message));
        } catch (BookNotFoundException e) {
            LOG.warn("book isbn not found in db", e);
            return List.of(new SendMessage(chatId, MessageConst.BOOK_NOT_FOUND));
        }
    }
}