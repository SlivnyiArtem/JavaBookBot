package ru.urfu.bot.handlers.commands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.urfu.bot.db.entities.Book;
import ru.urfu.bot.handlers.CommandHandler;
import ru.urfu.bot.services.UserBookService;
import ru.urfu.bot.utils.MessageConst;
import ru.urfu.bot.utils.dto.Command;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Выводит список книг пользователя.
 */
@Component
public class PrintBooksHandler implements CommandHandler {

    private final UserBookService userBookService;

    public PrintBooksHandler(UserBookService userBookService) {
        this.userBookService = userBookService;
    }


    @Override
    public List<SendMessage> handle(Command command, Update update) {
        String userName = update.getMessage().getChat().getUserName();
        Long chatId = update.getMessage().getChatId();

        List<Book> books;

        try {
            books = userBookService.getUserBooks(userName);
        } catch (NoSuchElementException e) {
            return List.of();
        }

        return books.stream()
                .map(book -> {
                    SendMessage sendMessage = new SendMessage(chatId.toString(),
                            MessageConst.BOOK_INFO_SHORT.formatted(
                                    book.getIsbn13(), book.getTitle(), book.getAuthors(), book.getPublishedDate()));

                    InlineKeyboardButton removeButton = new InlineKeyboardButton();
                    removeButton.setText(MessageConst.REMOVE_BUTTON_TEXT);
                    removeButton.setCallbackData(MessageConst.REMOVE_BUTTON_CALLBACK.formatted(book.getIsbn13()));

                    InlineKeyboardButton infoButton = new InlineKeyboardButton();
                    infoButton.setText(MessageConst.INFO_BUTTON_TEXT);
                    infoButton.setCallbackData(MessageConst.INFO_BUTTON_CALLBACK.formatted(book.getIsbn13()));

                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(
                            List.of(List.of(removeButton, infoButton))
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
