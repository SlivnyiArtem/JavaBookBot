package edu.telegram.utils;

import java.util.Map;

/**
 * Список текстовых констант для ответных сообщений, коллбэков и т.д.
 */
public final class MessageConst {

    private MessageConst() {

    }

    public static final String ADD_BUTTON_CALLBACK = "add_book";

    public static final String REMOVE_BUTTON_CALLBACK = "remove_book";

    public static final String INFO_BUTTON_CALLBACK = "book_info";

    public static final String ADD_BUTTON_TEXT = "Add book";

    public static final String REMOVE_BUTTON_TEXT = "Remove book";

    public static final String INFO_BUTTON_TEXT = "Show more info";

    public enum Commands {

        START("start", "start conversation"),
        SET_TIME("set_time", "set time when receiving notification"),
        HELP("help", "get available bot commands"),
        SEARCH("search", "search book by title in external API"),
        PRINT("my_books", "print user's tracking book");

        private final String name;

        private final String info;

        Commands(String name, String info) {
            this.name = name;
            this.info = info;
        }

        public String getName() {
            return name;
        }

        public String getInfo() {
            return info;
        }
    }

    public static final String BOOK_INFO_LONG = "ISBN: %d\ntitle: %s\nDescription: %s\nAuthors: %s\nPublisher: %s\nPublished date: %s";

    public static final String BOOK_INFO_SHORT = "ISBN: %d\nTitle: %s\nAuthors: %s\nPublished date: %s";

    public static final String EMPTY_LIST = "Books not found";

    public static final String SEARCH_BOOK_MESSAGE = "Enter book title:";

    public static final String BOOK_RELEASED_MESSAGE = "Book '%s' (isbn: %d) has released";

    public static final String BOOK_UPDATED_MESSAGE = "Book info '%s' (isbn: %d) has updated";

    public static final String SET_NOTIFICATION_TIME_MESSAGE = "Enter time for notification (e.g. 13:00+05:00):";

    public static final String NOTIFICATION_TIME_SET_MESSAGE = "The time for receiving notifications is set";

    public static final String WRONG_TIME_FORMAT_MESSAGE = "Wrong time string. Use this format: hh:mmZhh:mm";

    public static final String BOOK_ADDED_MESSAGE = "Book saved to collection";

    public static final String BOOK_REMOVED_MESSAGE = "Book removed from collection";

    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal server error";
}
