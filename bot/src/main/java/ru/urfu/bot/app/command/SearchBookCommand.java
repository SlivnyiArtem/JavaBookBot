package ru.urfu.bot.app.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;
import ru.urfu.bot.app.BookApiClient;

@Component
public class SearchBookCommand implements Command {

    private final BookApiClient bookApiClient;

    public SearchBookCommand(BookApiClient bookApiClient) {
        this.bookApiClient = bookApiClient;
    }

    @Override
    public SendMessage handle(Update update) {
        String userName = update.getMessage().getChat().getUserName();
        Long chatId = update.getMessage().getChatId();

        String query = update.getMessage().getText().split(" ")[1];

        Mono<?> bookList = bookApiClient.findBooksByName(query);

        return new SendMessage(chatId.toString(), "not implemented");
    }
}
