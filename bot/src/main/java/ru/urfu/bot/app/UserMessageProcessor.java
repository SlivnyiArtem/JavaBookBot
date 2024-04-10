package ru.urfu.bot.app;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.app.command.*;

import java.util.Map;

@Service
public class UserMessageProcessor {

    private final Map<String, Command> commands;

    public UserMessageProcessor(
            StartCommand startCommand, SearchBookCommand searchBookCommand,
            AddBookCommand addBookCommand, PrintBooksCommand printBooksCommand) {

        this.commands = Map.of(
                "/start", startCommand,
                "/search_book", searchBookCommand,
                "/add_book", addBookCommand,
                "/my_books", printBooksCommand
        );
    }

    public SendMessage process(Update update) {

        String command = update.getMessage().getText().split(" ")[0];

        Command handler = this.commands.get(command);

        return handler.handle(update);
    }
}
