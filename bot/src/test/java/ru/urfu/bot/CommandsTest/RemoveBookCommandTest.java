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
import ru.urfu.bot.domain.handlers.books.RemoveBookCommand;
import ru.urfu.bot.domain.services.UserBookService;

import java.util.NoSuchElementException;

import static org.mockito.Mockito.*;

public class RemoveBookCommandTest {

    String username;
    long chatId = 1;
    long isbn = 1;
    Message message;
    Chat chat;
    String text;

    RemoveBookCommand removeBookCommand;
    UserBookService userBookService;

    Book book;
    Update update;



    @BeforeEach
    void init(){
        userBookService = mock(UserBookService.class);
        removeBookCommand = new RemoveBookCommand(userBookService);
        book = mock(Book.class);
        update = mock(Update.class);
        username = "username";
        chat = mock(Chat.class);
        message = mock(Message.class);
        text = "/remove_book 1";

        Mockito.when(message.getChat()).thenReturn(chat);
        Mockito.when(message.getChatId()).thenReturn(chatId);
        Mockito.when(chat.getUserName()).thenReturn(username);
        Mockito.when(update.getMessage()).thenReturn(message);
        Mockito.when(message.getText()).thenReturn(text);
    }

    @Test
    void removeBookTest(){
        Mockito.when(userBookService.findBookByIsbn(isbn)).thenReturn(book);


        SendMessage actual_msg = removeBookCommand.handle(update);

        verify(userBookService).removeBook(username, book);

        SendMessage expected_msg = new SendMessage();
        expected_msg.setChatId(update.getMessage().getChatId());
        expected_msg.setText("Книга удаленна из избранных");

        Assertions.assertEquals(expected_msg, actual_msg);

    }

    @Test
    void notRemoveWhenIncorrectMessage(){
        Mockito.when(userBookService.findBookByIsbn(isbn)).thenThrow(new NoSuchElementException());

        SendMessage actual_msg = removeBookCommand.handle(update);

        verify(userBookService, never()).addBook(username, book);

        SendMessage expected_msg = new SendMessage();
        expected_msg.setChatId(update.getMessage().getChatId());
        expected_msg.setText("Книга не найдена");

        Assertions.assertEquals(expected_msg, actual_msg);
    }
}
