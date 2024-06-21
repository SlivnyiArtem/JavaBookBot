package edu.telegram.domain;

import jakarta.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.telegram.telegrambots.abilitybots.api.objects.Stats;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Document
public class BotData {

    public Map<Long, User> getUsers() {
        return users;
    }

    public Map<String, Long> getUserIds() {
        return userIds;
    }

    public Set<Long> getBlacklist() {
        return blacklist;
    }

    public Set<Long> getAdmins() {
        return admins;
    }

    public Map<String, Stats> getStats() {
        return stats;
    }

    public Map<Long, Integer> getStates() {
        return states;
    }

    public void setUsers(Map<Long, User> users) {
        this.users = users;
    }

    public void setUserIds(Map<String, Long> userIds) {
        this.userIds = userIds;
    }

    public void setStats(Map<String, Stats> stats) {
        this.stats = stats;
    }

    public void setStates(Map<Long, Integer> states) {
        this.states = states;
    }

    public void setBlacklist(Set<Long> blacklist) {
        this.blacklist = blacklist;
    }

    public void setAdmins(Set<Long> admins) {
        this.admins = admins;
    }

    private Map<Long, User> users = new LinkedHashMap<>();

    private Map<String, Long> userIds = new LinkedHashMap<>();

    private Map<String, Stats> stats = new LinkedHashMap<>();

    private Map<Long, Integer> states = new LinkedHashMap<>();

    private Set<Long> blacklist = new LinkedHashSet<>();

    private Set<Long> admins = new LinkedHashSet<>();
}
