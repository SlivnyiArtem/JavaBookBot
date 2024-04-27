package ru.urfu.bot.services.handlers.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.urfu.bot.services.handlers.CommandHandler;
import ru.urfu.bot.utils.MessageConst;
import ru.urfu.bot.utils.dto.Command;

import java.util.List;

/**
 * Стандартный обработчик. Сообщает, что команда не существует.
 */
@Service
public class DefaultHandlerService implements CommandHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultHandlerService.class);

    @Override
    public List<SendMessage> handle(Command command, String username, String chatId) {
        LOG.warn("unknown command received");
        return List.of(new SendMessage(chatId, MessageConst.UNKNOWN_COMMAND));
    }
}
