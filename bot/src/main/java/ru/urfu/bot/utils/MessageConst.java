package ru.urfu.bot.utils;

import java.util.Map;

/**
 * Список текстовых констант для ответных сообщений, коллбэков и т.д.
 */
public final class MessageConst {

    private MessageConst() {

    }

    public static final String BOOK_INFO_LONG = "ISBN: %d\ntitle: %s\nDescription: %s\nAuthors: %s\nPublisher: %s\nPublished date: %s";

    public static final String BOOK_INFO_SHORT = "ISBN: %d\nTitle: %s\nAuthors: %s\nPublished date: %s";

    public static final String ADD_BUTTON_CALLBACK = "add_book";

    public static final String REMOVE_BUTTON_CALLBACK = "remove_book";

    public static final String INFO_BUTTON_CALLBACK = "book_info";

    public static final String ADD_BUTTON_TEXT = "Add book";

    public static final String REMOVE_BUTTON_TEXT = "Remove book";

    public static final String INFO_BUTTON_TEXT = "Show more info";

    public static final Map<String, String> COMMANDS = Map.of(
            "start", "start conversation",
            "search", "search book by title in external API",
            "my_books", "print user's tracking book",
            "set_time", "set time when receiving notification",
            "help", "get bot commands"
    );

    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal server error";

    public static final String UNKNOWN_COMMAND_MESSAGE = "Unknown command. Type /help to get list of commands";

    public static final String EMPTY_LIST = "Books not found";

    public static final String SEARCH_BOOK_MESSAGE = "Enter book title:";

    public static final String BOOK_RELEASED_MESSAGE = "Book '%s' (isbn: %d) has released";

    public static final String BOOK_UPDATED_MESSAGE = "Book info '%s' (isbn: %d) has updated";

    public static final String SET_SCHEDULED_TIME_MESSAGE = "The time for receiving notifications is set";

    public static final String BOOK_ADDED_MESSAGE = "Book saved to collection";

    public static final String BOOK_REMOVED_MESSAGE = "Book removed from collection";
}
