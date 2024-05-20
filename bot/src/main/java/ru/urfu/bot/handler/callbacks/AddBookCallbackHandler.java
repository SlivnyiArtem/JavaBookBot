package ru.urfu.bot.handler.callbacks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.urfu.bot.service.BookTrackingService;
import ru.urfu.bot.utils.MessageConst;

import java.util.List;
import java.util.NoSuchElementException;

import static org.apache.commons.lang3.StringUtils.isNumeric;

/**
 * Добавляет книгу в коллекцию пользователя; Формирует ответ
 */
@Component
public class AddBookCallbackHandler extends CallbackUpdateHandler {

    private final BookTrackingService bookTrackingService;

    @Autowired
    public AddBookCallbackHandler(BookTrackingService bookTrackingService) {
        this.bookTrackingService = bookTrackingService;
    }

    @Override
    protected List<SendMessage> execute(Command command) throws NoSuchElementException {
        Long isbn = Long.parseLong(command.args()[1]);
        String username = command.username();
        String chatId = command.chatId().toString();

        bookTrackingService.trackBook(isbn, username);

        return List.of(new SendMessage(chatId, MessageConst.ADD_BOOK));
    }

    @Override
    protected boolean canExecute(String[] args) {
        return args.length == 2 && args[0].equals("/add_book") && isNumeric(args[1]);
    }
}
