package ru.urfu.bot.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.services.handlers.CommandHandler;
import ru.urfu.bot.utils.dto.Command;
import ru.urfu.bot.utils.dto.CommandType;

import java.util.List;
import java.util.Map;

/**
 * Сервис для определения и выполнения команд.
 */
@Service
public class UserMessageProcessor {

    private final Map<CommandType, CommandHandler> commands;

    private final Parser parser;

    private final static Logger LOG = LoggerFactory.getLogger(UserMessageProcessor.class);

    /**
     * @param commandMap обработчики для команд
     * @param parser парсер команд
     */
    public UserMessageProcessor(
            @Qualifier(value = "commandMap") Map<CommandType, CommandHandler> commandMap,
            Parser parser) {
        this.commands = commandMap;
        this.parser = parser;
    }

    // TODO fix dependency; fix search command bugs
    /**
     * Парсит обновление и формирует ответ для пользователя.
     * @param update входящее сообщение
     * @return список сформированных ответных сообщений
     */
    public List<SendMessage> process(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            Command command = parser.parseCommand(update);

            CommandHandler handler = commands.get(command.commandType());

            String username = update.getMessage().getChat().getUserName();
            String chatId = update.getMessage().getChatId().toString();

            addUserIfAbsent(username, chatId);

            return handler.handle(command, username, chatId);
        } else if (update.hasCallbackQuery()) {
            Command command = parser.parseCallback(update);

            CommandHandler handler = commands.get(command.commandType());

            String username = update.getCallbackQuery().getFrom().getUserName();
            String chatId = update.getCallbackQuery().getMessage().getChatId().toString();

            addUserIfAbsent(username, chatId);

            return handler.handle(command, username, chatId);
        } else {
            LOG.error("don't supported update received");
            return List.of();
        }
    }

    private void addUserIfAbsent(String username, String chatId) {
        Command startCommand = new Command(CommandType.START, "");
        commands.get(startCommand.commandType()).handle(startCommand, username, chatId);
    }
}
