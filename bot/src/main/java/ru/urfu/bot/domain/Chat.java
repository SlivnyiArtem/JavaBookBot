package ru.urfu.bot.domain;

import jakarta.persistence.*;

/**
 * Телеграм чат, сохраненный в бд
 */
@Entity
@Table(name = "CHAT")
public class Chat {

    public Chat(Long id) {
        this.id = id;
    }

    public Chat() { }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public void setUser(User user) {
        this.user = user;
    }
}
