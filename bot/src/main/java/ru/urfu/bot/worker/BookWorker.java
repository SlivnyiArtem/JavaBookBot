package ru.urfu.bot.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.urfu.bot.domain.Book;
import ru.urfu.bot.service.BookTrackingService;
import ru.urfu.bot.utils.MessageConst;
import ru.urfu.bot.utils.SendScheduledMessage;

import java.time.LocalDate;
import java.time.OffsetTime;
import java.util.concurrent.BlockingQueue;

/**
 * Воркер переодически в отдельных потоках независимо выполняет две задачи:
 * <ul>
 *     <li>Проверяет не наступила ли дата выхода для каждой книги, сохраненной в бд</li>
 *     <li>Обновляет устаревшую информацию о книгах в бд из внешнего сервиса</li>
 * </ul>
 */
@Component
public class BookWorker {

    private final Logger logger = LoggerFactory.getLogger(BookWorker.class);

    private final BlockingQueue<BotApiMethodMessage> sendQueue;

    public BookWorker(BookTrackingService bookTrackingService, BlockingQueue<BotApiMethodMessage> sendQueue) {
        this.bookTrackingService = bookTrackingService;
        this.sendQueue = sendQueue;
    }

    private final BookTrackingService bookTrackingService;

    /**
     * Переодически отправляет сообщения подписчикам книги, если она вышла
     */
    @Scheduled(fixedDelayString = "#{ @scheduler.checkReleaseDelay() }")
    @Transactional
    public void checkReleaseDate() {
        logger.trace("check if book released");
        bookTrackingService.getReleasedBook(LocalDate.now()).stream()
                .flatMap(book -> book.getUsers().stream()
                        .flatMap(user -> user.getChats().stream()
                                .map(chat ->
                                        getReleaseMessage(chat.getId().toString(), user.getScheduledTime(), book))))
                .forEach(message -> {
                    try {
                        sendQueue.put(message);
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage());
                    }
                });
    }

    private BotApiMethodMessage getReleaseMessage(String chatId, OffsetTime scheduledTime, Book book) {
        SendMessage sendMessage = new SendMessage(chatId,
                MessageConst.BOOK_RELEASE.formatted(book.getTitle(), book.getIsbn()));
        return new SendScheduledMessage(
                sendMessage,
                scheduledTime.atDate(LocalDate.now(scheduledTime.getOffset()))
        );
    }

    /**
     * Переодически проверяет, не устарела ли иноформация о книгах. Если да, то
     * сохраняет новую информацию в бд и отправляет уведомление подписчикам
     */
    @Scheduled(fixedDelayString = "#{ @scheduler.updateInfoDelay() }")
    @Transactional
    public void checkUpdateInfo() {
        logger.trace("check if book info updated");
        bookTrackingService.getUpdatedBook().stream()
                .flatMap(book -> book.getUsers().stream()
                        .flatMap(user -> user.getChats().stream()
                                .map(chat ->
                                        getUpdateInfoMessage(chat.getId().toString(), user.getScheduledTime(), book))))
                .forEach(message -> {
                    try {
                        sendQueue.put(message);
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage());
                    }
                });
    }

    private BotApiMethodMessage getUpdateInfoMessage(String chatId, OffsetTime scheduledTime, Book book) {
        SendMessage sendMessage = new SendMessage(chatId,
                MessageConst.BOOK_UPDATE_INFO.formatted(book.getTitle(), book.getIsbn()));
        return new SendScheduledMessage(
                sendMessage,
                scheduledTime.atDate(LocalDate.now(scheduledTime.getOffset()))
        );
    }
}
