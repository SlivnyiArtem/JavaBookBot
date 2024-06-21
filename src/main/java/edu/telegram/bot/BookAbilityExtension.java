package edu.telegram.bot;

import edu.telegram.domain.Book;
import edu.telegram.exception.DataNotFoundException;
import edu.telegram.service.BookTrackingService;
import edu.telegram.utils.MessageConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.abilitybots.api.objects.Ability;
import org.telegram.telegrambots.abilitybots.api.util.AbilityExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

import static edu.telegram.utils.MessageConst.*;
import static org.telegram.telegrambots.abilitybots.api.objects.Flag.*;
import static org.telegram.telegrambots.abilitybots.api.objects.Locality.ALL;
import static org.telegram.telegrambots.abilitybots.api.objects.Privacy.PUBLIC;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.isReplyTo;

@Component
public class BookAbilityExtension implements AbilityExtension {

    private static final String DATA_NOT_FOUND_LOG_MSG = "some data not found";

    private static final String UNKNOWN_EXCEPTION_LOG_MSG = "unknown exception";

    private final BookTrackingService bookTrackingService;

    private static final Logger log = LoggerFactory.getLogger(BookAbilityExtension.class);

    public BookAbilityExtension(BookTrackingService bookTrackingService) {
        this.bookTrackingService = bookTrackingService;
    }

    public Ability searchBook() {
        return Ability.builder()
                .name(Commands.SEARCH.getName())
                .info(Commands.SEARCH.getInfo())
                .locality(ALL)
                .privacy(PUBLIC)
                .flag(TEXT)
                .action(ctx -> ctx.bot().getSilent().forceReply(SEARCH_BOOK_MESSAGE, ctx.chatId()))
                .reply((bot, update) -> {
                    try {
                        List<Book> books = bookTrackingService.getBooksByTitle(update.getMessage().getText());
                        books.forEach(book -> {
                            SendMessage message = createBookInfoMessage(book, getChatId(update).toString());
                            bot.getSilent().execute(message);
                        });

                        if (books.isEmpty()) {
                            bot.getSilent().send(EMPTY_LIST, getChatId(update));
                        }
                    } catch (Exception e) {
                        log.error(UNKNOWN_EXCEPTION_LOG_MSG, e);
                        bot.getSilent().send(INTERNAL_SERVER_ERROR_MESSAGE, getChatId(update));
                    }
                }, REPLY, TEXT, isReplyTo(SEARCH_BOOK_MESSAGE))
                .reply((bot, update) -> {
                    String response;
                    try {
                        Long isbn = Long.parseLong(update.getCallbackQuery().getData().split(" ", 2)[1]);
                        bookTrackingService.trackBook(isbn, update.getCallbackQuery().getFrom().getUserName());
                        response = BOOK_ADDED_MESSAGE;
                    } catch (DataNotFoundException e) {
                        response = INTERNAL_SERVER_ERROR_MESSAGE;
                        log.warn(DATA_NOT_FOUND_LOG_MSG, e);
                    } catch (Exception e) {
                        response = INTERNAL_SERVER_ERROR_MESSAGE;
                        log.warn("incorrect callback received", e);
                    }
                    bot.getSilent().send(response, getChatId(update));
                }, CALLBACK_QUERY, update -> update.getCallbackQuery().getData().startsWith(ADD_BUTTON_CALLBACK))
                .build();
    }

    private SendMessage createBookInfoMessage(Book book, String chatId) {
        SendMessage sendMessage = new SendMessage(chatId, BOOK_INFO_SHORT.formatted(
                book.getIsbn(), book.getTitle(), book.getAuthors(), book.getPublishedDate()));

        InlineKeyboardButton addButton = InlineKeyboardButton.builder()
                .text(ADD_BUTTON_TEXT)
                .callbackData(ADD_BUTTON_CALLBACK + ' ' + book.getIsbn())
                .build();
        InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(addButton))
                .build();
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    public Ability printMyBooks() {
        return Ability.builder()
                .name(Commands.PRINT.getName())
                .info(Commands.PRINT.getInfo())
                .locality(ALL)
                .privacy(PUBLIC)
                .flag(TEXT)
                .action(ctx -> {
                    List<Book> books = bookTrackingService.getUserBooks(ctx.user().getUserName());
                    books.forEach(book -> {
                        SendMessage message = createUserBookInfoMessage(book, getChatId(ctx.update()).toString());
                        ctx.bot().getSilent().execute(message);
                    });

                    if (books.isEmpty()) {
                        ctx.bot().getSilent().send(EMPTY_LIST, getChatId(ctx.update()));
                    }
                })
                .reply((bot, update) -> {
                    Long isbn = Long.parseLong(update.getCallbackQuery().getData().split(" ", 2)[1]);
                    Book book = bookTrackingService.getBook(isbn);
                    String message = BOOK_INFO_LONG.formatted(
                            book.getIsbn(), book.getTitle(), book.getDescription(),
                            book.getAuthors(), book.getPublisher(), book.getPublishedDate()
                    );
                    bot.getSilent().send(message, getChatId(update));
                }, CALLBACK_QUERY, update -> update.getCallbackQuery().getData().startsWith(INFO_BUTTON_CALLBACK))
                .reply((bot, update) -> {
                    Long isbn = Long.parseLong(update.getCallbackQuery().getData().split(" ", 2)[1]);
                    bookTrackingService.untrackBook(isbn, update.getCallbackQuery().getFrom().getUserName());
                    bot.getSilent().send(BOOK_REMOVED_MESSAGE, getChatId(update));
                }, CALLBACK_QUERY, update -> update.getCallbackQuery().getData().startsWith(REMOVE_BUTTON_CALLBACK))
                .build();
    }

    private SendMessage createUserBookInfoMessage(Book book, String chatId) {
        SendMessage sendMessage = new SendMessage(chatId, BOOK_INFO_SHORT.formatted(
                book.getIsbn(), book.getTitle(), book.getAuthors(), book.getPublishedDate()));

        InlineKeyboardButton removeButton = InlineKeyboardButton.builder()
                .text(REMOVE_BUTTON_TEXT)
                .callbackData(REMOVE_BUTTON_CALLBACK + ' ' + book.getIsbn())
                .build();
        InlineKeyboardButton infoButton = InlineKeyboardButton.builder()
                .text(INFO_BUTTON_TEXT)
                .callbackData(INFO_BUTTON_CALLBACK + ' ' + book.getIsbn())
                .build();

        InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(removeButton, infoButton))
                .build();

        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }
}
