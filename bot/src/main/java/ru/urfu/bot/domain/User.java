package ru.urfu.bot.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    public Long getUserId() {
        return userId;
    }

    public Set<Chat> getChats() {
        return chats;
    }

    @Id
    private Long userId;

    @OneToMany(mappedBy = "user")
    private final Set<Chat> chats = new HashSet<>();
}
