package ru.urfu.bot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.app.port.UserMessageProcessor;
import ru.urfu.bot.domain.entities.Book;
import ru.urfu.bot.domain.handlers.Command;
import ru.urfu.bot.domain.handlers.books.AddBookCommand;
import ru.urfu.bot.domain.handlers.bot.HelpBotCommand;
import ru.urfu.bot.domain.services.UserBookService;

import java.util.Map;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.*;

public class UpdateTest {
    String username;
    long chatId = 1;
    Message message;
    Chat chat;
    String text;
    UserBookService userBookService;
    Command command;
    UserMessageProcessor userMessageProcessor;
    Update update;



    @BeforeEach
    void init(){

        command = mock(Command.class);
        update = mock(Update.class);
        username = "username";
        chat = mock(Chat.class);
        message = mock(Message.class);
        userBookService = mock(UserBookService.class);

        Mockito.when(message.getChat()).thenReturn(chat);
        Mockito.when(message.getChatId()).thenReturn(chatId);
        Mockito.when(chat.getUserName()).thenReturn(username);
        Mockito.when(update.getMessage()).thenReturn(message);
        Mockito.when(message.getText()).thenReturn(text);
    }

    @Test
    void unknownCommandTest(){
        //TODO
//        Map<String, Command> commandMap = Map.ofEntries(Map.entry("/help", new HelpBotCommand(userBookService)));
//        userMessageProcessor = mock(UserMessageProcessor.class, withSettings().useConstructor(commandMap));
////        Mockito.when(userMessageProcessor.commands.get(command)).thenReturn(null);
//
//        text = "/unkn 1";
//        String msgText = "Команда недоступна";
//
//        SendMessage expected_msg = new SendMessage();
//        expected_msg.setChatId(update.getMessage().getChatId());
//        expected_msg.setText(msgText);
//
//        SendMessage actual_msg = userMessageProcessor.process(update);
//
//        Assertions.assertEquals(expected_msg, actual_msg);

    }

    @Test
    void unsupportedCommandTest(){
        //TODO
////        userMessageProcessor = mock(UserMessageProcessor.class, withSettings().useConstructor(userService, searchService));
//
//        text = "/unsp 1";
//        String text = "Неизвестная команда. Введите /help для получения списка команд";
//
//        SendMessage expected_msg = new SendMessage();
//        expected_msg.setChatId(update.getMessage().getChatId());
//        expected_msg.setText(text);
//
//        SendMessage actual_msg = userMessageProcessor.process(update);
//
//        Assertions.assertEquals(expected_msg, actual_msg);
    }

    @Test
    void supportedCommandTest(){
        //TODO
//        verify(command).handle(update);
    }
}
