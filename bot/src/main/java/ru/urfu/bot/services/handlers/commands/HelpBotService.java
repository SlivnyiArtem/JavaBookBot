package ru.urfu.bot.services.handlers.commands;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.urfu.bot.services.handlers.CommandHandler;
import ru.urfu.bot.utils.MessageConst;
import ru.urfu.bot.utils.dto.Command;

import java.util.List;

/**
 * Выводит список доступных комманд.
 */
@Service
public class HelpBotService implements CommandHandler {

    @Override
    public List<SendMessage> handle(Command command, String username, String chatId) {
        return List.of(new SendMessage(chatId, MessageConst.HELP));
    }
}
