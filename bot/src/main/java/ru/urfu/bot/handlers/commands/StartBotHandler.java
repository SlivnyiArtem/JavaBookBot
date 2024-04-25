package ru.urfu.bot.handlers.commands;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.handlers.CommandHandler;
import ru.urfu.bot.services.UserBookService;
import ru.urfu.bot.utils.MessageConst;
import ru.urfu.bot.utils.dto.Command;

import java.util.List;

/**
 * Запускает бота и записывает пользователя и чат в бд.
 */
@Service
public class StartBotHandler implements CommandHandler {

    private final UserBookService userBookService;

    public StartBotHandler(UserBookService userBookService) {
        this.userBookService = userBookService;
    }

    @Override
    public List<SendMessage> handle(Command command, Update update) {
        String userName = update.getMessage().getChat().getUserName();
        Long chatId = update.getMessage().getChatId();

        userBookService.addChat(userName, chatId);

        return List.of(new SendMessage(chatId.toString(), MessageConst.BOT_STARTED));
    }

    @Override
    public boolean supports(Update update) {
        return true;
    }
}
