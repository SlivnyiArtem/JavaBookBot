package ru.urfu.bot.domain;

import jakarta.persistence.*;

import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;

/**
 * Объект пользователя телеграм-бота
 */
@Entity
@Table(name = "USERS")
public class User {

    public User(String userName) {
        this.userName = userName;
    }

    public User() { }

    public Long getUserId() {
        return userId;
    }

    public Set<Chat> getChats() {
        return chats;
    }

    public String getUserName() {
        return userName;
    }

    public Set<Book> getBooks() {
        return books;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true)
    private String userName;

    @OneToMany(mappedBy = "user")
    private final Set<Chat> chats = new HashSet<>();

    @ManyToMany(mappedBy = "users")
    private final Set<Book> books = new HashSet<>();

    private OffsetTime scheduledTime = OffsetTime.of(0, 0, 0, 0, ZoneOffset.ofHours(0));

    public OffsetTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(OffsetTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
}
