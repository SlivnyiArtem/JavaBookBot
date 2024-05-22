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
 * Выводит список книг пользователя.
 */
@OrderedHandler
public class PrintBooksCommandHandler extends CommandUpdateHandler {

    private final BookTrackingService bookTrackingService;

    @Autowired
    public PrintBooksCommandHandler(BookTrackingService bookTrackingService) {
        this.bookTrackingService = bookTrackingService;
    }

    @Override
    protected List<SendMessage> execute(Command command) {
        String username = command.username();
        String chatId = command.chatId().toString();

        List<SendMessage> books = bookTrackingService.getUserBooks(username).stream()
                .map((book -> createBookInfoMessage(book, chatId)) )
                .toList();
        return books.isEmpty()
                ? List.of(new SendMessage(chatId, MessageConst.EMPTY_LIST))
                : books;
    }

    @Override
    protected boolean canExecute(String[] args) {
        return args.length == 1 && args[0].equals("/my_books");
    }

    private SendMessage createBookInfoMessage(Book book, String chatId) {

        SendMessage sendMessage = new SendMessage(chatId,
                MessageConst.BOOK_INFO_SHORT.formatted(
                        book.getIsbn(), book.getTitle(), book.getAuthors(), book.getPublishedDate()));

        InlineKeyboardButton removeButton = new InlineKeyboardButton();
        removeButton.setText(MessageConst.REMOVE_BUTTON_TEXT);
        removeButton.setCallbackData(MessageConst.REMOVE_BUTTON_CALLBACK.formatted(book.getIsbn()));

        InlineKeyboardButton infoButton = new InlineKeyboardButton();
        infoButton.setText(MessageConst.INFO_BUTTON_TEXT);
        infoButton.setCallbackData(MessageConst.INFO_BUTTON_CALLBACK.formatted(book.getIsbn()));

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(
                List.of(List.of(removeButton, infoButton))
        );
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }
}
