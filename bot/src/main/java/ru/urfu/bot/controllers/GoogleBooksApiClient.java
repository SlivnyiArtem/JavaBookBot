package ru.urfu.bot.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.urfu.bot.config.BotProperties;
import ru.urfu.bot.db.entities.Book;
import ru.urfu.bot.utils.dto.BookApiDto;
import ru.urfu.bot.utils.dto.BookListApiDto;

import java.util.List;
import java.util.Optional;

/**
 * Клиент, предназначенный для взаимодействия с Google Books API
 */
@Component
public class GoogleBooksApiClient {

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
    public List<Book> findBooksByTitle(String name) {
        BookListApiDto bookListApiDto = webClient.get()
                .uri("/volumes?q={name}", name)
                .retrieve()
                .bodyToMono(BookListApiDto.class)
                .block();

        if (bookListApiDto == null || bookListApiDto.getTotalItems() == 0) {
            return List.of();
        }
        return bookListApiDto
                .getItems()
                .stream()
                .filter(bookApiDto -> bookApiDto.getIsbn13() != null)
                .map(bookApiDto -> modelMapper.map(bookApiDto, Book.class))
                .limit(5)
                .toList();
    }

    /**
     * Находит и возвращает книгу, по isbn коду
     */
    public Optional<Book> findBookByIsbn(Long isbn) {
        BookListApiDto bookListApiDto = webClient.get()
                .uri("/volumes?q=isbn:{isbn}", isbn)
                .retrieve()
                .bodyToMono(BookListApiDto.class)
                .block();

        if (bookListApiDto == null || bookListApiDto.getTotalItems() == 0) {
            return Optional.empty();
        }
        BookApiDto bookApiDto = bookListApiDto.getItems()
                .stream()
                .filter(bookApiDto1 -> bookApiDto1.getIsbn13() != null)
                .findFirst()
                .orElseThrow();
        return Optional.of(modelMapper.map(bookApiDto, Book.class));
    }
}
