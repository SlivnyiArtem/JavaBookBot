package ru.urfu.bot.utils.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.urfu.bot.services.handlers.callbacks.AddBookService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Dto для получения информации о книге из api.
 */
@JsonNaming
public class BookApiDto {
    private static final Logger LOG = LoggerFactory.getLogger(AddBookService.class);

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public LocalDate getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(LocalDate publishedDate) {
        this.publishedDate = publishedDate;
    }

    public Long getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(Long isbn13) {
        this.isbn13 = isbn13;
    }

    private Long isbn13;

    private String title;

    private String description;

    private String authors = "";

    private String publisher;

    private LocalDate publishedDate;

    @JsonProperty("volumeInfo")
    public void unpackNested(Map<String,Object> volumeInfo) {
        this.title = volumeInfo.get("title") != null ? (String) volumeInfo.get("title") : "";
        this.description = volumeInfo.get("description") != null ? (String) volumeInfo.get("description") : "";

        List<String> authors = (List<String>) volumeInfo.get("authors");
        if (authors != null) {
            this.authors = String.join(", ", authors);
        }

        this.publisher = volumeInfo.get("publisher") != null ? (String) volumeInfo.get("publisher") : "";

        try {
            System.out.println(volumeInfo.get("publishedDate"));
            this.publishedDate = LocalDate.parse((String) volumeInfo.get("publishedDate"));
        } catch (DateTimeParseException e) {
            LOG.error(String.format("incorrect published date of %s", title), e);
        }

        List<Map<String, String>> identifiers = (List<Map<String, String>>) volumeInfo.get("industryIdentifiers");

        try {
            this.isbn13 = Long.parseLong(identifiers.stream()
                    .filter(map -> map.get("type").equals("ISBN_13"))
                    .findFirst()
                    .orElseThrow()
                    .get("identifier"));
        } catch (NoSuchElementException e) {
            LOG.error(String.format("no isbn of %s", title), e);
        }
    }
}
