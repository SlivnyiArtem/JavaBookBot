package ru.urfu.bot.domain.handlers.books;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.urfu.bot.domain.entities.Book;
import ru.urfu.bot.domain.handlers.Command;
import ru.urfu.bot.domain.services.UserBookService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Ищет книги по названию в стороннем сервисе
 */
@Component
public class SearchBookCommand implements Command {

    private final UserBookService userBookService;

    public SearchBookCommand(UserBookService userBookService) {
        this.userBookService = userBookService;
    }

    @Override
    public List<SendMessage> handle(Update update) {
        Long chatId = update.getMessage().getChatId();

        String query = update.getMessage().getText().substring(update.getMessage().getText().indexOf(" ") + 1);

        return userBookService.findBooksByTitle(query).stream()
                .map(book -> {
                    SendMessage sendMessage = new SendMessage(chatId.toString(),
                            "isbn: %d\nНазвание: %s\nАвторы: %s\nИздатель: %s\nДата издания: %s\n/add_book %s\n\n".formatted(
                            book.getIsbn13(), book.getTitle(),
                            book.getAuthors(), book.getPublisher(),
                            book.getPublishedDate(), book.getIsbn13()));

                    InlineKeyboardButton addButton = new InlineKeyboardButton();
                    addButton.setText("Добавить книгу в избранное");
                    addButton.setCallbackData("/add_book %d".formatted(book.getIsbn13()));
                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(
                            List.of(List.of(addButton))
                    );
                    sendMessage.setReplyMarkup(inlineKeyboardMarkup);
                    return sendMessage;
                })
                .toList();
    }

    @Override
    public boolean supports(Update update) {
        return userBookService.containsChat(update.getMessage().getChatId());
    }
}
