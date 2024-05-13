package ru.urfu.bot.services;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.utils.dto.Command;
import ru.urfu.bot.utils.dto.CommandType;

import static org.apache.commons.lang3.StringUtils.isNumeric;

/**
 * Парсит сообщения.
 */
@Service
public class Parser {

    /**
     * Парсит обновления, имеющие коллбэк
     * @param update обновление
     * @return комманда
     */
    public Command parseCallback(Update update) {
        String query = update.getCallbackQuery().getData();
        String data = query.split(" ").length == 2 && isNumeric(query.split(" ")[1])
                ? query.split(" ")[1]
                : "-1";
        return switch (query.split(" ", 2)[0]) {
            case "/add_book" -> new Command(CommandType.ADD, data);
            case "/remove_book" -> new Command(CommandType.REMOVE, data);
            case "/book_inf" -> new Command(CommandType.INFO, data);
            default -> new Command(CommandType.UNKNOWN, update.getCallbackQuery().getMessage().getChatId().toString());
        };
    }

    /**
     * Парсит обновления с текстом
     * @param update обновление
     * @return комманда
     */
    public Command parseCommand(Update update) {
        String query = update.getMessage().getText();
        String data = query.split(" ", 2).length > 1
                ? query.split(" ", 2)[1]
                : "";
        return switch (query.split(" ", 2)[0]) {
            case "/start" -> new Command(CommandType.START, "");
            case "/search" -> new Command(CommandType.SEARCH, data);
            case "/my_books" -> new Command(CommandType.PRINT, "");
            case "/help" -> new Command(CommandType.HELP, "");
            case "/set_time" -> new Command(CommandType.SET_TIME, data);
            default -> new Command(CommandType.UNKNOWN, update.getMessage().getChatId().toString());
        };
    }
}
