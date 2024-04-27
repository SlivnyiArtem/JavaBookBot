package ru.urfu.bot.services.handlers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.urfu.bot.utils.dto.Command;

import java.util.List;

/**
 * Команда для управления ботом
 */
public interface CommandHandler {

    /**
     * Метод обрабатывает сообщение пользователя и возвращает ответ
     * @param command dto объект текущей команды
     * @param username телеграм никнейм пользователя
     * @param chatId id телегарам чата
     * @return ответные сообщения для пользователя
     */
    List<SendMessage> handle(Command command, String username, String chatId);
}
