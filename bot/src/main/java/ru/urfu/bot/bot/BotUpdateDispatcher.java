package ru.urfu.bot.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.handler.UpdateHandler;
import ru.urfu.bot.utils.MessageConst;

import java.util.List;
import java.util.Optional;

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
                .orElseGet(() -> handleUnknownUpdate(update).map(List::of).orElse(List.of()));
    }

    private Optional<SendMessage> handleUnknownUpdate(Update update) {
        try {
            if (update.hasCallbackQuery()) {
                String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
                String data = update.getCallbackQuery().getData();
                logger.warn("unknown callback received: %s".formatted(data));
                return Optional.of(new SendMessage(chatId, MessageConst.INTERNAL_SERVER_ERROR));
            } else if (update.hasMessage() && update.getMessage().hasText()) {
                String chatId = update.getMessage().getChatId().toString();
                String data = update.getMessage().getText();
                logger.debug("unknown user command received: %s".formatted(data));
                return Optional.of(new SendMessage(chatId, MessageConst.UNKNOWN_COMMAND));
            }
        } catch (NullPointerException ignored) { }
        logger.warn("don't supported update received: %s".formatted(update.toString()));
        return Optional.empty();
    }
}
