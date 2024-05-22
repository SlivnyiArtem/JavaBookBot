package ru.urfu.bot.handler.callbacks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.urfu.bot.utils.MessageConst;
import ru.urfu.bot.utils.OrderedHandler;

import java.util.List;

@OrderedHandler(order = 100)
public class DefaultCallbackHandler extends CallbackUpdateHandler {

    private final Logger logger = LoggerFactory.getLogger(DefaultCallbackHandler.class);

    @Override
    protected List<SendMessage> execute(Command command) {
        String chatId = command.chatId().toString();
        String data = String.join(" ", command.args());
        logger.warn("unknown callback received: %s".formatted(data));
        return List.of(new SendMessage(chatId, MessageConst.INTERNAL_SERVER_ERROR));
    }

    @Override
    protected boolean canExecute(String[] args) {
        return true;
    }
}
