package ru.urfu.bot.infrastructure;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.urfu.bot.app.BookApiClient;
import ru.urfu.bot.domain.entities.Book;
import ru.urfu.bot.infrastructure.config.BotProperties;
import ru.urfu.bot.infrastructure.dto.BookApiDto;
import ru.urfu.bot.infrastructure.dto.BookListApiDto;

import java.util.List;

@Component
public class GoogleBooksApiClient implements BookApiClient {

    private final String apiKey;
    private final WebClient webClient;
    private final ModelMapper modelMapper;


    public GoogleBooksApiClient(BotProperties botProperties, ModelMapper modelMapper) {
        this.apiKey = botProperties.apiKey();
        this.webClient = WebClient.create(botProperties.bookApiBaseUrl());
        this.modelMapper = modelMapper;
    }

    @Override
    public List<Book> findBooksByName(String name) {
        return webClient.get()
                .uri("/volumes?q={name}", name)
                .retrieve()
                .bodyToMono(BookListApiDto.class)
                .block()
                .getItems()
                .stream()
                .map(bookApiDto -> modelMapper.map(bookApiDto, Book.class))
                .limit(10)
                .toList();
    }

    @Override
    public Book findBookByIsbn(Long isbn) {
        BookApiDto bookApiDto = webClient.get()
                .uri("/volumes?q=isbn:{isbn}", isbn)
                .retrieve()
                .bodyToMono(BookListApiDto.class)
                .block()
                .getItems()
                .getFirst();
        return modelMapper.map(bookApiDto, Book.class);
    }
}
