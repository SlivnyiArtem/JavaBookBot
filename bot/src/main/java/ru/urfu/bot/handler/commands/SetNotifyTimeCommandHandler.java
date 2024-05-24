package ru.urfu.bot.handler.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.urfu.bot.service.ChatUserService;
import ru.urfu.bot.utils.MessageConst;
import ru.urfu.bot.utils.OrderedHandler;

import java.time.OffsetTime;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Устанавливает время для отправки уведомлений для указанного пользователя.
 */
@OrderedHandler
public class SetNotifyTimeCommandHandler extends CommandUpdateHandler {

    private final ChatUserService chatUserService;

    @Autowired
    public SetNotifyTimeCommandHandler(ChatUserService chatUserService) {
        this.chatUserService = chatUserService;
    }

    @Override
    protected List<SendMessage> execute(Command command) {
        OffsetTime scheduledTime = OffsetTime.parse(command.args()[1]);
        chatUserService.setNotificationTime(command.username(), scheduledTime);
        return List.of(new SendMessage(command.chatId().toString(), MessageConst.SET_SCHEDULED_TIME));
    }

    @Override
    protected boolean canExecute(String[] args) {
        return args.length == 2 && args[0].equals("/set_time") && isNotBlank(args[1]);
    }
}
