package ru.urfu.bot.domain.handlers.books;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.domain.entities.Book;
import ru.urfu.bot.domain.handlers.Command;
import ru.urfu.bot.domain.services.UserBookService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Выводит список книг пользователя
 */
@Component
public class PrintBooksCommand implements Command {

    private final UserBookService userBookService;

    public PrintBooksCommand(UserBookService userBookService) {
        this.userBookService = userBookService;
    }


    @Override
    public List<SendMessage> handle(Update update) {
        String userName = update.getMessage().getChat().getUserName();
        Long chatId = update.getMessage().getChatId();

        List<Book> books = userBookService.getUserBooks(userName);
        String message = "Книги в избранном:\n%s".formatted(books.stream()
                .map(book -> "isbn: %d\nНазвание: %s\n\n".formatted(
                        book.getIsbn13(), book.getTitle()
                )).collect(Collectors.joining()));

        return List.of(new SendMessage(chatId.toString(), message));
    }

    @Override
    public boolean supports(Update update) {
        return userBookService.containsChat(update.getMessage().getChatId());
    }
}
