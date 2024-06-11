package ru.urfu.bot.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.urfu.bot.service.BookTrackingService;
import ru.urfu.bot.utils.MessageConst;

import java.time.LocalDate;
import java.util.Timer;
import java.util.Date;
import java.util.TimerTask;

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

    private final BookTrackingService bookTrackingService;

    private final BaseAbilityBot abilityBot;

    public BookWorker(BookTrackingService bookTrackingService, BaseAbilityBot abilityBot) {
        this.bookTrackingService = bookTrackingService;
        this.abilityBot = abilityBot;
    }

    /**
     * Переодически отправляет сообщения подписчикам книги, если она вышла
     */
    @Scheduled(fixedDelayString = "#{ @scheduler.checkReleaseDelay() }")
    @Transactional
    public void checkReleaseDate() {
        logger.trace("check if book released");
        bookTrackingService.getReleasedBook(LocalDate.now())
                .forEach(book -> book.getUsers()
                        .forEach(user -> user.getChats()
                                .forEach(chat -> {
                                    new Timer().schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            String message = MessageConst.BOOK_RELEASED_MESSAGE.formatted(book.getTitle(), book.getIsbn());
                                            abilityBot.getSilent().send(message, chat.getId());
                                        }
                                    }, new Date(user.getScheduledTime().toEpochSecond(LocalDate.now())));
                                })));
    }

    /**
     * Переодически проверяет, не устарела ли иноформация о книгах. Если да, то
     * сохраняет новую информацию в бд и отправляет уведомление подписчикам
     */
    @Scheduled(fixedDelayString = "#{ @scheduler.updateInfoDelay() }")
    @Transactional
    public void checkUpdateInfo() {
        logger.trace("check if book info updated");
        bookTrackingService.getUpdatedBook()
                .forEach(book -> book.getUsers()
                        .forEach(user -> user.getChats()
                                .forEach(chat -> {
                                    new Timer().schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            String message = MessageConst.BOOK_UPDATED_MESSAGE.formatted(book.getTitle(), book.getIsbn());
                                            abilityBot.getSilent().send(message, chat.getId());
                                        }
                                    }, new Date(user.getScheduledTime().toEpochSecond(LocalDate.now())));
                                })));
    }
}
