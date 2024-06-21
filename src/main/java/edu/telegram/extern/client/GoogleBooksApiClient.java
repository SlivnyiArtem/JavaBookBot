package edu.telegram.extern.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import edu.telegram.config.BotProperties;
import edu.telegram.domain.Book;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Клиент, предназначенный для взаимодействия с Google Books API
 */
@Component
public class GoogleBooksApiClient {

    private final String apiKey;

    private final WebClient webClient;

    private static final int MAX_RESPONSE_SIZE = 5;

    @Autowired
    public GoogleBooksApiClient(BotProperties.BookApi bookApiProperties) {
        this.apiKey = bookApiProperties.apiKey();
        this.webClient = WebClient.create(bookApiProperties.baseUrl());
    }

    /**
     * Возвращает список книг, найденных через API. Ищет по названию книги
     */
    public List<Book> findBooksByTitle(String name) {
        return webClient.get()
                .uri("/volumes?q={name}", name)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> Mono.empty())
                .bodyToMono(BookList.class)
                .blockOptional()
                .filter(bookList -> bookList.getTotalItems() != null && bookList.getTotalItems() > 0)
                .map(BookList::getItems)
                .orElse(List.of()).stream()
                .filter(book -> book.getIsbn() != null && findBookByIsbn(book.getIsbn()).isPresent())
                .limit(MAX_RESPONSE_SIZE)
                .toList();
    }

    /**
     * Находит и возвращает книгу, по ISBN коду
     */
    public Optional<Book> findBookByIsbn(Long isbn) {
        return webClient.get()
                .uri("/volumes?q=isbn:{isbn}", isbn)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> Mono.empty())
                .bodyToMono(BookList.class)
                .blockOptional()
                .filter(bookList -> bookList.getTotalItems() != null && bookList.getTotalItems() > 0)
                .map(BookList::getItems)
                .orElse(List.of()).stream()
                .filter(book -> Objects.equals(book.getIsbn(), isbn))
                .findFirst();
    }

    /**
     * Дто для парсинга JSON ответа из API
     */
    private static class BookList {

        private Long totalItems;

        private List<Book> items;

        public List<Book> getItems() {
            return items;
        }

        public void setItems(List<Book> items) {
            this.items = items;
        }

        public Long getTotalItems() {
            return totalItems;
        }

        public void setTotalItems(Long totalItems) {
            this.totalItems = totalItems;
        }
    }
}
