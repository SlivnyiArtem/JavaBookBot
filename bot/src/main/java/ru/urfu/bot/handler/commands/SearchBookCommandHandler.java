package ru.urfu.bot.handler.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.urfu.bot.domain.Book;
import ru.urfu.bot.service.BookTrackingService;
import ru.urfu.bot.utils.MessageConst;
import ru.urfu.bot.utils.OrderedHandler;

import java.util.List;

/**
 * Ищет книги по названию в стороннем сервисе.
 */
@OrderedHandler
public class SearchBookCommandHandler extends CommandUpdateHandler {

    private final BookTrackingService bookTrackingService;

    @Autowired
    public SearchBookCommandHandler(BookTrackingService bookTrackingService) {
        this.bookTrackingService = bookTrackingService;
    }

    @Override
    protected List<SendMessage> execute(Command command) {
        String text = command.args()[1];
        String chatId = command.chatId().toString();

        List<SendMessage> books = bookTrackingService.getBooksByTitle(text).stream()
                .map(book -> createBookInfoMessage(book, chatId))
                .toList();

        return books.isEmpty()
                ? List.of(new SendMessage(chatId, MessageConst.EMPTY_LIST))
                : books;
    }

    @Override
    protected boolean canExecute(String[] args) {
        return args.length == 2 && args[0].equals("/search");
    }

    private SendMessage createBookInfoMessage(Book book, String chatId) {
        SendMessage sendMessage = new SendMessage(chatId,
                MessageConst.BOOK_INFO_SHORT.formatted(
                        book.getIsbn(), book.getTitle(), book.getAuthors(), book.getPublishedDate()));

        InlineKeyboardButton addButton = new InlineKeyboardButton();
        addButton.setText(MessageConst.ADD_BUTTON_TEXT);
        addButton.setCallbackData(MessageConst.ADD_BUTTON_CALLBACK.formatted(book.getIsbn()));
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(
                List.of(List.of(addButton))
        );
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }
}
