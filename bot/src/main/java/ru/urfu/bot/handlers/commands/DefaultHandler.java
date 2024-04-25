package ru.urfu.bot.handlers.commands;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.utils.MessageConst;
import ru.urfu.bot.utils.dto.Command;
import ru.urfu.bot.handlers.CommandHandler;

import java.util.List;

/**
 * Стандартный обработчик. Сообщает, что команда не существует.
 */
public class DefaultHandler implements CommandHandler {

    @Override
    public List<SendMessage> handle(Command command, Update update) {
        String chatId = command.data();
        return List.of(new SendMessage(chatId, MessageConst.UNKNOWN_COMMAND));
    }

    @Override
    public boolean supports(Update update) {
        return true;
    }
}
