package ru.urfu.bot.domain.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Book {

    public void setIsbn13(Long isbn13) {
        this.isbn13 = isbn13;
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
