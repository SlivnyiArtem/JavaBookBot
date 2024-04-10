package ru.urfu.bot.app.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class SearchBookCommand implements Command {
    @Override
    public SendMessage handle(Update update) {
        return null;
    }
}
