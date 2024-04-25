package ru.urfu.bot.CommandsTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.handlers.commands.StartBotHandler;
import ru.urfu.bot.services.UserBookService;
import ru.urfu.bot.utils.MessageConst;
import ru.urfu.bot.utils.dto.Command;
import ru.urfu.bot.utils.dto.CommandType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StartBotHandlerTest {

    String username = "username";

    long chatId = 1;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    Update update;

    StartBotHandler startBotHandler;

    @Mock
    UserBookService userBookService;

    @BeforeEach
    void init() {
        startBotHandler = new StartBotHandler(userBookService);
        when(update.getMessage().getChatId()).thenReturn(chatId);
        when(update.getMessage().getChat().getUserName()).thenReturn(username);
    }

    @Test
    void startCommandTest() {
        // Arrange
        Command command = new Command(CommandType.START, "");
        SendMessage expected = SendMessage.builder()
                .chatId(chatId).text(MessageConst.BOT_STARTED)
                .build();

        // Act
        List<SendMessage> actual_msg = startBotHandler.handle(command, update);

        // Assert
        assertFalse(actual_msg.isEmpty());
        assertEquals(expected, actual_msg.getFirst());
    }
}
