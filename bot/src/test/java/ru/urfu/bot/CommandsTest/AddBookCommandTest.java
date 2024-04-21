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
import ru.urfu.bot.domain.services.UserBookService;

import java.util.NoSuchElementException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AddBookCommandTest {

    String username;
    long chatId = 1;
    long isbn = 1;
    Message message;
    Chat chat;
    String text;

    AddBookCommand addBookCommand;
    UserBookService userBookService;

    Book book;
    Update update;



    @BeforeEach
    void init(){
        userBookService = mock(UserBookService.class);
        addBookCommand = new AddBookCommand(userBookService);
        book = mock(Book.class);
        update = mock(Update.class);
        username = "username";
        chat = mock(Chat.class);
        message = mock(Message.class);
        text = "/add_book 1";

        Mockito.when(message.getChat()).thenReturn(chat);
        Mockito.when(message.getChatId()).thenReturn(chatId);
        Mockito.when(chat.getUserName()).thenReturn(username);
        Mockito.when(update.getMessage()).thenReturn(message);
        Mockito.when(message.getText()).thenReturn(text);
    }

    @Test
    void addBookTest(){
        Mockito.when(userBookService.findBookByIsbn(isbn)).thenReturn(book);


        SendMessage actual_msg = addBookCommand.handle(update);

        verify(userBookService).addBook(username, book);

        SendMessage expected_msg = new SendMessage();
        expected_msg.setChatId(update.getMessage().getChatId());
        expected_msg.setText("Книга добавленна в избранное");

        Assertions.assertEquals(expected_msg, actual_msg);

    }

    @Test
    void notTestWhenIncorrectMessage(){
        Mockito.when(userBookService.findBookByIsbn(isbn)).thenThrow(new NoSuchElementException());

        SendMessage actual_msg = addBookCommand.handle(update);

        verify(userBookService, never()).addBook(username, book);

        SendMessage expected_msg = new SendMessage();
        expected_msg.setChatId(update.getMessage().getChatId());
        expected_msg.setText("Книга не найдена");

        Assertions.assertEquals(expected_msg, actual_msg);
    }

}
