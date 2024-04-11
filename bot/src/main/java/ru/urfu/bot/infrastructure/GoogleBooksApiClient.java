package ru.urfu.bot.infrastructure;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.urfu.bot.app.BookApiClient;

import java.util.List;

@Service
public class GoogleBooksApiClient implements BookApiClient {

    private final String apiKey;

    private final WebClient webClient;

    public GoogleBooksApiClient(BotProperties botProperties) {
        this.apiKey = botProperties.apiKey();
        this.webClient = WebClient.create(botProperties.bookApiBaseUrl());
    }

    @Override
    public Mono<?> findBooksByName(String name) {
        return webClient.get()
                .uri("/volumes?q={name}", name)
                .retrieve()
                .bodyToMono(List.class);
    }
}
