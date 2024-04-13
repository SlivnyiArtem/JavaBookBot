package ru.urfu.bot.domain.handlers.books;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.app.UserBookService;
import ru.urfu.bot.domain.entities.Book;
import ru.urfu.bot.domain.handlers.Command;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SearchBookCommand implements Command {

    private final UserBookService userBookService;

    public SearchBookCommand(UserBookService userBookService) {
        this.userBookService = userBookService;
    }

    @Override
    public SendMessage handle(Update update) {
        Long chatId = update.getMessage().getChatId();

        String query = update.getMessage().getText().split(" ")[1];

        List<Book> bookList = userBookService.getBooksByTitle(query);
        String message = "Результаты поиска:\n%s".formatted(bookList.stream()
                .map(book -> "isbn: %d\nНазвание: %s\nАвторы: %s\nИздатель: %s\nДата издания: %s\n\n".formatted(
                        book.getIsbn13(), book.getTitle(),
                        book.getAuthors(), book.getPublisher(), book.getPublishedDate()
                )).collect(Collectors.joining()));

        return new SendMessage(chatId.toString(), message);
    }
}
