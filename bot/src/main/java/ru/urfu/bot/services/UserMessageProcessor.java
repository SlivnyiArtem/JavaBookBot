package ru.urfu.bot.services;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.utils.MessageConst;
import ru.urfu.bot.utils.dto.Command;
import ru.urfu.bot.utils.dto.CommandType;
import ru.urfu.bot.handlers.CommandHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Сервис для определения и выполнения команд.
 */
@Service
public class UserMessageProcessor {

    private final Map<CommandType, CommandHandler> commands;

    private final Map<CommandType, CommandHandler> callbacks;

    private final Parser parser;

    /**
     * @param commandMap обработчики для команд
     * @param callbacks обработчики коллэков
     * @param parser парсер команд
     */
    public UserMessageProcessor(
            @Qualifier(value = "commandMap") Map<CommandType, CommandHandler> commandMap,
            @Qualifier(value = "callbackMap") Map<CommandType, CommandHandler> callbacks,
            Parser parser) {
        this.commands = commandMap;
        this.callbacks = callbacks;
        this.parser = parser;
    }

    /**
     * Парсит обновление и формирует ответ для пользователя.
     * @param update входящее сообщение
     * @return список сформированных ответных сообщений
     */
    public List<SendMessage> process(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()
                && update.getMessage().getText().startsWith("/")) {
            Command command = parser.parseCommand(update);

            CommandHandler handler = commands.get(command.commandType());

            List<SendMessage> response = new ArrayList<>();

            if (!handler.supports(update)) {
                if (commands.containsKey(CommandType.START)) {
                    response.addAll(commands.get(CommandType.START).handle(
                            new Command(CommandType.START, ""), update));
                } else {
                    // TODO logger
                    return List.of();
                }
            }
            response.addAll(handler.handle(command, update));
            return response;
        } else if (update.hasCallbackQuery()) {
            Command command = parser.parseCallback(update);

            CommandHandler handler = callbacks.get(command.commandType());

            return handler.handle(command, update);
        } else {
            return List.of(new SendMessage(update.getMessage().getChatId().toString(), MessageConst.UNKNOWN_COMMAND));
        }
    }
}
