package ru.urfu.bot.domain.handlers.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.domain.handlers.Command;
import ru.urfu.bot.domain.services.UserBookService;

/**
 * Выводит список доступных комманд
 */
@Component
public class HelpBotCommand implements Command {

    private final UserBookService userBookService;

    public HelpBotCommand(UserBookService userBookService) {
        this.userBookService = userBookService;
    }

    @Override
    public SendMessage handle(Update update) {
        String userName = update.getMessage().getChat().getUserName();
        Long chatId = update.getMessage().getChatId();

        String message = """
                /start - начать работать с ботом
                /search_book {title} - поиск книги по названию
                /my_books - вывести список книг
                /add_book {isbn} - добавить книгу
                /remove_book {isbn} - убрать книгу
                /help - помощь
                /book_inf {isbn} - иинформация о книге
                """;

        return new SendMessage(chatId.toString(), message);
    }

    @Override
    public boolean supports(Update update) {
        return true;
    }
}
