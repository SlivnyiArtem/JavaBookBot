package ru.urfu.bot.app.domain.services;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.app.domain.handlers.Command;
import ru.urfu.bot.app.domain.handlers.books.AddBookCommand;
import ru.urfu.bot.app.domain.handlers.books.PrintBooksCommand;
import ru.urfu.bot.app.domain.handlers.books.SearchBookCommand;
import ru.urfu.bot.app.domain.handlers.bot.StartBotCommand;

import java.util.Map;

@Service
public class UserMessageProcessor {

    private final Map<String, Command> commands;

    public UserMessageProcessor(
            StartBotCommand startBotCommand, SearchBookCommand searchBookCommand,
            AddBookCommand addBookCommand, PrintBooksCommand printBooksCommand) {

        this.commands = Map.of(
                "/start", startBotCommand,
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
