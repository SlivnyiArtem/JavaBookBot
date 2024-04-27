package ru.urfu.bot.CommandsTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.urfu.bot.db.entities.Chat;
import ru.urfu.bot.db.entities.User;
import ru.urfu.bot.db.repositories.JpaChatRepository;
import ru.urfu.bot.db.repositories.JpaUserRepository;
import ru.urfu.bot.services.handlers.commands.StartBotService;
import ru.urfu.bot.utils.dto.Command;
import ru.urfu.bot.utils.dto.CommandType;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StartBotServiceTest {

    private final String username = "username";

    private final long chatId = 1L;

    private StartBotService startBotService;

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private JpaChatRepository chatRepository;

    @Mock
    private Chat chat;

    @Mock
    private User user;

    @BeforeEach
    void init() {
        startBotService = new StartBotService(userRepository, chatRepository);
    }

    @Test
    void newChatTest() {
        // Arrange
        when(chatRepository.existsById(eq(chatId))).thenReturn(false);
        when(userRepository.findByUserName(username)).thenReturn(Optional.of(user));

        Command command = new Command(CommandType.ADD, "");

        // Act
        startBotService.handle(command, username, Long.toString(chatId));

        // Assert
        verify(userRepository).save(user);
        verify(chatRepository).save(any());
    }
}
