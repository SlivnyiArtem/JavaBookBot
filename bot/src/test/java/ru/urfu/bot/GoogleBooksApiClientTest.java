package ru.urfu.bot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import ru.urfu.bot.client.GoogleBooksApiClient;
import ru.urfu.bot.config.BotProperties;
import ru.urfu.bot.domain.Book;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тест на класс {@link ru.urfu.bot.client.GoogleBooksApiClient}. Используетя wiremock для мока API
 */
@WireMockTest(httpPort = 8080)
public class GoogleBooksApiClientTest {

    private final BotProperties botProperties = new BotProperties("", "", 
            "", "http://localhost:8080");
    
    private final GoogleBooksApiClient googleBooksApiClient = new GoogleBooksApiClient(botProperties);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private void isBooksEquals(Book book1, Book book2) {
        assertEquals(book1.getIsbn(), book2.getIsbn());
        assertEquals(book1.getTitle(), book2.getTitle());
        assertEquals(book1.getDescription(), book2.getDescription());
        assertEquals(book1.getAuthors(), book2.getAuthors());
        assertEquals(book1.getPublisher(), book2.getPublisher());
        assertEquals(book1.getPublishedDate(), book2.getPublishedDate());
    }

    /**
     * Коректное тело запроса правильно парсится
     */
    @Test
    void correctVolumeInfoTest() throws JsonProcessingException {
        String body = objectMapper.writeValueAsString(
                Map.of(
                        "totalItems", 1,
                        "items", List.of(
                                Map.of(
                                        "volumeInfo", Map.of(
                                                "industryIdentifiers", List.of(
                                                        Map.of("type", "ISBN_10", "identifier","2"),
                                                        Map.of("type", "ISBN_13", "identifier","1")
                                                ),
                                                "title", "title",
                                                "publishedDate", "2023-11-08",
                                                "authors", List.of("author1", "author2"),
                                                "description", "description",
                                                "publisher", "publisher"
                                        )
                                )
                        )
                )
        );
        stubFor(get(urlEqualTo("/volumes?q=isbn:1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)));

        Book book = new Book();
        book.setIsbn(1L);
        book.setTitle("title");
        book.setDescription("description");
        book.setAuthors("author1, author2");
        book.setPublisher("publisher");
        book.setPublishedDate(LocalDate.of(2023, 11, 8));

        Optional<Book> actual = googleBooksApiClient.findBookByIsbn(1L);
        isBooksEquals(book, actual.orElseThrow());
    }

    /**
     * При отсутствии идентификатора, книга не возвращается
     */
    @Test
    public void testIncorrectIsbn() throws JsonProcessingException {
        String body1 = objectMapper.writeValueAsString(
                Map.of(
                        "totalItems", 1,
                        "items", List.of(
                                Map.of(
                                        "volumeInfo", Map.of(
                                                "industryIdentifiers", List.of(
                                                        Map.of("type", "ISBN_10", "identifier","1")
                                                ),
                                                "title", "title",
                                                "publishedDate", "2023-11-08",
                                                "authors", List.of("author1", "author2"),
                                                "description", "description",
                                                "publisher", "publisher"
                                        )
                                )
                        )
                )
        );
        stubFor(get(urlEqualTo("/volumes?q=isbn:1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(body1)));

        Optional<Book> actual = googleBooksApiClient.findBookByIsbn(1L);
        assertTrue(actual.isEmpty());

        String body2 = objectMapper.writeValueAsString(
                Map.of(
                        "totalItems", 1,
                        "items", List.of(
                                Map.of(
                                        "volumeInfo", Map.of(
                                                "industryIdentifiers", List.of("1", "2"),
                                                "title", "title",
                                                "publishedDate", "2023-11-08",
                                                "authors", List.of("author1", "author2"),
                                                "description", "description",
                                                "publisher", "publisher"
                                        )
                                )
                        )
                )
        );
        stubFor(get(urlEqualTo("/volumes?q=isbn:1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(body2)));

        actual = googleBooksApiClient.findBookByIsbn(1L);
        assertTrue(actual.isEmpty());

        String body3 = objectMapper.writeValueAsString(
                Map.of(
                        "totalItems", 1,
                        "items", List.of(
                                Map.of(
                                        "volumeInfo", Map.of(
                                                "title", "title",
                                                "publishedDate", "2023-11-08",
                                                "authors", List.of("author1", "author2"),
                                                "description", "description",
                                                "publisher", "publisher"
                                        )
                                )
                        )
                )
        );
        stubFor(get(urlEqualTo("/volumes?q=isbn:1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(body3)));

        actual = googleBooksApiClient.findBookByIsbn(1L);
        assertTrue(actual.isEmpty());
    }

    /**
     * Если часть свойств не установлена (кроме ISBN), то книга возвращается
     */
    @Test
    public void testIncorrectProperties() throws JsonProcessingException {
        String body1 = objectMapper.writeValueAsString(
                Map.of(
                        "totalItems", 1,
                        "items", List.of(
                                Map.of(
                                        "volumeInfo", Map.of(
                                                "industryIdentifiers", List.of(
                                                        Map.of("type", "ISBN_13", "identifier","1")
                                                ),
                                                "title", List.of("title"),
                                                "publishedDate", "2023-11",
                                                "authors", "author1, author2",
                                                "description", 123,
                                                "publisher", Map.of("name", "publisher")
                                        )
                                )
                        )
                )
        );
        stubFor(get(urlEqualTo("/volumes?q=isbn:1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(body1)));

        Book book = new Book();
        book.setIsbn(1L);

        Optional<Book> actual = googleBooksApiClient.findBookByIsbn(1L);
        isBooksEquals(book, actual.orElseThrow());

        String body2 = objectMapper.writeValueAsString(
                Map.of(
                        "totalItems", 1,
                        "items", List.of(
                                Map.of(
                                        "volumeInfo", Map.of(
                                                "industryIdentifiers", List.of(
                                                        Map.of("type", "ISBN_13", "identifier","1"),
                                                        Map.of("type", "ISBN_10", "identifier","2")
                                                )
                                        )
                                )
                        )
                )
        );
        stubFor(get(urlEqualTo("/volumes?q=isbn:1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(body2)));

        actual = googleBooksApiClient.findBookByIsbn(1L);
        isBooksEquals(book, actual.orElseThrow());
    }

    /**
     * Если код ответа сервера не 200, или тело запроса не содержит информации о книге,
     * или тело запроса нестандартное, то возращается пустой optional
     */
    @Test
    public void incorrectServerResponseTest() throws JsonProcessingException {
        stubFor(get(urlEqualTo("/volumes?q=isbn:1"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")));

        Optional<Book> actual = googleBooksApiClient.findBookByIsbn(1L);
        assertTrue(actual.isEmpty());

        String body = objectMapper.writeValueAsString(
                Map.of(
                        "totalItems", 0
        ));
        stubFor(get(urlEqualTo("/volumes?q=isbn:1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)));
        actual = googleBooksApiClient.findBookByIsbn(1L);
        assertTrue(actual.isEmpty());

        stubFor(get(urlEqualTo("/volumes?q=isbn:1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("")));
        actual = googleBooksApiClient.findBookByIsbn(1L);
        assertTrue(actual.isEmpty());
    }
}
