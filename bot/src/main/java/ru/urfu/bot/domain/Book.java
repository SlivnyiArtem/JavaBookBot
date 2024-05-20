package ru.urfu.bot.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Книга, сохраненная в бд
 */
@Entity
@Table(name = "BOOK")
public class Book {

    public void setIsbn(Long isbn) {
        this.isbn = isbn;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setPublishedDate(LocalDate publishedDate) {
        this.publishedDate = publishedDate;
    }

    public Long getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthors() {
        return authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public LocalDate getPublishedDate() {
        return publishedDate;
    }

    @Id
    private Long isbn;

    private String title;

    private String description;

    private String authors;

    private String publisher;

    private LocalDate publishedDate;

    @ManyToMany
    @JoinTable(
            name = "tracking",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private final Set<User> users = new HashSet<>();

    public Set<User> getUsers() {
        return users;
    }

    @Transient
    private final Logger logger = LoggerFactory.getLogger(Book.class);

    @Transient
    private static final int TEXT_FIELDS_MAX_SIZE = 255;

    @JsonProperty("volumeInfo")
    public void unpackNested(Map<String,Object> volumeInfo) {
        if (volumeInfo.get("industryIdentifiers") != null) {
            try {
                List<?> identifiers = (List<?>) volumeInfo.get("industryIdentifiers");
                this.isbn = Long.parseLong(
                        (String) identifiers.stream()
                                .map(obj -> (Map<?,?>) obj)
                                .filter(map -> Objects.equals(map.get("type"), "ISBN_13"))
                                .findFirst()
                                .orElseThrow()
                                .get("identifier")
                );
            } catch (ClassCastException | NoSuchElementException | NumberFormatException e) {
                logger.warn(e.getMessage());
                // Если нет идентификатора, значит объект не годен
                return;
            }
        } else {
            return;
        }


        if (volumeInfo.get("title") != null) {
            try {
                this.title = trimValue((String) volumeInfo.get("title"));
            } catch (ClassCastException | NoSuchElementException e) {
                logger.warn(e.getMessage());
            }
        }

        if (volumeInfo.get("description") != null) {
            try {
                this.description = trimValue((String) volumeInfo.get("description"));
            } catch (ClassCastException | NoSuchElementException e) {
                logger.warn("can't parse description", e);
            }
        }

        if (volumeInfo.get("authors") != null) {
            try {
                List<?> authorsList = (List<?>) volumeInfo.get("authors");
                this.authors = trimValue(authorsList.stream()
                        .map(obj -> (String) obj)
                        .collect(Collectors.joining(", ")));
            } catch (ClassCastException | NoSuchElementException e) {
                logger.warn("can't parse authors list", e);
            }
        }

        if (volumeInfo.get("publisher") != null) {
            try {
                this.publisher = trimValue((String) volumeInfo.get("publisher"));
            } catch (ClassCastException | NoSuchElementException e) {
                logger.warn("can't parse publisher", e);
            }
        }

        if (volumeInfo.get("publishedDate") != null) {
            try {
                this.publishedDate = LocalDate.parse((String) volumeInfo.get("publishedDate"));
            } catch (ClassCastException | NoSuchElementException | DateTimeParseException e) {
                logger.warn("can't parse published date", e);
            }
        }
    }

    private String trimValue(String bookValue) {
        return bookValue.substring(0, Math.min(TEXT_FIELDS_MAX_SIZE, bookValue.length()));
    }
}
