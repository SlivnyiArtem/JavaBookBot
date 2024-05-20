package ru.urfu.bot.handler.callbacks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.urfu.bot.domain.Book;
import ru.urfu.bot.service.BookTrackingService;
import ru.urfu.bot.utils.MessageConst;

import java.util.List;
import java.util.NoSuchElementException;

import static org.apache.commons.lang3.StringUtils.isNumeric;

/**
 * Выводит информацию о книге по ISBN из базы данных.
 */
@Component
public class BookInfoCallbackHandler extends CallbackUpdateHandler {

    private final BookTrackingService bookTrackingService;

    @Autowired
    public BookInfoCallbackHandler(BookTrackingService bookTrackingService) {
        this.bookTrackingService = bookTrackingService;
    }

    @Override
    protected List<SendMessage> execute(Command command) throws NoSuchElementException {
        Long isbn = Long.parseLong(command.args()[1]);
        String chatId = command.chatId().toString();

        Book book = bookTrackingService.getBook(isbn);

        String message = MessageConst.BOOK_INFO.formatted(
                book.getIsbn(), book.getTitle(), book.getDescription(),
                book.getAuthors(), book.getPublisher(), book.getPublishedDate()
        );
        return List.of(new SendMessage(chatId, message));
    }

    @Override
    protected boolean canExecute(String[] args) {
        return args.length == 2 && args[0].equals("/book_inf") && isNumeric(args[1]);
    }
}