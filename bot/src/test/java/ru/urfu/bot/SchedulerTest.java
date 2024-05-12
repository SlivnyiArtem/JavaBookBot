package ru.urfu.bot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.urfu.bot.controllers.GoogleBooksApiClient;
import ru.urfu.bot.db.entities.Book;
import ru.urfu.bot.db.entities.Chat;
import ru.urfu.bot.db.entities.User;
import ru.urfu.bot.db.repositories.JpaBookRepository;
import ru.urfu.bot.services.QueueProvider;
import ru.urfu.bot.services.SchedulerService;

import java.time.LocalDate;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SchedulerTest {

    @Mock
    private GoogleBooksApiClient booksApiClient;

    @Mock
    private JpaBookRepository bookRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private QueueProvider queueProvider;

    @InjectMocks
    private SchedulerService schedulerService;

    private final OffsetTime scheduledTime =
            OffsetTime.of(10, 0, 0, 0, ZoneOffset.ofHours(0));

    @BeforeEach
    public void init() {
        Chat chat1 = new Chat(1L);
        Chat chat2 = new Chat(2L);
        Chat chat3 = new Chat(3L);

        User user1 = new User("user1");
        User user2 = new User("user2");
        user1.setScheduledTime(scheduledTime);
        user2.setScheduledTime(scheduledTime);

        user1.getChats().add(chat1);
        user2.getChats().add(chat2);
        user2.getChats().add(chat3);

        Book book1 = new Book();
        book1.setIsbn13(1L);
        book1.setTitle("book1");
        book1.setPublishedDate(LocalDate.of(2000, 1, 2));
        Book book2 = new Book();
        book2.setIsbn13(2L);
        book2.setTitle("book2");
        book2.setPublishedDate(LocalDate.of(2000, 1, 2));
        Book book3 = new Book();
        book3.setIsbn13(3L);
        book3.setTitle("book3");
        book3.setPublishedDate(LocalDate.of(2000, 1, 2));
        Book book4 = new Book();
        book4.setIsbn13(4L);
        book4.setTitle("book4");
        book4.setPublishedDate(LocalDate.of(2000, 1, 1));
        Book book5 = new Book();
        book5.setIsbn13(5L);
        book5.setTitle("book5");

        book1.addUser(user1);
        book1.addUser(user2);
        book2.addUser(user1);
        book2.addUser(user2);
        book4.addUser(user2);
        book5.addUser(user2);

        when(bookRepository.findAll()).thenReturn(List.of(book1, book2, book3, book4, book5));
    }

    /**
     * Проверяется:
     * <ol>
     *     <li>Сообщение о выходе книги отправляется во все чаты всех подписанных пользователей</li>
     *     <li>Для книги без подписчиков уведомление не отправляется</li>
     *     <li>Если дата выхода не равна текущей или равна null, то уведомление не отправляется</li>
     * </ol>
     */
    @Test
    public void checkReleaseTest() throws InterruptedException {
        LocalDate now = LocalDate.of(2000, 1, 2);

        try (MockedStatic<LocalDate> localDate =  mockStatic(LocalDate.class)) {
            localDate
                    .when(() -> LocalDate.now(eq(scheduledTime.getOffset())))
                    .thenReturn(now);
            localDate
                    .when(LocalDate::now)
                    .thenReturn(now);

            schedulerService.checkReleaseDate();
            verify(queueProvider.getSendQueue()).put(eq(new SendMessage("1", "Книга book1 (isbn: 1) вышла")));
            verify(queueProvider.getSendQueue()).put(eq(new SendMessage("2", "Книга book1 (isbn: 1) вышла")));
            verify(queueProvider.getSendQueue()).put(eq(new SendMessage("3", "Книга book1 (isbn: 1) вышла")));
            verify(queueProvider.getSendQueue()).put(eq(new SendMessage("3", "Книга book2 (isbn: 2) вышла")));
            verifyNoMoreInteractions(queueProvider.getSendQueue());
        }
    }

    /**
     * Проверяется:
     * <ol>
     *     <li>
     *         Если у книги изменились поля, кроме users, то отправляеся уведомление всем
     *         подписчикам; новая информация сохраняется в бд
     *     </li>
     *     <li>
     *         Если информация не изменилась иле api не находит книгу, то книга не сохраняется
     *         и уведомление не отправляется
     *     </li>
     * </ol>
     */
    @Test
    public void checkInfoTest() throws InterruptedException {
        Book newBook1 = new Book();
        newBook1.setIsbn13(1L);
        newBook1.setTitle("book1");
        newBook1.setPublishedDate(LocalDate.of(2000, 1, 2));

        Book newBook2 = new Book();
        newBook2.setIsbn13(2L);
        newBook2.setTitle("book2");
        newBook2.setPublishedDate(LocalDate.of(2006, 1, 2));

        when(booksApiClient.findBookByIsbn(eq(1L))).thenReturn(Optional.of(newBook1));
        when(booksApiClient.findBookByIsbn(eq(2L))).thenReturn(Optional.of(newBook2));
        when(booksApiClient.findBookByIsbn(eq(3L))).thenReturn(Optional.empty());
        when(booksApiClient.findBookByIsbn(eq(4L))).thenReturn(Optional.empty());
        when(booksApiClient.findBookByIsbn(eq(5L))).thenReturn(Optional.empty());

        schedulerService.checkUpdateInfo();

        verify(bookRepository, times(1)).save(eq(newBook2));
        verify(bookRepository, never()).save(eq(newBook1));
        verify(queueProvider.getSendQueue()).put(eq(new SendMessage("1", "Информация о книге book2 (isbn: 2) обновленна")));
        verify(queueProvider.getSendQueue()).put(eq(new SendMessage("2", "Информация о книге book2 (isbn: 2) обновленна")));
        verify(queueProvider.getSendQueue()).put(eq(new SendMessage("3", "Информация о книге book2 (isbn: 2) обновленна")));
        verifyNoMoreInteractions(queueProvider.getSendQueue());
    }
}
