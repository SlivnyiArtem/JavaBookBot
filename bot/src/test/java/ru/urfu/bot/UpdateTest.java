package ru.urfu.bot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.app.port.UserMessageProcessor;
import ru.urfu.bot.domain.handlers.Command;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;

public class UpdateTest {

    Map<String, Command> commandMap;
    UserMessageProcessor userMessageProcessor;


    @BeforeEach
    void init(){
        Command command1 = mock(Command.class);
        Mockito.when(command1.supports(any())).thenReturn(false);
        Command command2 = mock(Command.class);
        Mockito.when(command2.supports(any())).thenReturn(true);
        Mockito.when(command2.handle(any())).thenReturn(mock(SendMessage.class));
        commandMap = Map.of(
                "/command1", command1,
                "/command2", command2
        );
        userMessageProcessor = new UserMessageProcessor(commandMap);

    }

    private Update mockUpdate(String text) {
        String username = "username";
        Long chatId = 1L;
        Update update = mock(Update.class);
        Chat chat = mock(Chat.class);
        Message message = mock(Message.class);
        Mockito.when(message.getChat()).thenReturn(chat);
        Mockito.when(message.getChatId()).thenReturn(chatId);
        Mockito.when(chat.getUserName()).thenReturn(username);
        Mockito.when(update.getMessage()).thenReturn(message);
        Mockito.when(message.getText()).thenReturn(text);
        return update;
    }

    @Test
    void unknownCommandTest(){
        // Arrange
        Update update = mockUpdate("/unkn 1");
        String expected_msg = "Неизвестная команда. Введите /help для получения списка команд";

        // Act
        String actual_msg = userMessageProcessor.process(update).getText();

        // Assert
        assertEquals(expected_msg, actual_msg);

    }

    @Test
    void unsupportedCommandTest(){
        // Arrange
        Update update = mockUpdate("/command1");
        String expected_msg = "Команда недоступна";

        // Act
        String actual_msg = userMessageProcessor.process(update).getText();

        // Assert
        assertEquals(expected_msg, actual_msg);
    }

    @Test
    void supportedCommandTest(){
        // Arrange
        Update update = mockUpdate("/command2");
        String expected_msg = "Команда недоступна";

        // Act
        String actual_msg = userMessageProcessor.process(update).getText();

        // Assert
        assertNotEquals(expected_msg, actual_msg);
    }
}
