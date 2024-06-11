package ru.urfu.bot.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerResponse;
import org.telegram.telegrambots.abilitybots.api.objects.Ability;
import org.telegram.telegrambots.abilitybots.api.objects.Flag;
import org.telegram.telegrambots.abilitybots.api.util.AbilityExtension;
import org.telegram.telegrambots.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import reactor.core.publisher.Mono;
import ru.urfu.bot.domain.Book;
import ru.urfu.bot.exception.DataNotFoundException;
import ru.urfu.bot.exception.EmptyResultException;
import ru.urfu.bot.service.BookTrackingService;
import ru.urfu.bot.utils.MessageConst;

import java.util.List;
import java.util.NoSuchElementException;

import static org.telegram.telegrambots.abilitybots.api.objects.Flag.*;
import static org.telegram.telegrambots.abilitybots.api.objects.Locality.ALL;
import static org.telegram.telegrambots.abilitybots.api.objects.Privacy.PUBLIC;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.*;
import static ru.urfu.bot.utils.MessageConst.*;

@Component
public class BookAbilityExtension implements AbilityExtension {

    private final BookTrackingService bookTrackingService;

    private static final Logger log = LoggerFactory.getLogger(BookAbilityExtension.class);

    public BookAbilityExtension(BookTrackingService bookTrackingService) {
        this.bookTrackingService = bookTrackingService;
    }

    public Ability searchBook() {
        return Ability.builder()
                .name("search")
                .info(COMMANDS.get("search"))
                .locality(ALL)
                .privacy(PUBLIC)
                .flag(TEXT)
                .action(ctx -> ctx.bot().getSilent().forceReply(SEARCH_BOOK_MESSAGE, ctx.chatId()))
                .reply((bot, update) -> {
                    List<Book> books = bookTrackingService.getBooksByTitle(update.getMessage().getText());
                    books.forEach(book -> {
                        SendMessage message = createBookInfoMessage(book, getChatId(update).toString());
                        bot.getSilent().execute(message);
                    });

                    if (books.isEmpty()) {
                        bot.getSilent().send(EMPTY_LIST, getChatId(update));
                    }
                }, REPLY, TEXT, isReplyTo(SEARCH_BOOK_MESSAGE))
                .reply((bot, update) -> {
                    long isbn;
                    try {
                        isbn = Long.parseLong(update.getCallbackQuery().getData().split(" ", 2)[1]);
                    } catch (Exception e) {
                        log.warn("incorrect callback received", e);
                        return;
                    }
                    bookTrackingService.trackBook(isbn, update.getCallbackQuery().getFrom().getUserName());
                    bot.getSilent().send(BOOK_ADDED_MESSAGE, update.getCallbackQuery().getMessage().getChatId());
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
                .name("my_books")
                .info(COMMANDS.get("my_books"))
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
