package ru.urfu.bot.infrastructure.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.urfu.bot.app.UserBookService;
import ru.urfu.bot.domain.handlers.Command;
import ru.urfu.bot.domain.handlers.books.AddBookCommand;
import ru.urfu.bot.domain.handlers.books.PrintBooksCommand;
import ru.urfu.bot.domain.handlers.books.SearchBookCommand;
import ru.urfu.bot.domain.handlers.bot.HelpBotCommand;
import ru.urfu.bot.domain.handlers.bot.StartBotCommand;

import java.util.Map;

@Configuration
public class ApplicationConfig {

    @Bean
    TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        return new TelegramBotsApi(DefaultBotSession.class);
    }

    @Bean
    Map<String, Command> commandMap(UserBookService userBookService) {
        return Map.of(
                "/start", new StartBotCommand(userBookService),
                "/search_book", new SearchBookCommand(userBookService),
                "/add_book", new AddBookCommand(userBookService),
                "/my_books", new PrintBooksCommand(userBookService),
                "/help", new HelpBotCommand(userBookService)
        );
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
