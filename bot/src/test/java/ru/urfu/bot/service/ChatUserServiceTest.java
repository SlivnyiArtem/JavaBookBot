package ru.urfu.bot.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.urfu.bot.domain.Chat;
import ru.urfu.bot.domain.User;
import ru.urfu.bot.repository.JpaChatRepository;
import ru.urfu.bot.repository.JpaUserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тесты на клас {@link ChatUserService}
 */
@ExtendWith(MockitoExtension.class)
public class ChatUserServiceTest {

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private JpaChatRepository chatRepository;

    @InjectMocks
    private ChatUserService chatUserService;

    /**
     * Тест {@link ChatUserService#addUserChatIfAbsent(String, Long)}.
     * <ol>
     *     <li>Чат и пользователь не существуют; возрат true</li>
     *     <li>Чат не существуют; возрат true</li>
     *     <li>Чат и пользователь существует; возрат false</li>
     * </ol>
     */
    @Test
    public void createChatTest() {
        Long chatId = 1L;
        String username = "test";

        when(chatRepository.findById(eq(chatId))).thenReturn(Optional.empty());
        when(userRepository.findByUserName(eq(username))).thenReturn(Optional.empty());
        assertTrue(chatUserService.addUserChatIfAbsent(username, chatId));

        User user = new User(username);
        when(userRepository.findByUserName(eq(username))).thenReturn(Optional.of(user));
        assertTrue(chatUserService.addUserChatIfAbsent(username, chatId));
        verify(userRepository).save(eq(user));

        Chat chat = new Chat(chatId);
        when(chatRepository.findById(eq(chatId))).thenReturn(Optional.of(chat));
        assertFalse(chatUserService.addUserChatIfAbsent(username, chatId));
    }
}
