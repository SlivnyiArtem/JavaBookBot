package ru.urfu.bot.domain.handlers.books;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.domain.port.UserBookService;
import ru.urfu.bot.domain.entities.Book;
import ru.urfu.bot.domain.handlers.Command;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PrintBooksCommand implements Command {

    private final UserBookService userBookService;

    public PrintBooksCommand(UserBookService userBookService) {
        this.userBookService = userBookService;
    }


    @Override
    public SendMessage handle(Update update) {
        String userName = update.getMessage().getChat().getUserName();
        Long chatId = update.getMessage().getChatId();

        List<Book> books = userBookService.getUserBooks(userName);
        String message = "Книги в избранном:\n%s".formatted(books.stream()
                .map(book -> "isbn: %d\nНазвание: %s\nОписание: %s\nАвторы: %s\nИздатель: %s\nДата издания: %s\n\n".formatted(
                        book.getIsbn13(), book.getTitle(), book.getDescription(),
                        book.getAuthors(), book.getPublisher(), book.getPublishedDate()
                )).collect(Collectors.joining()));

        return new SendMessage(chatId.toString(), message);
    }
}
