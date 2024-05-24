package ru.urfu.bot.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.handler.UpdateHandler;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Воркер для определения и выполнения команд.
 */
@Component
public class DispatchUpdateWorker {

    private final List<UpdateHandler> updateHandlers;

    private final BlockingQueue<BotApiMethodMessage> sendQueue;

    private final BlockingQueue<Update> receivedQueue;

    private final Logger logger = LoggerFactory.getLogger(DispatchUpdateWorker.class);

    /**
     * @param updateHandlers упорядоченный список обработчиков
     * @param sendQueue очередь сообщений на отправку
     * @param receivedQueue очередь обновлений на обработку
     */
    public DispatchUpdateWorker(List<UpdateHandler> updateHandlers,
                                BlockingQueue<BotApiMethodMessage> sendQueue, BlockingQueue<Update> receivedQueue) {
        this.updateHandlers = updateHandlers;
        this.sendQueue = sendQueue;
        this.receivedQueue = receivedQueue;
    }

    /**
     * Определяет и вызывает обработчик для обновления. Помещает ответ в очередь на отправку.
     */
    @Scheduled(fixedDelayString = "#{ @scheduler.receivedProcessDelay() }")
    @Transactional
    public void dispatch() {
        Update update;
        try {
            update = receivedQueue.take();
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            return;
        }
        updateHandlers.stream()
                .filter(handler -> handler.canHandle(update))
                .findFirst()
                .map(handler -> handler.process(update))
                .orElseGet(() -> {
                    logger.error("unhandled update: {}", update);
                    return List.of();
                })
                .forEach(sendMessage -> {
                    try {
                        sendQueue.put(sendMessage);
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage());
                    }
                });
    }
}
