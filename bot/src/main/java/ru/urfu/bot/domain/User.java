package ru.urfu.bot.domain;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true)
    private String userName;

    @OneToMany(mappedBy = "user")
    private final Set<Chat> chats = new HashSet<>();


}
