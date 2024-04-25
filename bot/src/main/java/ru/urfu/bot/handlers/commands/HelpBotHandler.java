package ru.urfu.bot.handlers.commands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.handlers.CommandHandler;
import ru.urfu.bot.utils.MessageConst;
import ru.urfu.bot.utils.dto.Command;

import java.util.List;

/**
 * Выводит список доступных комманд.
 */
@Component
public class HelpBotHandler implements CommandHandler {

    @Override
    public List<SendMessage> handle(Command command, Update update) {
        Long chatId = update.getMessage().getChatId();

        return List.of(new SendMessage(chatId.toString(), MessageConst.HELP));
    }

    @Override
    public boolean supports(Update update) {
        return true;
    }
}
