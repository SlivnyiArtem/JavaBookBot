package ru.urfu.bot.utils;

/**
 * Список текстовых констант для ответных сообщений, коллбэков и т.д.
 */
public class MessageConst {

    public static final String ADD_BOOK = "Книга добавленна в избранное";

    public static final String REMOVE_BOOK = "Книга удаленна из избранных";

    public static final String BOOK_INFO = "isbn: %d\nНазвание: %s\nОписание: %s\nАвторы: %s\nИздатель: %s\nДата издания: %s";

    public static final String BOOK_INFO_SHORT = "isbn: %d\nНазвание: %s\nАвторы: %s\nДата издания: %s";

    public static final String ADD_BUTTON_CALLBACK = "/add_book %d";

    public static final String REMOVE_BUTTON_CALLBACK = "/remove_book %d";

    public static final String INFO_BUTTON_CALLBACK = "/book_inf %d";

    public static final String ADD_BUTTON_TEXT = "Добавить книгу в избранное";

    public static final String REMOVE_BUTTON_TEXT = "Удалить книгу из избранного";

    public static final String INFO_BUTTON_TEXT = "Показать дополнительную информацию";

    public static final String UNKNOWN_COMMAND = "Неизвестная команда. Введите /help для получения списка команд";

    public static final String HELP = """
                /start - начать работать с ботом
                /search {title} - поиск книги по названию
                /my_books - вывести список книг
                /help - помощь
                """;

    public static final String INTERNAL_ERROR = "Внутренняя ошибка сервера";

    public static final String EMPTY_LIST = "Книги не найдены";
}
