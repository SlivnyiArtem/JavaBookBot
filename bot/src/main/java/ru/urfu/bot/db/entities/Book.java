package ru.urfu.bot.db.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Id;


import java.time.LocalDate;
import java.util.HashSet;
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
