package ru.urfu.bot.handlers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.utils.dto.Command;

import java.util.List;

/**
 * Команда для управления ботом
 */
public interface CommandHandler {

    /**
     * Метод обрабатывает сообщение пользователя и возвращает ответ
     * @param command dto объект текущей команды
     * @param update входящее обновление бота
     * @return ответные сообщения для пользователя
     */
    List<SendMessage> handle(Command command, Update update);

    /**
     * Проверить доступность команды
     * @param update входящее обновление бота
     * @return возвращает true, если команда поддерживается
     */
    boolean supports(Update update);
}
