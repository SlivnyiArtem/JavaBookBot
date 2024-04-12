package ru.urfu.bot.app.domain.handlers.books;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.app.domain.handlers.Command;

@Component
public class PrintBooksCommand implements Command {
    @Override
    public SendMessage handle(Update update) {
        return null;
    }
}
