package ru.urfu.bot.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.urfu.bot.config.BotProperties;
import ru.urfu.bot.domain.Book;

import java.util.List;
import java.util.Optional;

/**
 * Клиент, предназначенный для взаимодействия с Google Books API
 */
@Component
public class GoogleBooksApiClient {

    private final String apiKey;
    private final WebClient webClient;

    @Autowired
    public GoogleBooksApiClient(BotProperties botProperties) {
        this.apiKey = botProperties.apiKey();
        this.webClient = WebClient.create(botProperties.bookApiBaseUrl());
    }

    /**
     * Возвращает список книг, найденных через API. Ищет по названию книги
     */
    public List<Book> findBooksByTitle(String name) {
        BookList bookList = webClient.get()
                .uri("/volumes?q={name}", name)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> Mono.empty())
                .bodyToMono(BookList.class)
                .block();

        if (bookList == null || bookList.getTotalItems() == null || bookList.getTotalItems() == 0) {
            return List.of();
        }
        return bookList
                .getItems()
                .stream()
                .filter(book -> book.getIsbn() != null)
                // этот фильтр замедляет вывод; нужен потому, что некоторые книги нельзя найти по ISBN
                .filter(book -> findBookByIsbn(book.getIsbn()).isPresent())
                .limit(5)
                .toList();
    }

    /**
     * Находит и возвращает книгу, по ISBN коду
     */
    public Optional<Book> findBookByIsbn(Long isbn) {
        BookList bookList = webClient.get()
                .uri("/volumes?q=isbn:{isbn}", isbn)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> Mono.empty())
                .bodyToMono(BookList.class)
                .block();

        if (bookList == null || bookList.getTotalItems() == null || bookList.getTotalItems() == 0) {
            return Optional.empty();
        }
        return bookList.getItems()
                .stream()
                .filter(book1 -> book1.getIsbn() != null)
                .findFirst();
    }

    /**
     * Дто для парсинга JSON ответа из API
     * <br/><br/>
     * P.S. вроде это не тот static который запрещен
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
