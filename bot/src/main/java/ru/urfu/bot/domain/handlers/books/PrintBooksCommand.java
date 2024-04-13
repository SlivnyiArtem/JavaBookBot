package ru.urfu.bot.domain.handlers.books;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.app.UserBookService;
import ru.urfu.bot.domain.entities.Book;
import ru.urfu.bot.domain.handlers.Command;

import java.util.List;

@Component
public class PrintBooksCommand implements Command {

    private final UserBookService userBookService;

    public PrintBooksCommand(UserBookService userBookService) {
        this.userBookService = userBookService;
    }


    @Override
    public SendMessage handle(Update update) {
        String userName = update.getMessage().getChat().getUserName();
        Long chatId = update.getMessage().getChatId();

        List<Book> books = userBookService.getUserBooks(userName);

        return new SendMessage(chatId.toString(), "not implemented");
    }
}
