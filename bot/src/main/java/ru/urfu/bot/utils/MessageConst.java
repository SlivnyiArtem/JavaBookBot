package ru.urfu.bot.utils;

/**
 * Список текстовых констант для ответных сообщений, коллбэков и т.д.
 */
public class MessageConst {

    public static final String ADD_BOOK = "Книга добавлена в избранное";

    public static final String REMOVE_BOOK = "Книга удалена из избранных";

    public static final String BOOK_INFO = "ISBN: %d\nНазвание: %s\nОписание: %s\nАвторы: %s\nИздатель: %s\nДата издания: %s";

    public static final String BOOK_INFO_SHORT = "ISBN: %d\nНазвание: %s\nАвторы: %s\nДата издания: %s";

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
                /set_time {time} - установить время для получения уведомлений (например '10:15+01:00' или '10:15:30+01:00')
                /help - помощь
                """;

    public static final String INTERNAL_SERVER_ERROR = "Внутренняя ошибка сервера";

    public static final String EMPTY_LIST = "Книги не найдены";

    public static final String BOOK_NOT_FOUND = "Книга не найденна в коллекции пользователя";

    public static final String BOOK_RELEASE = "Книга %s (isbn: %d) вышла";

    public static final String BOOK_UPDATE_INFO = "Информация о книге %s (isbn: %d) обновленна";

    public static final String SET_SCHEDULED_TIME = "Установленно время для получения уведомлений";
}
