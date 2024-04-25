package ru.urfu.bot.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.urfu.bot.handlers.CommandHandler;
import ru.urfu.bot.handlers.callbacks.AddBookHandler;
import ru.urfu.bot.handlers.callbacks.BookInfoHandler;
import ru.urfu.bot.handlers.callbacks.RemoveBookHandler;
import ru.urfu.bot.handlers.commands.*;
import ru.urfu.bot.services.UserBookService;
import ru.urfu.bot.utils.dto.CommandType;

import java.util.Map;

/**
 * Конфигурация дополнительных бинов
 */
@Configuration
public class ApplicationConfig {

    @Bean
    TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        return new TelegramBotsApi(DefaultBotSession.class);
    }

    /**
     * Обработчики для команда, вводимых пользователем
     */
    @Bean
    Map<CommandType, CommandHandler> commandMap(UserBookService userBookService) {
        return Map.of(
                CommandType.START, new StartBotHandler(userBookService),
                CommandType.SEARCH, new SearchBookHandler(userBookService),
                CommandType.PRINT, new PrintBooksHandler(userBookService),
                CommandType.HELP, new HelpBotHandler(),
                CommandType.UNKNOWN, new DefaultHandler()
        );
    }

    /**
     * Обработчики для команд, возвращаемых коллбэком
     */
    @Bean
    Map<CommandType, CommandHandler> callbackMap(UserBookService userBookService) {
        return Map.of(
                CommandType.ADD, new AddBookHandler(userBookService),
                CommandType.INFO, new BookInfoHandler(userBookService),
                CommandType.REMOVE, new RemoveBookHandler(userBookService),
                CommandType.UNKNOWN, new DefaultHandler()
        );
    }

    /**
     * Объект, предназначенный для конвертации dto и моделей (сущностей)
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
