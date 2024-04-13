package ru.urfu.bot.app;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.domain.handlers.Command;
import ru.urfu.bot.domain.handlers.books.AddBookCommand;
import ru.urfu.bot.domain.handlers.books.PrintBooksCommand;
import ru.urfu.bot.domain.handlers.books.SearchBookCommand;
import ru.urfu.bot.domain.handlers.bot.HelpBotCommand;
import ru.urfu.bot.domain.handlers.bot.StartBotCommand;

import java.util.Map;

@Service
public class UserMessageProcessor {

    private final Map<String, Command> commands;

    public UserMessageProcessor(@Qualifier(value = "commandMap") Map<String, Command> commandMap) {
        this.commands = commandMap;
    }

    public SendMessage process(Update update) {

        String command = update.getMessage().getText().split(" ")[0];

        Command handler = this.commands.get(command);

        return handler.handle(update);
    }
}
