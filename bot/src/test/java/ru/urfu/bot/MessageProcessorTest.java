package ru.urfu.bot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.services.Parser;
import ru.urfu.bot.services.UserMessageProcessor;
import ru.urfu.bot.handlers.CommandHandler;
import ru.urfu.bot.utils.MessageConst;
import ru.urfu.bot.utils.dto.Command;
import ru.urfu.bot.utils.dto.CommandType;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageProcessorTest {

    String username = "username";

    Long chatId = 1L;

    Map<CommandType, CommandHandler> commandMap;

    Map<CommandType, CommandHandler> callbackMap;

    UserMessageProcessor userMessageProcessor;

    @Mock
    Parser parser;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    Update update;

    @Mock
    CommandHandler commandHandler;

    @BeforeEach
    void init(){
        commandMap = Map.of(
                CommandType.UNKNOWN, commandHandler
        );
        callbackMap = Map.of(
                CommandType.UNKNOWN, commandHandler
        );

        userMessageProcessor = new UserMessageProcessor(commandMap, callbackMap, parser);
    }

    @Test
    void unsupportedCommandTest() {
        // Arrange
        when(commandHandler.supports(any())).thenReturn(false);

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage().hasText()).thenReturn(true);
        when(update.getMessage().getText()).thenReturn("/");

        when(parser.parseCommand(any())).thenReturn(new Command(CommandType.UNKNOWN, ""));

        // Act
        List<SendMessage> actual_msg = userMessageProcessor.process(update);

        // Assert
        assertTrue(actual_msg.isEmpty());
    }

    @Test
    void messageUpdateTest(){
        // Arrange
        when(commandHandler.supports(any())).thenReturn(true);
        when(commandHandler.handle(any(), any())).thenReturn(List.of(mock(SendMessage.class)));

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage().hasText()).thenReturn(true);
        when(update.getMessage().getText()).thenReturn("/");

        when(parser.parseCommand(any())).thenReturn(new Command(CommandType.UNKNOWN, ""));

        // Act
        List<SendMessage> actual_msg = userMessageProcessor.process(update);

        // Assert
        assertFalse(actual_msg.isEmpty());
    }

    @Test
    void callbackUpdateTest() {
        // Arrange
        when(commandHandler.handle(any(), any())).thenReturn(List.of(mock(SendMessage.class)));

        when(update.hasCallbackQuery()).thenReturn(true);

        when(parser.parseCallback(update)).thenReturn(new Command(CommandType.UNKNOWN, ""));

        // Act
        List<SendMessage> actual_msg = userMessageProcessor.process(update);

        // Assert
        assertFalse(actual_msg.isEmpty());
    }

    @Test
    void notCommandTest() {
        // Arrange
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage().hasText()).thenReturn(true);
        when(update.getMessage().getText()).thenReturn("message");
        when(update.getMessage().getChatId()).thenReturn(chatId);

        SendMessage expected = SendMessage.builder()
                .chatId(chatId).text(MessageConst.UNKNOWN_COMMAND)
                .build();

        // Act
        List<SendMessage> actual_msg = userMessageProcessor.process(update);

        // Assert
        assertEquals(expected, actual_msg.getFirst());
    }
}
