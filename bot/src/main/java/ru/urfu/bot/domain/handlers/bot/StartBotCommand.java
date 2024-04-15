package ru.urfu.bot.domain.handlers.bot;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.domain.handlers.Command;
import ru.urfu.bot.domain.port.UserBookService;

/**
 * Запускает бота и записывает пользователя и чат в бд
 */
@Service
public class StartBotCommand implements Command {

    private final UserBookService userBookService;

    public StartBotCommand(UserBookService userBookService) {
        this.userBookService = userBookService;
    }

    @Override
    public SendMessage handle(Update update) {
        String userName = update.getMessage().getChat().getUserName();
        Long chatId = update.getMessage().getChatId();

        userBookService.addChat(userName, chatId);

        return new SendMessage(chatId.toString(), "Bot started");
    }

    @Override
    public boolean supports(Update update) {
        return true;
    }
}
