package ru.urfu.bot.db.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Книга, сохраненная в бд
 */
@Entity
@Table(name = "BOOK")
public class Book {

    public void setIsbn13(Long isbn13) {
        this.isbn13 = isbn13;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        try {
            if (description == null) {
                return;
            }
            int size = 255;
            int inLength = description.length();
            if (inLength>size)
            {
                description = description.substring(0, size);
            }
        } catch (SecurityException ex) {
        }
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

    public Long getIsbn13() {
        return isbn13;
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
    private Long isbn13;

    private String title;

    private String description;

    private String authors;

    private String publisher;

    private LocalDate publishedDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(isbn13, book.isbn13) && Objects.equals(title, book.title) && Objects.equals(description, book.description) && Objects.equals(authors, book.authors) && Objects.equals(publisher, book.publisher) && Objects.equals(publishedDate, book.publishedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn13, title, description, authors, publisher, publishedDate);
    }

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
}
