package ru.urfu.bot.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Dto для получения информации о книге из api
 */
public class BookApiDto {

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
    private void unpackNested(Map<String,Object> volumeInfo) {
        this.title = volumeInfo.get("title") != null ? (String) volumeInfo.get("title") : "";
        this.description = volumeInfo.get("description") != null ? (String) volumeInfo.get("description") : "";

        List<String> authors = (List<String>) volumeInfo.get("authors");
        if (authors != null) {
            this.authors = String.join(", ", authors);
        }

        this.publisher = volumeInfo.get("publisher") != null ? (String) volumeInfo.get("publisher") : "";

        try {
            this.publishedDate = LocalDate.parse((String) volumeInfo.get("publishedDate"));
        } catch (DateTimeParseException e) {
        }

        List<Map<String, String>> identifiers = (List<Map<String, String>>) volumeInfo.get("industryIdentifiers");

        try {
            this.isbn13 = Long.parseLong(identifiers.stream()
                    .filter(map -> map.get("type").equals("ISBN_13"))
                    .findFirst()
                    .orElseThrow()
                    .get("identifier"));
        } catch (NoSuchElementException e) {
        }
    }
}
