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
 * Выводит информацию о книге по isbn из коллекции пользователя.
 */
@Component
public class BookInfoHandler implements CommandHandler {

    private final UserBookService userBookService;

    public BookInfoHandler(UserBookService userBookService) {
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

        String message = MessageConst.BOOK_INFO.formatted(
                book.get().getIsbn13(), book.get().getTitle(), book.get().getDescription(),
                book.get().getAuthors(), book.get().getPublisher(), book.get().getPublishedDate()
        );

        return List.of(new SendMessage(chatId.toString(), message));
    }

    @Override
    public boolean supports(Update update) {
        return true;
    }
}