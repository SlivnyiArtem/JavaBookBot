package ru.urfu.bot.domain.handlers.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.domain.handlers.Command;

@Component
public class HelpBotCommand implements Command {
    @Override
    public SendMessage handle(Update update) {
        return new SendMessage(update.getMessage().getChatId().toString(), "Bot started");
    }
}
