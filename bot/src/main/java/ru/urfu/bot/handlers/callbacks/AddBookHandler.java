package ru.urfu.bot.handlers.callbacks;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.db.entities.Book;
import ru.urfu.bot.handlers.CommandHandler;
import ru.urfu.bot.services.UserBookService;
import ru.urfu.bot.utils.MessageConst;
import ru.urfu.bot.utils.dto.Command;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Сохраняет книгу по isbn коду в коллекцию конретного пользователя.
 */
@Component
public class AddBookHandler implements CommandHandler {

    private final UserBookService userBookService;

    public AddBookHandler(UserBookService userBookService) {
        this.userBookService = userBookService;
    }

    @Override
    public List<SendMessage> handle(Command command, Update update) {
        String userName = update.getCallbackQuery().getFrom().getUserName();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        Long isbn = Long.parseLong(command.data());

        Optional<Book> book = userBookService.findBookByIsbn(isbn);

        if (book.isEmpty()) {
            return List.of(new SendMessage(chatId.toString(), MessageConst.BOOK_NOT_FOUND_IN_SOURCE.formatted(isbn)));
        }

        try {
            userBookService.addBook(userName, book.get());
        } catch (NoSuchElementException e) {
            return List.of();
        }

        return List.of(new SendMessage(chatId.toString(), MessageConst.ADD_BOOK));
    }

    @Override
    public boolean supports(Update update) {
        return true;
    }
}
