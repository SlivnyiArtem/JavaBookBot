package ru.urfu.bot.infrastructure;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.urfu.bot.domain.BookApiClient;
import ru.urfu.bot.domain.entities.Book;
import ru.urfu.bot.infrastructure.config.BotProperties;

import java.util.List;

@Component
public class GoogleBooksApiClient implements BookApiClient {

    private final String apiKey;

    private final WebClient webClient;

    public GoogleBooksApiClient(BotProperties botProperties) {
        this.apiKey = botProperties.apiKey();
        this.webClient = WebClient.create(botProperties.bookApiBaseUrl());
    }

    @Override
    public List<Book> findBooksByName(String name) {
        webClient.get()
                .uri("/volumes?q={name}", name)
                .retrieve()
                .bodyToMono(Object.class).log();
        return List.of();
    }

    @Override
    public Book findBookByIsbn(Long isbn) {
        return null;
    }
}
