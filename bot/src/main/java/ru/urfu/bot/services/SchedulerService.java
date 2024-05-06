package ru.urfu.bot.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.urfu.bot.bot.Bot;
import ru.urfu.bot.controllers.GoogleBooksApiClient;
import ru.urfu.bot.db.entities.Book;
import ru.urfu.bot.db.repositories.JpaBookRepository;
import ru.urfu.bot.utils.MessageConst;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Сервис переодически в отдельном потоке независимо выпольняет две задачи:
 * <ul>
 *     <li>Проверяет не наступила ли дата выхода для каждой книги, сохраненной в бд</li>
 *     <li>Обновляет устаревшую информацию о книгах в бд из внешнего сервиса</li>
 * </ul>
 */
@Service
public class SchedulerService {

    private static final Logger LOG = LoggerFactory.getLogger(SchedulerService.class);

    public SchedulerService(JpaBookRepository bookRepository, GoogleBooksApiClient booksApiClient, Bot bot) {
        this.bookRepository = bookRepository;
        this.booksApiClient = booksApiClient;
        this.bot = bot;
    }

    private final JpaBookRepository bookRepository;

    private final GoogleBooksApiClient booksApiClient;

    private final Bot bot;

    /**
     * Переодически отправляет сообщения подписчикам книги, если она вышла
     */
    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.DAYS)
    @Transactional
    public void checkReleaseDate() {
        bookRepository.findAll().stream()
                .filter(book -> book.getPublishedDate() != null
                        && book.getPublishedDate().equals(LocalDate.now()))
                .flatMap(book -> book.getUsers().stream()
                        .flatMap(user -> user.getChats().stream()
                                .map(chat -> getReleaseMessage(chat.getId().toString(), book))))
                .forEach(message -> {
                    try {
                        bot.execute(message);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                });
        LOG.debug("check if book released");
    }

    private SendMessage getReleaseMessage(String chatId, Book book) {
        return new SendMessage(
                chatId,
                MessageConst.BOOK_RELEASE.formatted(book.getTitle(), book.getIsbn13())
        );
    }

    /**
     * Переодически проверяет, не устарела ли иноформация о книгах. Если да, то
     * сохраняет новую информацию в бд и отправляет уведомление подписчикам
     */
    @Scheduled(fixedRateString = "#{ @'app-ru.urfu.bot.config.BotProperties'.updateInfoDelay() }")
    @Transactional
    public void checkUpdateInfo() {
        bookRepository.findAll().stream()
                .filter(book -> {
                    Optional<Book> book1 = booksApiClient.findBookByIsbn(book.getIsbn13());
                    if (book1.isPresent() && !book1.get().equals(book)) {
                        bookRepository.save(book1.get());
                        return true;
                    }
                    return false;
                })
                .flatMap(book -> book.getUsers().stream()
                        .flatMap(user -> user.getChats().stream()
                                .map(chat -> getUpdateInfoMessage(chat.getId().toString(), book))))
                .forEach(message -> {
                    try {
                        bot.execute(message);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                });
        LOG.debug("check if book info updated");
    }

    private SendMessage getUpdateInfoMessage(String chatId, Book book) {
        return new SendMessage(
                chatId,
                MessageConst.BOOK_UPDATE_INFO.formatted(book.getTitle(), book.getIsbn13())
        );
    }
}
