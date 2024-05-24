package ru.urfu.bot.handler.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.urfu.bot.utils.MessageConst;
import ru.urfu.bot.utils.OrderedHandler;

import java.util.List;

@OrderedHandler(order = 100)
public class DefaultCommandHandler extends CommandUpdateHandler {

    private final Logger logger = LoggerFactory.getLogger(DefaultCommandHandler.class);

    @Override
    protected List<SendMessage> execute(Command command) {
        String chatId = command.chatId().toString();
        String data = String.join(" ", command.args());
        logger.debug("unknown user command received: %s".formatted(data));
        return List.of(new SendMessage(chatId, MessageConst.UNKNOWN_COMMAND));
    }

    @Override
    protected boolean canExecute(String[] args) {
        return true;
    }
}
