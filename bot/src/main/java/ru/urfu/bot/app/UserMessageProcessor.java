package ru.urfu.bot.app;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.domain.handlers.Command;

import java.util.Map;

/**
 * Сервис для определения и выполнения команд
 */
@Service
public class UserMessageProcessor {

    private final Map<String, Command> commands;

    public UserMessageProcessor(@Qualifier(value = "commandMap") Map<String, Command> commandMap) {
        this.commands = commandMap;
    }

    public SendMessage process(Update update) {

        String command = update.getMessage().getText().split(" ")[0];

        Command handler = this.commands.get(command);

        if (handler == null) {
            return new SendMessage(update.getMessage().getChatId().toString(),
                    "Неизвестная команда. Введите /help для получения списка команд");
        }
        if (!handler.supports(update)) {
            return new SendMessage(update.getMessage().getChatId().toString(), "Команда не доступна");
        }
        return handler.handle(update);
    }
}
