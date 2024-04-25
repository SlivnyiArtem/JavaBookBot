package ru.urfu.bot.CommandsTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.handlers.commands.HelpBotHandler;
import ru.urfu.bot.utils.MessageConst;
import ru.urfu.bot.utils.dto.Command;
import ru.urfu.bot.utils.dto.CommandType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HelpBotHandlerTest {

    long chatId = 1;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    Update update;

    HelpBotHandler helpBotHandler;

    @BeforeEach
    void init() {
        helpBotHandler = new HelpBotHandler();
        when(update.getMessage().getChatId()).thenReturn(chatId);
    }

    @Test
    void helpCommandTest() {
        // Arrange
        Command command = new Command(CommandType.HELP, "");
        SendMessage expected = SendMessage.builder()
                .chatId(chatId).text(MessageConst.HELP)
                .build();

        // Act
        List<SendMessage> actual_msg = helpBotHandler.handle(command, update);

        // Assert
        assertFalse(actual_msg.isEmpty());
        assertEquals(expected, actual_msg.getFirst());
    }
}
