package ru.urfu.bot.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Book {

    @Id
    private Long isbn10;

    private String title;

    private String description;

    private String authors;

    private String publisher;

    private LocalDate publishedDate;

    @JoinTable(
            name = "tracking",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private final Set<User> users = new HashSet<>();
}
