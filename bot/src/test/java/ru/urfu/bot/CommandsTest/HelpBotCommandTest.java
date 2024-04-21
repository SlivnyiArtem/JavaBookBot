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
import ru.urfu.bot.domain.handlers.bot.HelpBotCommand;
import ru.urfu.bot.domain.handlers.bot.StartBotCommand;
import ru.urfu.bot.domain.services.UserBookService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class HelpBotCommandTest {
    String username;
    long chatId = 1;
    long isbn = 1;
    Message message;
    Chat chat;
    String text;

    HelpBotCommand helpBotCommand;
    UserBookService userBookService;

    Book book;
    Update update;



    @BeforeEach
    void init(){
        userBookService = mock(UserBookService.class);
        helpBotCommand = new HelpBotCommand(userBookService);
        book = mock(Book.class);
        update = mock(Update.class);
        username = "username";
        chat = mock(Chat.class);
        message = mock(Message.class);
        text = "/help";

        Mockito.when(message.getChat()).thenReturn(chat);
        Mockito.when(message.getChatId()).thenReturn(chatId);
        Mockito.when(chat.getUserName()).thenReturn(username);
        Mockito.when(update.getMessage()).thenReturn(message);
        Mockito.when(message.getText()).thenReturn(text);
    }

    @Test
    void helpBotTest(){
        Mockito.when(userBookService.findBookByIsbn(isbn)).thenReturn(book);


        SendMessage actual_msg = helpBotCommand.handle(update);

//        verify(userBookService).addChat(username, chatId);

        var text = """
                /start - начать работать с ботом
                /search_book {title} - поиск книги по названию
                /my_books - вывести список книг
                /add_book {isbn} - добавить книгу
                /remove_book {isbn} - убрать книгу
                /help - помощь
                /book_inf {isbn} - иинформация о книге
                """;

        SendMessage expected_msg = new SendMessage();
        expected_msg.setChatId(update.getMessage().getChatId());
        expected_msg.setText(text);

        Assertions.assertEquals(expected_msg, actual_msg);

    }
}
