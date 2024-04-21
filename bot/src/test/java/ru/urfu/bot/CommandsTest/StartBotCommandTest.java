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
import ru.urfu.bot.domain.handlers.bot.StartBotCommand;
import ru.urfu.bot.domain.services.UserBookService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class StartBotCommandTest {
    String username;
    long chatId = 1;
    long isbn = 1;
    Message message;
    Chat chat;
    String text;

    StartBotCommand startBotCommand;
    UserBookService userBookService;

    Book book;
    Update update;



    @BeforeEach
    void init(){
        userBookService = mock(UserBookService.class);
        startBotCommand = new StartBotCommand(userBookService);
        book = mock(Book.class);
        update = mock(Update.class);
        username = "username";
        chat = mock(Chat.class);
        message = mock(Message.class);
        text = "/start";

        Mockito.when(message.getChat()).thenReturn(chat);
        Mockito.when(message.getChatId()).thenReturn(chatId);
        Mockito.when(chat.getUserName()).thenReturn(username);
        Mockito.when(update.getMessage()).thenReturn(message);
        Mockito.when(message.getText()).thenReturn(text);
    }

    @Test
    void startBotTest(){
        Mockito.when(userBookService.findBookByIsbn(isbn)).thenReturn(book);


        SendMessage actual_msg = startBotCommand.handle(update);

        verify(userBookService).addChat(username, chatId);

        SendMessage expected_msg = new SendMessage();
        expected_msg.setChatId(update.getMessage().getChatId());
        expected_msg.setText("Bot started");

        Assertions.assertEquals(expected_msg, actual_msg);

    }
}
