package ru.urfu.bot.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.services.handlers.CommandHandler;
import ru.urfu.bot.utils.dto.Command;
import ru.urfu.bot.utils.dto.CommandType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Сервис для определения и выполнения комманд.
 */
@Service
public class MessageProcessor {

    private final Map<CommandType, CommandHandler> commands;

    private final Parser parser;

    private final static Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

    private final BlockingQueue<Update> receiveQueue;

    private final BlockingQueue<SendMessage> sendQueue;

    /**
     * @param commandMap обработчики для команд
     * @param parser парсер команд
     * @param queueProvider предоставляет очереди сообщений
     */
    public MessageProcessor(
            @Qualifier(value = "commandMap") Map<CommandType, CommandHandler> commandMap,
            Parser parser, QueueProvider queueProvider) {
        this.commands = commandMap;
        this.parser = parser;
        this.receiveQueue = queueProvider.getReceiveQueue();
        this.sendQueue = queueProvider.getSendQueue();
    }

    /**
     * Обрабатывает обновления из очереди на обработку. Подготавливает ответ и
     * отправляет в очередь на отправку.
     */
    @Scheduled(fixedRate = 500)
    public void processUpdate() {
        Update update;
        try {
            update = receiveQueue.take();
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
            return;
        }
        LOG.trace("process update #{}", update.getUpdateId());

        if (update.hasMessage() && update.getMessage().hasText()) {
            Command command = parser.parseCommand(update);

            CommandHandler handler = commands.get(command.commandType());

            String username = update.getMessage().getChat().getUserName();
            String chatId = update.getMessage().getChatId().toString();

            addUserIfAbsent(username, chatId);
            executeMessages(handler.handle(command, username, chatId));
        } else if (update.hasCallbackQuery()) {
            Command command = parser.parseCallback(update);

            CommandHandler handler = commands.get(command.commandType());

            String username = update.getCallbackQuery().getFrom().getUserName();
            String chatId = update.getCallbackQuery().getMessage().getChatId().toString();

            addUserIfAbsent(username, chatId);
            executeMessages(handler.handle(command, username, chatId));
        } else {
            LOG.error("don't supported update received");
        }
    }

    private void executeMessages(List<SendMessage> sendMessages) {
        sendMessages.forEach(sendMessage -> {
            try {
                sendQueue.put(sendMessage);
            } catch (InterruptedException e) {
                LOG.error(e.getMessage());
            }
        });
    }

    private void addUserIfAbsent(String username, String chatId) {
        Command startCommand = new Command(CommandType.START, "");
        executeMessages(commands.get(startCommand.commandType()).handle(startCommand, username, chatId));
    }
}
