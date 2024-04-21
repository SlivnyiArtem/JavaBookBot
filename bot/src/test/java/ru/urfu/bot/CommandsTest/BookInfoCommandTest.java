package ru.urfu.bot.CommandsTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.domain.entities.Book;
import ru.urfu.bot.domain.handlers.books.AddBookCommand;
import ru.urfu.bot.domain.handlers.books.BookInfoCommand;
import ru.urfu.bot.domain.services.UserBookService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BookInfoCommandTest {
    String username;
    long chatId = 1;
    long isbn = 1;
    Message message;
    Chat chat;
    String text;

    BookInfoCommand addBookCommand;
    UserBookService userBookService;

    Book book;
    Update update;



    @BeforeEach
    void init(){
        userBookService = mock(UserBookService.class);
        addBookCommand = new BookInfoCommand(userBookService);
        book = mock(Book.class);
        update = mock(Update.class);
        username = "username";
        chat = mock(Chat.class);
        message = mock(Message.class);
        text = "/book_inf 1";

        Mockito.when(message.getChat()).thenReturn(chat);
        Mockito.when(message.getChatId()).thenReturn(chatId);
        Mockito.when(chat.getUserName()).thenReturn(username);
        Mockito.when(update.getMessage()).thenReturn(message);
        Mockito.when(message.getText()).thenReturn(text);
    }

    @Test
    void sendCorrectInfo(){
        String desc = "";
        String auth = "";
        String pbl = "";
        String title = "";
        LocalDate date = mock(LocalDate.class);
        Mockito.when(userBookService.getUserBooks(username)).thenReturn(List.of(book));
        Mockito.when(book.getIsbn13()).thenReturn(isbn);
        Mockito.when(book.getDescription()).thenReturn(desc);
        Mockito.when(book.getAuthors()).thenReturn(auth);
        Mockito.when(book.getPublisher()).thenReturn(pbl);
        Mockito.when(book.getTitle()).thenReturn(title);
        Mockito.when(book.getPublishedDate()).thenReturn(date);


        SendMessage actual_msg = addBookCommand.handle(update);

        verify(userBookService).getUserBooks(username);

        String msgText = "isbn: %d\nНазвание: %s\nОписание: %s\nАвторы: %s\nИздатель: %s\nДата издания: %s\n\n"
                .formatted(isbn, title, desc, auth, pbl, date);

        SendMessage expected_msg = new SendMessage();
        expected_msg.setChatId(update.getMessage().getChatId());
        expected_msg.setText(msgText);

        Assertions.assertEquals(expected_msg, actual_msg);

    }

    @Test
    void bookNotFoundInfo(){
        Mockito.when(userBookService.getUserBooks(username)).thenThrow(new NoSuchElementException());

        SendMessage actual_msg = addBookCommand.handle(update);
        verify(userBookService).getUserBooks(username);

        String msgText = "В списке избранного отсутствует книга с данным ISBN: %d".formatted(1L);

        SendMessage expected_msg = new SendMessage();
        expected_msg.setChatId(update.getMessage().getChatId());
        expected_msg.setText(msgText);

        Assertions.assertEquals(expected_msg, actual_msg);
    }
}
