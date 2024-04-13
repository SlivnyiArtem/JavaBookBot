package ru.urfu.bot.domain.handlers.books;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.app.UserBookService;
import ru.urfu.bot.domain.entities.Book;
import ru.urfu.bot.domain.handlers.Command;

@Component
public class AddBookCommand implements Command {

    private final UserBookService userBookService;

    public AddBookCommand(UserBookService userBookService) {
        this.userBookService = userBookService;
    }


    @Override
    public SendMessage handle(Update update) {
        String userName = update.getMessage().getChat().getUserName();
        Long chatId = update.getMessage().getChatId();

        String query = update.getMessage().getText().split(" ")[1];

        Book bookList = userBookService.addBookByIsbn(userName, Long.parseLong(query));

        return new SendMessage(chatId.toString(), "not implemented");
    }
}
