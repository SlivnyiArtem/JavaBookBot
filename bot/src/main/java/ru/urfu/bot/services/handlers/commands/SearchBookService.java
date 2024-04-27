package ru.urfu.bot.services.handlers.commands;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.urfu.bot.controllers.GoogleBooksApiClient;
import ru.urfu.bot.db.entities.Book;
import ru.urfu.bot.services.handlers.CommandHandler;
import ru.urfu.bot.utils.MessageConst;
import ru.urfu.bot.utils.dto.Command;

import java.util.List;

/**
 * Ищет книги по названию в стороннем сервисе.
 */
@Service
public class SearchBookService implements CommandHandler {

    private final GoogleBooksApiClient bookApiClient;

    public SearchBookService(GoogleBooksApiClient bookApiClient) {
        this.bookApiClient = bookApiClient;
    }

    @Override
    public List<SendMessage> handle(Command command, String username, String chatId) {

        String query = command.data();

        List<SendMessage> books = bookApiClient.findBooksByTitle(query).stream()
                .map(book -> createBookInfoMessage(book, chatId))
                .toList();

        return books.isEmpty()
                ? List.of(new SendMessage(chatId, MessageConst.EMPTY_LIST))
                : books;
    }

    private SendMessage createBookInfoMessage(Book book, String chatId) {
        SendMessage sendMessage = new SendMessage(chatId,
                MessageConst.BOOK_INFO_SHORT.formatted(
                        book.getIsbn13(), book.getTitle(), book.getAuthors(), book.getPublishedDate()));

        InlineKeyboardButton addButton = new InlineKeyboardButton();
        addButton.setText(MessageConst.ADD_BUTTON_TEXT);
        addButton.setCallbackData(MessageConst.ADD_BUTTON_CALLBACK.formatted(book.getIsbn13()));
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(
                List.of(List.of(addButton))
        );
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }
}
