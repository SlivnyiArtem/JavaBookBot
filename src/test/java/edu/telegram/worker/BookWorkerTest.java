package edu.telegram.worker;

import edu.telegram.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import edu.telegram.domain.Book;
import edu.telegram.domain.Chat;
import edu.telegram.service.BookTrackingService;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.lenient;

/**
 * Тесты на класс {@link BookWorker}
 */
@ExtendWith(MockitoExtension.class)
public class BookWorkerTest {

    @Mock
    private BookTrackingService bookTrackingService;

    private BlockingQueue<BotApiMethodMessage> sendQueue;

    private BookWorker bookWorker;

    @BeforeEach
    public void init() {
        sendQueue = new LinkedBlockingQueue<>();
        bookWorker = new BookWorker(bookTrackingService);
        Chat chat1 = new Chat(1L);
        Chat chat2 = new Chat(2L);
        Chat chat3 = new Chat(3L);

        User user1 = new User("user1");
        User user2 = new User("user2");

        user1.getChats().add(chat1);
        user1.getChats().add(chat2);
        user2.getChats().add(chat3);

        Book book1 = new Book();
        book1.setIsbn(1L);
        book1.setTitle("book1");
        book1.setPublishedDate(LocalDate.of(2000, 1, 2));
        Book book2 = new Book();
        book2.setIsbn(2L);
        book2.setTitle("book2");
        book2.setPublishedDate(LocalDate.of(2000, 1, 2));
        Book book3 = new Book();
        book3.setIsbn(3L);
        book3.setTitle("book3");
        book3.setPublishedDate(LocalDate.of(2000, 1, 2));

        book1.getUsers().add(user1);
        book1.getUsers().add(user2);
        book2.getUsers().add(user2);

        lenient().when(bookTrackingService.getReleasedBook(eq(LocalDate.now()))).thenReturn(List.of(book1, book2, book3));
        lenient().when(bookTrackingService.getUpdatedBook()).thenReturn(List.of(book1, book2, book3));
    }

    /**
     * Проверяется:
     * <ol>
     *     <li>Сообщение о выходе книги отправляется во все чаты всех подписанных пользователей</li>
     *     <li>Для книги без подписчиков уведомление не отправляется</li>
     * </ol>
     */
    @Test
    public void checkReleaseTest() {
        List<SendMessage> expected = List.of(
                new SendMessage("1", "Книга book1 (isbn: 1) вышла"),
                new SendMessage("2", "Книга book1 (isbn: 1) вышла"),
                new SendMessage("3", "Книга book1 (isbn: 1) вышла"),
                new SendMessage("3", "Книга book2 (isbn: 2) вышла"));

        bookWorker.checkReleaseDate();
        List<SendMessage> actual = sendQueue.stream()
                .map(botApiMethodMessage -> {
                    if (botApiMethodMessage instanceof SendMessage message) {
                        return message;
                    } else if (botApiMethodMessage instanceof SendScheduledMessage scheduledMessage
                            && scheduledMessage.getBotApiMethodMessage() instanceof SendMessage message) {
                        return message;
                    } else {
                        throw new RuntimeException("not supported message %s".formatted(botApiMethodMessage));
                    }
                })
                .sorted(Comparator.comparing(SendMessage::getChatId).thenComparing(SendMessage::getText))
                .toList();
        assertEquals(expected, actual);
    }

    /**
     * Проверяется:
     * <ol>
     *     <li>Сообщение об обновлении книги отправляется во все чаты всех подписанных пользователей</li>
     *     <li>Для книги без подписчиков уведомление не отправляется</li>
     * </ol>
     */
    @Test
    public void checkInfoTest() {

        List<SendMessage> expected = List.of(
                new SendMessage("1", "Информация о книге book1 (isbn: 1) обновленна"),
                new SendMessage("2", "Информация о книге book1 (isbn: 1) обновленна"),
                new SendMessage("3", "Информация о книге book1 (isbn: 1) обновленна"),
                new SendMessage("3", "Информация о книге book2 (isbn: 2) обновленна"));

        bookWorker.checkUpdateInfo();
        List<SendMessage> actual = sendQueue.stream()
                .map(botApiMethodMessage -> {
                    if (botApiMethodMessage instanceof SendMessage message) {
                        return message;
                    } else if (botApiMethodMessage instanceof SendScheduledMessage scheduledMessage
                            && scheduledMessage.getBotApiMethodMessage() instanceof SendMessage message) {
                        return message;
                    } else {
                        throw new RuntimeException("not supported message %s".formatted(botApiMethodMessage));
                    }
                })
                .sorted(Comparator.comparing(SendMessage::getChatId).thenComparing(SendMessage::getText))
                .toList();
        assertEquals(expected, actual);
    }
}
