package ru.urfu.bot.handler.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.urfu.bot.service.ChatUserService;
import ru.urfu.bot.utils.MessageConst;
import ru.urfu.bot.utils.OrderedHandler;

import java.util.List;

/**
 * Запускает бота и записывает пользователя и чат в бд.
 */
@OrderedHandler
public class StartBotCommandHandler extends CommandUpdateHandler {

    private final ChatUserService chatUserService;

    private final Logger logger = LoggerFactory.getLogger(StartBotCommandHandler.class);

    @Autowired
    public StartBotCommandHandler(ChatUserService chatUserService) {
        this.chatUserService = chatUserService;
    }

    @Override
    protected List<SendMessage> execute(Command command) {
        String username = command.username();
        Long chatId = command.chatId();

        if (chatUserService.addUserChatIfAbsent(username, chatId)) {
            logger.debug("user %s added".formatted(username));
            return List.of(new SendMessage(chatId.toString(), MessageConst.HELP));
        }

        return List.of();
    }

    @Override
    protected boolean canExecute(String[] args) {
        return args.length == 1 && args[0].equals("/start");
    }
}
