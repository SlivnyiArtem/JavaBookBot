package ru.urfu.bot.handler.commands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.urfu.bot.utils.MessageConst;

import java.util.List;

/**
 * Выводит список доступных комманд.
 */
@Component
public class HelpBotCommandHandler extends CommandUpdateHandler {

    @Override
    protected List<SendMessage> execute(Command command) {
        return List.of(new SendMessage(command.chatId().toString(), MessageConst.HELP));
    }

    @Override
    protected boolean canExecute(String[] args) {
        return args.length == 1 && args[0].equals("/help");
    }
}
