package ru.urfu.bot.services.handlers.commands;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.urfu.bot.db.entities.Book;
import ru.urfu.bot.db.repositories.JpaBookRepository;
import ru.urfu.bot.services.handlers.CommandHandler;
import ru.urfu.bot.utils.MessageConst;
import ru.urfu.bot.utils.dto.Command;

import java.util.List;

/**
 * Выводит список книг пользователя.
 */
@Service
public class PrintBooksService implements CommandHandler {

    private final JpaBookRepository bookRepository;

    public PrintBooksService(JpaBookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional
    public List<SendMessage> handle(Command command, String username, String chatId) {

        List<SendMessage> books = bookRepository.findAllByUsers_UserName(username).stream()
                .map((book -> createBookInfoMessage(book, chatId)) )
                .toList();
        return books.isEmpty()
                ? List.of(new SendMessage(chatId, MessageConst.EMPTY_LIST))
                : books;
    }

    private SendMessage createBookInfoMessage(Book book, String chatId) {

        SendMessage sendMessage = new SendMessage(chatId,
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
    }
}
