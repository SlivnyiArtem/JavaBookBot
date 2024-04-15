package ru.urfu.bot.infrastructure;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.urfu.bot.app.port.BookApiClient;
import ru.urfu.bot.domain.entities.Book;
import ru.urfu.bot.infrastructure.config.BotProperties;
import ru.urfu.bot.infrastructure.dto.BookApiDto;
import ru.urfu.bot.infrastructure.dto.BookListApiDto;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Клиент, предназначенный для взаимодействия с Google Books API
 */
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

    /**
     * Возвращает список книг, найденных через api. Ищет по названию книги
     */
    @Override
    public List<Book> findBooksByName(String name) {
        BookListApiDto bookListApiDto= webClient.get()
                .uri("/volumes?q={name}", name)
                .retrieve()
                .bodyToMono(BookListApiDto.class)
                .block();

        if (bookListApiDto.getTotalItems() == 0) {
            return List.of();
        }
        return bookListApiDto
                .getItems()
                .stream()
                .filter(bookApiDto -> bookApiDto.getIsbn13() != null)
                .map(bookApiDto -> modelMapper.map(bookApiDto, Book.class))
                .limit(10)
                .toList();
    }

    /**
     * Находит и возвращает книгу, по isbn коду
     */
    @Override
    public Book findBookByIsbn(Long isbn) {
        BookListApiDto bookListApiDto = webClient.get()
                .uri("/volumes?q=isbn:{isbn}", isbn)
                .retrieve()
                .bodyToMono(BookListApiDto.class)
                .block();

        if (bookListApiDto.getTotalItems() == 0) {
            throw new NoSuchElementException();
        }
        BookApiDto bookApiDto = bookListApiDto.getItems()
                .stream()
                .filter(bookApiDto1 -> bookApiDto1.getIsbn13() != null)
                .findFirst()
                .orElseThrow();
        return modelMapper.map(bookApiDto, Book.class);
    }
}
