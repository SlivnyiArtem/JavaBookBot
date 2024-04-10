package ru.urfu.bot.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Chat {

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
}
