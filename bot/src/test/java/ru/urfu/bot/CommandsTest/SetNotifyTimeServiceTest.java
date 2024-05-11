package ru.urfu.bot.CommandsTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.urfu.bot.db.entities.User;
import ru.urfu.bot.db.repositories.JpaUserRepository;
import ru.urfu.bot.services.handlers.commands.SetNotifyTimeService;
import ru.urfu.bot.utils.dto.Command;
import ru.urfu.bot.utils.dto.CommandType;

import java.time.OffsetTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SetNotifyTimeServiceTest {

    private final String username = "username";

    private final String chatId = "1";

    @InjectMocks
    private SetNotifyTimeService setNotifyTimeService;

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private User user;

    private static Stream<Arguments> provideCorrectTimeString() {
        return Stream.of(
                Arguments.of("10:00:59+01:00"),
                Arguments.of("00:00+01:00"),
                Arguments.of("00:00-18:00")
        );
    }

    @ParameterizedTest
    @MethodSource("provideCorrectTimeString")
    public void setCorrectTimeTest(String timeString) {
        OffsetTime offsetTime = OffsetTime.parse(timeString);
        when(userRepository.findByUserName(username)).thenReturn(Optional.of(user));

        Command command = new Command(CommandType.SET_TIME, timeString);

        List<SendMessage> sendMessageList = setNotifyTimeService.handle(command, username, chatId);

        verify(user, times(1)).setScheduledTime(eq(offsetTime));
        assertEquals("Установленно время для получения уведомлений", sendMessageList.getFirst().getText());
    }

    private static Stream<Arguments> provideIncorrectTimeString() {
        return Stream.of(
                Arguments.of("24:00:59+01:00"),
                Arguments.of("23:80:59+01:00"),
                Arguments.of("23:50:70+01:00"),
                Arguments.of("00:00:00"),
                Arguments.of("00:00+20:00"),
                Arguments.of("ffffff")
        );
    }

    @ParameterizedTest
    @MethodSource("provideIncorrectTimeString")
    public void setIncorrectTimeTest(String timeString) {
        when(userRepository.findByUserName(username)).thenReturn(Optional.of(user));

        Command command = new Command(CommandType.SET_TIME, timeString);

        List<SendMessage> sendMessageList = setNotifyTimeService.handle(command, username, chatId);

        verify(user, never()).setScheduledTime(any(OffsetTime.class));
        assertEquals("Внутренняя ошибка сервера", sendMessageList.getFirst().getText());
    }

    @Test
    void userNotFoundTest() {
        when(userRepository.findByUserName(username)).thenReturn(Optional.empty());

        Command command = new Command(CommandType.SET_TIME, "00:00+00:00");

        List<SendMessage> sendMessageList = setNotifyTimeService.handle(command, username, chatId);

        assertEquals("Внутренняя ошибка сервера", sendMessageList.getFirst().getText());
    }
}
