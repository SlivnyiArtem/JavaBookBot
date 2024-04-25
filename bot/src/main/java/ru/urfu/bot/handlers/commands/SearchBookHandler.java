package ru.urfu.bot.handlers.commands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.urfu.bot.handlers.CommandHandler;
import ru.urfu.bot.services.UserBookService;
import ru.urfu.bot.utils.MessageConst;
import ru.urfu.bot.utils.dto.Command;

import java.util.List;

/**
 * Ищет книги по названию в стороннем сервисе.
 */
@Component
public class SearchBookHandler implements CommandHandler {

    private final UserBookService userBookService;

    public SearchBookHandler(UserBookService userBookService) {
        this.userBookService = userBookService;
    }

    @Override
    public List<SendMessage> handle(Command command, Update update) {
        Long chatId = update.getMessage().getChatId();

        String query = command.data();

        return userBookService.findBooksByTitle(query).stream()
                .map(book -> {
                    SendMessage sendMessage = new SendMessage(chatId.toString(),
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
                })
                .toList();
    }

    @Override
    public boolean supports(Update update) {
        return userBookService.containsChat(update.getMessage().getChatId());
    }
}
