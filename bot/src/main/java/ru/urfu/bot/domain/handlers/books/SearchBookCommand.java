package ru.urfu.bot.domain.handlers.books;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;
import ru.urfu.bot.app.UserBookService;
import ru.urfu.bot.domain.BookApiClient;
import ru.urfu.bot.domain.entities.Book;
import ru.urfu.bot.domain.handlers.Command;

import java.util.List;

@Component
public class SearchBookCommand implements Command {

    private final UserBookService userBookService;

    public SearchBookCommand(UserBookService userBookService) {
        this.userBookService = userBookService;
    }

    @Override
    public SendMessage handle(Update update) {
        Long chatId = update.getMessage().getChatId();

        String query = update.getMessage().getText().split(" ")[1];

        List<Book> bookList = userBookService.getBooksByTitle(query);

        return new SendMessage(chatId.toString(), "not implemented");
    }
}
