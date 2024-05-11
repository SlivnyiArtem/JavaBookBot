package ru.urfu.bot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.services.MessageProcessor;
import ru.urfu.bot.services.Parser;
import ru.urfu.bot.services.QueueProvider;
import ru.urfu.bot.services.handlers.CommandHandler;
import ru.urfu.bot.utils.dto.Command;
import ru.urfu.bot.utils.dto.CommandType;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageProcessorTest {

    private final String username = "username";

    private final Long chatId = 1L;

    @Mock
    private Map<CommandType, CommandHandler> commandMap;

    @Mock
    private CommandHandler commandHandler;

    @Mock
    private Parser parser;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private QueueProvider queueProvider;

    @InjectMocks
    private MessageProcessor messageProcessor;

    private final Command command = new Command(CommandType.UNKNOWN, "");;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Update update;

    @Test
    void messageUpdateTest() throws InterruptedException {
        // Arrange
        SendMessage sendMessage = new SendMessage(chatId.toString(), "message");
        when(parser.parseCommand(update)).thenReturn(command);

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage().hasText()).thenReturn(true);
        when(update.getMessage().getChat().getUserName()).thenReturn(username);
        when(update.getMessage().getChatId()).thenReturn(chatId);

        when(commandMap.get(eq(CommandType.UNKNOWN))).thenReturn(commandHandler);
        when(commandHandler.handle(eq(command), eq(username), eq(Long.toString(chatId))))
                .thenReturn(List.of(sendMessage));
        when(commandMap.get(eq(CommandType.START))).thenReturn(mock(CommandHandler.class));

        when(queueProvider.getReceiveQueue().take()).thenReturn(update);

        // Act
        messageProcessor.processUpdate();

        // Assert
        verify(commandMap.get(CommandType.START), times(1))
                .handle(eq(new Command(CommandType.START, "")), eq(username), eq(Long.toString(chatId)));
        verify(queueProvider.getSendQueue(), times(1)).put(eq(sendMessage));
    }

    @Test
    void callbackUpdateTest() throws InterruptedException {
        // Arrange
        SendMessage sendMessage = new SendMessage(chatId.toString(), "message");
        when(parser.parseCallback(update)).thenReturn(command);

        when(update.hasCallbackQuery()).thenReturn(true);
        when(update.getCallbackQuery().getFrom().getUserName()).thenReturn(username);
        when(update.getCallbackQuery().getMessage().getChatId()).thenReturn(chatId);

        when(commandMap.get(eq(CommandType.UNKNOWN))).thenReturn(commandHandler);
        when(commandHandler.handle(eq(command), eq(username), eq(Long.toString(chatId))))
                .thenReturn(List.of(sendMessage));
        when(commandMap.get(eq(CommandType.START))).thenReturn(mock(CommandHandler.class));

        when(queueProvider.getReceiveQueue().take()).thenReturn(update);

        // Act
        messageProcessor.processUpdate();

        // Assert
        verify(commandMap.get(CommandType.START), times(1))
                .handle(eq(new Command(CommandType.START, "")), eq(username), eq(Long.toString(chatId)));
        verify(queueProvider.getSendQueue(), times(1)).put(eq(sendMessage));
    }

    @Test
    void notCommandTest() throws InterruptedException {
        // Arrange
        when(update.hasCallbackQuery()).thenReturn(false);
        when(update.hasMessage()).thenReturn(false);

        when(queueProvider.getReceiveQueue().take()).thenReturn(update);

        // Act
        messageProcessor.processUpdate();

        // Assert
        verify(queueProvider.getSendQueue(), never()).put(any());
    }
}
