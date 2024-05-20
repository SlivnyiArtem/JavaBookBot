package ru.urfu.bot.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Обработчик для обновлений телеграм бота
 */
public interface UpdateHandler {

    /**
     * Обрабатывает обновление
     * @param update обновление бота
     * @return ответ пользователю
     */
    List<SendMessage> process(Update update);

    /**
     * Проверят может ли {@link UpdateHandler} обработать обновление
     * @param update телеграм обновление
     * @return true, если способна обработать, иначе false
     */
    boolean canHandle(Update update);

    /**
     * Спарсенное обновление; Только для внутреннего использования
     * @param args содержание обновления
     * @param username имя пользователя
     * @param chatId ID чата
     */
    record Command(@NotEmpty String[] args, @NotBlank String username, @NotNull Long chatId) { }
}
