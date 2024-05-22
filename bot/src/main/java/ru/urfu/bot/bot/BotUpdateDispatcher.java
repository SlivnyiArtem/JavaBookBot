package ru.urfu.bot.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.handler.UpdateHandler;

import java.util.List;

/**
 * Объект для определения и выполнения команд.
 */
@Component
public class BotUpdateDispatcher {

    private final List<UpdateHandler> updateHandlers;

    private final Logger logger = LoggerFactory.getLogger(BotUpdateDispatcher.class);

    /**
     * @param updateHandlers упорядоченный список обработчиков
     */
    public BotUpdateDispatcher(List<UpdateHandler> updateHandlers) {
        this.updateHandlers = updateHandlers;
    }

    /**
     * Определяет и вызывает обработчик для обновления
     * @param update входящее сообщение
     * @return список сформированных ответных сообщений
     */
    public List<SendMessage> dispatch(Update update) {
        return updateHandlers.stream()
                .filter(handler -> handler.canHandle(update))
                .findFirst()
                .map(handler -> handler.process(update))
                .orElseGet(() -> {
                    logger.error("unhandled update: {}", update);
                    return List.of();
                });
    }
}
