package ru.urfu.bot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.services.Parser;
import ru.urfu.bot.utils.dto.Command;
import ru.urfu.bot.utils.dto.CommandType;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParserTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    Update update;

    Parser parser;

    private final static Long CHAT_ID = 1L;

    @BeforeEach
    void init() {
        parser = new Parser();
    }

    private static Stream<Arguments> provideMessageUpdates() {
        return Stream.of(
                Arguments.of("/search title", new Command(CommandType.SEARCH, "title")),
                Arguments.of("/search", new Command(CommandType.SEARCH, "")),
                Arguments.of("/search word1 word2 word3",
                        new Command(CommandType.SEARCH, "word1 word2 word3")),
                Arguments.of("/start", new Command(CommandType.START, "")),
                Arguments.of("/start args", new Command(CommandType.START, "")),
                Arguments.of("/my_books", new Command(CommandType.PRINT, "")),
                Arguments.of("/my_books args", new Command(CommandType.PRINT, "")),
                Arguments.of("/help", new Command(CommandType.HELP, "")),
                Arguments.of("/help args", new Command(CommandType.HELP, "")),
                Arguments.of("/fidf title", new Command(CommandType.UNKNOWN, CHAT_ID.toString())),
                Arguments.of("/fidf", new Command(CommandType.UNKNOWN, CHAT_ID.toString()))
        );
    }

    private static Stream<Arguments> provideCallbackUpdates() {
        return Stream.of(
                Arguments.of("/add_book 123", new Command(CommandType.ADD, "123")),
                Arguments.of("/add_book 123 1 f", new Command(CommandType.ADD, "-1")),
                Arguments.of("/add_book", new Command(CommandType.ADD, "-1")),
                Arguments.of("/remove_book 123", new Command(CommandType.REMOVE, "123")),
                Arguments.of("/remove_book fe", new Command(CommandType.REMOVE, "-1")),
                Arguments.of("/remove_book", new Command(CommandType.REMOVE, "-1")),
                Arguments.of("/book_inf 123", new Command(CommandType.INFO, "123")),
                Arguments.of("/book_inf", new Command(CommandType.INFO, "-1")),
                Arguments.of("/fsef 123", new Command(CommandType.UNKNOWN, CHAT_ID.toString())),
                Arguments.of("/lij", new Command(CommandType.UNKNOWN, CHAT_ID.toString()))
        );
    }

    @ParameterizedTest
    @MethodSource("provideMessageUpdates")
    void messageUpdateTest(String text, Command expected) {
        // Arrange
        lenient().when(update.getMessage().getChatId()).thenReturn(CHAT_ID);
        when(update.getMessage().getText()).thenReturn(text);

        // Act
        Command actual = parser.parseCommand(update);

        // Assert
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("provideCallbackUpdates")
    void callbackUpdateTest(String text, Command expected) {
        // Arrange
        lenient().when(update.getCallbackQuery().getMessage().getChatId()).thenReturn(CHAT_ID);
        when(update.getCallbackQuery().getData()).thenReturn(text);

        // Act
        Command actual = parser.parseCallback(update);

        // Assert
        assertEquals(expected, actual);
    }
}
