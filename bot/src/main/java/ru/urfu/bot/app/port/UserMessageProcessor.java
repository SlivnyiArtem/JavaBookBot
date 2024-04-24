package ru.urfu.bot.app.port;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.domain.handlers.Command;

import java.util.List;
import java.util.Map;

/**
 * Сервис для определения и выполнения команд
 */
@Service
public class UserMessageProcessor {

    private final Map<String, Command> commands;

    /**
     *
     * @param commandMap - автосвязь с бином commandMap
     */
    public UserMessageProcessor(@Qualifier(value = "commandMap") Map<String, Command> commandMap) {
        this.commands = commandMap;
    }

    /**
     * формирование орбъекта ответного сообщения
     * @param update - входящее сообщение
     * @return   - сформированное ответное сообщение, готовое к отправке
     */
    public List<SendMessage> process(Update update) {

        String command = "";
        if (update.hasMessage() && update.getMessage().hasText()
                && update.getMessage().getText().startsWith("/")) {
            command = update.getMessage().getText().split(" ")[0];
        } else if (update.hasCallbackQuery() && update.getCallbackQuery().getData().startsWith("/")) {
            command = update.getCallbackQuery().getData().split(" ")[0];
        }

        Command handler = this.commands.get(command);

        if (handler == null) {
            return List.of(new SendMessage(update.getMessage().getChatId().toString(),
                    "Неизвестная команда. Введите /help для получения списка команд"));
        }
        if (!handler.supports(update)) {
            return List.of(new SendMessage(update.getMessage().getChatId().toString(), "Команда недоступна"));
        }
        return handler.handle(update);
    }
}
