package ru.urfu.bot.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.urfu.bot.controllers.GoogleBooksApiClient;
import ru.urfu.bot.db.entities.Book;
import ru.urfu.bot.db.repositories.JpaBookRepository;
import ru.urfu.bot.utils.MessageConst;
import ru.urfu.bot.utils.dto.SendScheduledMessage;

import java.time.LocalDate;
import java.time.OffsetTime;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Сервис переодически в отдельных потоках независимо выпольняет две задачи:
 * <ul>
 *     <li>Проверяет не наступила ли дата выхода для каждой книги, сохраненной в бд</li>
 *     <li>Обновляет устаревшую информацию о книгах в бд из внешнего сервиса</li>
 * </ul>
 */
@Service
public class SchedulerService {

    private static final Logger LOG = LoggerFactory.getLogger(SchedulerService.class);

    private final BlockingQueue<SendMessage> sendQueue;

    public SchedulerService(JpaBookRepository bookRepository, GoogleBooksApiClient booksApiClient, QueueProvider queueProvider) {
        this.bookRepository = bookRepository;
        this.booksApiClient = booksApiClient;
        this.sendQueue = queueProvider.getSendQueue();
    }

    private final JpaBookRepository bookRepository;

    private final GoogleBooksApiClient booksApiClient;

    /**
     * Переодически отправляет сообщения подписчикам книги, если она вышла
     */
    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.DAYS)
    @Transactional
    public void checkReleaseDate() {
        LOG.trace("check if book released");
        bookRepository.findAll().stream()
                .filter(book -> book.getPublishedDate() != null
                        && book.getPublishedDate().equals(LocalDate.now()))
                .flatMap(book -> book.getUsers().stream()
                        .flatMap(user -> user.getChats().stream()
                                .map(chat ->
                                        getReleaseMessage(chat.getId().toString(), user.getScheduledTime(), book))))
                .forEach(message -> {
                    try {
                        sendQueue.put(message);
                    } catch (InterruptedException e) {
                        LOG.error(e.getMessage());
                    }
                });
    }

    private SendMessage getReleaseMessage(String chatId, OffsetTime scheduledTime, Book book) {
        return new SendScheduledMessage(
                chatId,
                MessageConst.BOOK_RELEASE.formatted(book.getTitle(), book.getIsbn13()),
                scheduledTime.atDate(LocalDate.now(scheduledTime.getOffset()))
        );
    }

    /**
     * Переодически проверяет, не устарела ли иноформация о книгах. Если да, то
     * сохраняет новую информацию в бд и отправляет уведомление подписчикам
     */
    @Scheduled(fixedRateString = "#{ @'app-ru.urfu.bot.config.BotProperties'.updateInfoDelay() }")
    @Transactional
    public void checkUpdateInfo() {
        LOG.trace("check if book info updated");
        bookRepository.findAll().stream()
                .filter(book -> {
                    Optional<Book> apiBook = booksApiClient.findBookByIsbn(book.getIsbn13());
                    if (apiBook.isPresent() && !apiBook.get().equals(book)) {
                        bookRepository.save(apiBook.get());
                        return true;
                    }
                    return false;
                })
                .flatMap(book -> book.getUsers().stream()
                        .flatMap(user -> user.getChats().stream()
                                .map(chat ->
                                        getUpdateInfoMessage(chat.getId().toString(), user.getScheduledTime(), book))))
                .forEach(message -> {
                    try {
                        sendQueue.put(message);
                    } catch (InterruptedException e) {
                        LOG.error(e.getMessage());
                    }
                });
    }

    private SendMessage getUpdateInfoMessage(String chatId, OffsetTime scheduledTime, Book book) {
        return new SendScheduledMessage(
                chatId,
                MessageConst.BOOK_UPDATE_INFO.formatted(book.getTitle(), book.getIsbn13()),
                scheduledTime.atDate(LocalDate.now(scheduledTime.getOffset()))
        );
    }
}
