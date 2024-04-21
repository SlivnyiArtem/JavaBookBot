package ru.urfu.bot.domain.handlers.books;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.domain.entities.Book;
import ru.urfu.bot.domain.handlers.Command;
import ru.urfu.bot.domain.services.UserBookService;

import java.util.NoSuchElementException;

/**
 * Сохраняет книгу по isbn коду в бд для конретного пользователя
 */
@Component
public class AddBookCommand implements Command {

    private final UserBookService userBookService;

    public AddBookCommand(UserBookService userBookService) {
        this.userBookService = userBookService;
    }

    /**
     * Добавление книги в список избранного
     * @param update - данные входящего сообщения
     * @return - сообщение об успешности операции
     */
    @Override
    public SendMessage handle(Update update) {
        String userName = update.getMessage().getChat().getUserName();
        Long chatId = update.getMessage().getChatId();

        String query = update.getMessage().getText().split(" ")[1];

        try {
            Book book = userBookService.findBookByIsbn(Long.parseLong(query));
            userBookService.addBook(userName, book);
            return new SendMessage(chatId.toString(), "Книга добавленна в избранное");
        } catch (NoSuchElementException e) {
            return new SendMessage(chatId.toString(), "Книга не найдена");
        }
    }

    @Override
    public boolean supports(Update update) {
        return userBookService.containsChat(update.getMessage().getChatId());
    }
}
