package ru.urfu.bot.domain.handlers.books;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.domain.entities.Book;
import ru.urfu.bot.domain.handlers.Command;
import ru.urfu.bot.domain.services.UserBookService;

import java.util.NoSuchElementException;

/**
 * Выводит информацию о книге по isbn (в бд)
 */
@Component
public class BookInfoCommand implements Command {

    private final UserBookService userBookService;

    public BookInfoCommand(UserBookService userBookService) {
        this.userBookService = userBookService;
    }

    @Override
    public SendMessage handle(Update update) {
        String userName = update.getMessage().getChat().getUserName();
        Long chatId = update.getMessage().getChatId();

        String query = update.getMessage().getText().split(" ")[1];
        String message;
        try {
            Book book = userBookService.getUserBooks(userName).stream()
                    .filter(book1 -> book1.getIsbn13().equals(Long.parseLong(query)))
                    .findFirst()
                    .orElseThrow();

            message = "isbn: %d\nНазвание: %s\nОписание: %s\nАвторы: %s\nИздатель: %s\nДата издания: %s\n\n"
                    .formatted(
                            book.getIsbn13(), book.getTitle(), book.getDescription(),
                            book.getAuthors(), book.getPublisher(), book.getPublishedDate()
                    );
        }
        catch (NoSuchElementException exc){
            message = "В списке избранного отсутствует книга с данным ISBN: %d".formatted(Long.parseLong(query));
        }

        return new SendMessage(chatId.toString(), message);
    }

    @Override
    public boolean supports(Update update) {
        return userBookService.containsChat(update.getMessage().getChatId());
    }
}