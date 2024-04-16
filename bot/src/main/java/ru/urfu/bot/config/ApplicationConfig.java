package ru.urfu.bot.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.urfu.bot.domain.handlers.Command;
import ru.urfu.bot.domain.handlers.books.*;
import ru.urfu.bot.domain.handlers.bot.HelpBotCommand;
import ru.urfu.bot.domain.handlers.bot.StartBotCommand;
import ru.urfu.bot.domain.services.UserBookService;

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
     * Списко используемых комманд
     */
    @Bean
    Map<String, Command> commandMap(UserBookService userBookService) {
        return Map.of(
                "/start", new StartBotCommand(userBookService),
                "/search_book", new SearchBookCommand(userBookService),
                "/add_book", new AddBookCommand(userBookService),
                "/remove_book", new RemoveBookCommand(userBookService),
                "/my_books", new PrintBooksCommand(userBookService),
                "/help", new HelpBotCommand(userBookService),
                "/book_inf", new BookInfoCommand(userBookService)
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
