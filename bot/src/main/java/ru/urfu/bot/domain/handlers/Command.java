package ru.urfu.bot.domain.handlers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Команда для управления ботом
 */
public interface Command {

    /**
     * Метод обрабатывает сообщение пользователя и возварщает ответ
     */
    SendMessage handle(Update update);

    /**
     * Возвращает true, если команда поддерживается в данный момент
     */
    boolean supports(Update update);
}
