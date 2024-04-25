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
 * Удаляет книгу по isbn коду из коллекции конретного пользователя.
 */
@Component
public class RemoveBookHandler implements CommandHandler {

    private final UserBookService userBookService;

    public RemoveBookHandler(UserBookService userBookService) {
        this.userBookService = userBookService;
    }

    @Override
    public List<SendMessage> handle(Command command, Update update) {
        String userName = update.getCallbackQuery().getFrom().getUserName();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        Long isbn = Long.parseLong(command.data());

        Optional<Book> book;
        try {
            book = userBookService.findBookByIsbn(userName, isbn);
        } catch (NoSuchElementException e) {
            return List.of();
        }

        if (book.isEmpty()) {
            return List.of(new SendMessage(chatId.toString(), MessageConst.BOOK_NOT_FOUND_IN_FAVORITE.formatted(isbn)));
        }

        userBookService.removeBook(userName, book.get());

        return List.of(new SendMessage(chatId.toString(), MessageConst.REMOVE_BOOK));
    }

    @Override
    public boolean supports(Update update) {
        return true;
    }
}
