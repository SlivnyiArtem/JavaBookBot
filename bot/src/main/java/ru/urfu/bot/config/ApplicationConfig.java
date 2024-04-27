package ru.urfu.bot.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.urfu.bot.services.handlers.CommandHandler;
import ru.urfu.bot.services.handlers.callbacks.AddBookService;
import ru.urfu.bot.services.handlers.callbacks.BookInfoService;
import ru.urfu.bot.services.handlers.callbacks.RemoveBookService;
import ru.urfu.bot.services.handlers.commands.StartBotService;
import ru.urfu.bot.services.handlers.commands.SearchBookService;
import ru.urfu.bot.services.handlers.commands.PrintBooksService;
import ru.urfu.bot.services.handlers.commands.HelpBotService;
import ru.urfu.bot.services.handlers.commands.DefaultHandlerService;
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
    Map<CommandType, CommandHandler> commandMap(ApplicationContext context) {
        return Map.of(
                CommandType.START, context.getBean(StartBotService.class),
                CommandType.SEARCH, context.getBean(SearchBookService.class),
                CommandType.PRINT, context.getBean(PrintBooksService.class),
                CommandType.HELP, context.getBean(HelpBotService.class),
                CommandType.UNKNOWN, context.getBean(DefaultHandlerService.class)
        );
    }

    /**
     * Обработчики для команд, возвращаемых коллбэком
     */
    @Bean
    Map<CommandType, CommandHandler> callbackMap(ApplicationContext context) {
        return Map.of(
                CommandType.ADD, context.getBean(AddBookService.class),
                CommandType.INFO, context.getBean(BookInfoService.class),
                CommandType.REMOVE, context.getBean(RemoveBookService.class),
                CommandType.UNKNOWN, context.getBean(DefaultHandlerService.class)
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
