package ru.urfu.bot.CommandsTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.domain.entities.Book;
import ru.urfu.bot.domain.handlers.books.AddBookCommand;
import ru.urfu.bot.domain.handlers.books.SearchBookCommand;
import ru.urfu.bot.domain.services.UserBookService;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.*;

public class SearchBookCommandTest {

    String username;
    long chatId = 1;
    long isbn = 1;
    Message message;
    Chat chat;
    String text;

    SearchBookCommand searchBookCommand;
    UserBookService userBookService;

    Book book1;
    Book book2;
    Update update;



    @BeforeEach
    void init(){
        userBookService = mock(UserBookService.class);
        searchBookCommand = new SearchBookCommand(userBookService);
        book1 = mock(Book.class);
        book2 = mock(Book.class);
        update = mock(Update.class);
        username = "username";
        chat = mock(Chat.class);
        message = mock(Message.class);
        text = "/search_book title";




        Mockito.when(message.getChat()).thenReturn(chat);
        Mockito.when(message.getChatId()).thenReturn(chatId);
        Mockito.when(chat.getUserName()).thenReturn(username);
        Mockito.when(update.getMessage()).thenReturn(message);
        Mockito.when(message.getText()).thenReturn(text);
    }

    @Test
    void searchBookTest(){
        String auth1 = "auth1";
        String pbl1 = "pbl1";
        String title1 = "title1";
        LocalDate date1 = mock(LocalDate.class);

        String auth2 = "auth2";
        String pbl2 = "pbl2";
        String title2 = "title2";
        LocalDate date2 = mock(LocalDate.class);


        Mockito.when(book1.getIsbn13()).thenReturn(1L);
        Mockito.when(book1.getTitle()).thenReturn(title1);
        Mockito.when(book1.getAuthors()).thenReturn(auth1);
        Mockito.when(book1.getPublisher()).thenReturn(pbl1);
        Mockito.when(book1.getPublishedDate()).thenReturn(date1);

        Mockito.when(book2.getIsbn13()).thenReturn(2L);
        Mockito.when(book2.getTitle()).thenReturn(title2);
        Mockito.when(book2.getAuthors()).thenReturn(auth2);
        Mockito.when(book2.getPublisher()).thenReturn(pbl2);
        Mockito.when(book2.getPublishedDate()).thenReturn(date2);


        Mockito.when(userBookService.findBooksByTitle("title")).thenReturn(List.of(book1, book2));


        SendMessage actual_msg = searchBookCommand.handle(update);

        var text = """
                Результаты поиска:
                isbn: %d
                Название: %s
                Авторы: %s
                Издатель: %s
                Дата издания: %s
                /add_book %s                
                
                isbn: %d
                Название: %s
                Авторы: %s
                Издатель: %s
                Дата издания: %s
                /add_book %s
                
                """.formatted(book1.getIsbn13(), book1.getTitle(), book1.getAuthors(), book1.getPublisher(), book1.getPublishedDate(), book1.getIsbn13(),
                book2.getIsbn13(), book2.getTitle(), book2.getAuthors(), book2.getPublisher(), book2.getPublishedDate(), book2.getIsbn13());
        SendMessage expected_msg = new SendMessage();
        expected_msg.setChatId(update.getMessage().getChatId());
        expected_msg.setText(text);

        Assertions.assertEquals(expected_msg, actual_msg);

    }

    @Test
    void WhenNoBooksTest(){
        Mockito.when(userBookService.findBooksByTitle("title")).thenReturn(List.of());


        SendMessage actual_msg = searchBookCommand.handle(update);

        var text = "Результаты поиска:\n";
        SendMessage expected_msg = new SendMessage();
        expected_msg.setChatId(update.getMessage().getChatId());
        expected_msg.setText(text);

        Assertions.assertEquals(expected_msg, actual_msg);
    }
}
