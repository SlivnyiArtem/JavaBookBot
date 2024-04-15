package ru.urfu.bot.domain.handlers.books;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.domain.entities.Book;
import ru.urfu.bot.domain.handlers.Command;
import ru.urfu.bot.domain.port.UserBookService;

/**
 * Удаляет книгу по isbn коду из бд для конретного пользователя
 */
@Component
public class RemoveBookCommand implements Command {

    private final UserBookService userBookService;

    public RemoveBookCommand(UserBookService userBookService) {
        this.userBookService = userBookService;
    }

    @Override
    public SendMessage handle(Update update) {
        String userName = update.getMessage().getChat().getUserName();
        Long chatId = update.getMessage().getChatId();

        String query = update.getMessage().getText().split(" ")[1];

        Book book = userBookService.findBookByIsbn(Long.parseLong(query));
        userBookService.removeBook(userName, book);

        return new SendMessage(chatId.toString(), "Книга удаленна из избранных");
    }
}
