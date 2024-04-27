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
import ru.urfu.bot.services.handlers.CommandHandler;
import ru.urfu.bot.utils.dto.Command;
import ru.urfu.bot.utils.dto.CommandType;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageProcessorTest {

    private final String username = "username";

    private final Long chatId = 1L;

    @Mock
    private Map<CommandType, CommandHandler> commandMap;

    @Mock
    private Map<CommandType, CommandHandler> callbackMap;

    private UserMessageProcessor userMessageProcessor;

    private Command command;

    @Mock
    private Parser parser;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Update update;

    @Mock
    private CommandHandler commandHandler;

    @Mock
    private CommandHandler callbackHandler;

    @BeforeEach
    void init(){
        command = new Command(CommandType.UNKNOWN, "");

        userMessageProcessor = new UserMessageProcessor(commandMap, callbackMap, parser);
    }

    @Test
    void messageUpdateTest(){
        // Arrange
        when(parser.parseCommand(update)).thenReturn(command);

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage().hasText()).thenReturn(true);
        when(update.getMessage().getChat().getUserName()).thenReturn(username);
        when(update.getMessage().getChatId()).thenReturn(chatId);

        when(commandHandler.handle(eq(command), eq(username), eq(Long.toString(chatId))))
                .thenReturn(List.of(new SendMessage()));
        when(commandMap.get(eq(CommandType.UNKNOWN))).thenReturn(commandHandler);
        when(commandMap.get(eq(CommandType.START))).thenReturn(mock(CommandHandler.class));

        // Act
        List<SendMessage> actual_msg = userMessageProcessor.process(update);

        // Assert
        assertFalse(actual_msg.isEmpty());
        verify(commandHandler).handle(command, username, Long.toString(chatId));
        verify(callbackHandler, times(0)).handle(command, username, Long.toString(chatId));
    }

    @Test
    void callbackUpdateTest() {
        // Arrange
        when(parser.parseCallback(update)).thenReturn(command);

        when(update.hasCallbackQuery()).thenReturn(true);
        when(update.getCallbackQuery().getFrom().getUserName()).thenReturn(username);
        when(update.getCallbackQuery().getMessage().getChatId()).thenReturn(chatId);

        when(callbackHandler.handle(eq(command), eq(username), eq(Long.toString(chatId))))
                .thenReturn(List.of(new SendMessage()));
        when(callbackMap.get(eq(CommandType.UNKNOWN))).thenReturn(callbackHandler);
        when(callbackMap.get(eq(CommandType.START))).thenReturn(mock(CommandHandler.class));

        // Act
        List<SendMessage> actual_msg = userMessageProcessor.process(update);

        // Assert
        assertFalse(actual_msg.isEmpty());
        verify(commandHandler, times(0)).handle(command, username, Long.toString(chatId));
        verify(callbackHandler).handle(command, username, Long.toString(chatId));
    }

    @Test
    void notCommandTest() {
        // Arrange
        when(update.hasCallbackQuery()).thenReturn(false);
        when(update.hasMessage()).thenReturn(false);

        // Act
        List<SendMessage> actual_msg = userMessageProcessor.process(update);

        // Assert
        assertTrue(actual_msg.isEmpty());
        verifyNoInteractions(commandHandler);
        verifyNoInteractions(callbackHandler);
    }
}
