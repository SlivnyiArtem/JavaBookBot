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
import ru.urfu.bot.domain.handlers.books.PrintBooksCommand;
import ru.urfu.bot.domain.services.UserBookService;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PrintBooksCommandTest {
    String username;
    long chatId = 1;
    long isbn = 1;
    Message message;
    Chat chat;
    String text;

    PrintBooksCommand printBooksCommand;
    UserBookService userBookService;

    Book book1;
    Book book2;
    Update update;



    @BeforeEach
    void init(){
        userBookService = mock(UserBookService.class);
        printBooksCommand = new PrintBooksCommand(userBookService);
        book1 = mock(Book.class);
        book2 = mock(Book.class);
        update = mock(Update.class);
        username = "username";
        chat = mock(Chat.class);
        message = mock(Message.class);
        text = "/my_books";

        Mockito.when(message.getChat()).thenReturn(chat);
        Mockito.when(message.getChatId()).thenReturn(chatId);
        Mockito.when(chat.getUserName()).thenReturn(username);
        Mockito.when(update.getMessage()).thenReturn(message);
        Mockito.when(message.getText()).thenReturn(text);
    }

    @Test
    void sendCorrectInfo(){
        Mockito.when(book1.getIsbn13()).thenReturn(1L);
        Mockito.when(book1.getTitle()).thenReturn("title1");
        Mockito.when(book2.getIsbn13()).thenReturn(2L);
        Mockito.when(book2.getTitle()).thenReturn("title2");

        Mockito.when(userBookService.getUserBooks(username)).thenReturn(List.of(book1, book2));


        SendMessage actual_msg = printBooksCommand.handle(update);

        verify(userBookService).getUserBooks(username);

        String msgText = "Книги в избранном:\nisbn: %d\nНазвание: %s\n\nisbn: %d\nНазвание: %s\n\n"
                .formatted(1L, "title1", 2L, "title2");

        SendMessage expected_msg = new SendMessage();
        expected_msg.setChatId(update.getMessage().getChatId());
        expected_msg.setText(msgText);

        Assertions.assertEquals(expected_msg, actual_msg);
    }
}
