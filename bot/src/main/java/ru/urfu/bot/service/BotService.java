package ru.urfu.bot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.telegrambots.abilitybots.api.db.DBContext;
import org.telegram.telegrambots.abilitybots.api.db.Var;
import org.telegram.telegrambots.abilitybots.api.objects.ReplyFlow;
import ru.urfu.bot.domain.BotData;
import ru.urfu.bot.repository.BotDataRepository;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class BotService implements DBContext {

    private final BotDataRepository botDataRepository;

    private final static Logger log = LoggerFactory.getLogger(BotService.class);

    public BotService(BotDataRepository botDataRepository) {
        this.botDataRepository = botDataRepository;
    }

    private BotData getBotData() {
        List<BotData> data = botDataRepository.findAll();
        if (data.isEmpty()) {
            return botDataRepository.save(new BotData());
        } else {
            return data.getFirst();
        }
    }

    @Override
    public <T> List<T> getList(String name) {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> Map<K, V> getMap(String name) {
        try {
            return switch (name) {
                case BaseAbilityBot.USERS -> (Map<K, V>) getBotData().getUsers();
                case BaseAbilityBot.USER_ID -> (Map<K, V>) getBotData().getUserIds();
                case BaseAbilityBot.STATS -> (Map<K, V>) getBotData().getStats();
                case ReplyFlow.ReplyFlowBuilder.STATES -> (Map<K, V>) getBotData().getStates();
                default -> Map.of();
            };
        } catch (ClassCastException e) {
            log.error("Cast error", e);
            return Map.of();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Set<T> getSet(String name) {
        try {
            return switch (name) {
                case BaseAbilityBot.BLACKLIST -> (Set<T>) getBotData().getBlacklist();
                case BaseAbilityBot.ADMINS -> (Set<T>) getBotData().getAdmins();
                default -> Set.of();
            };
        } catch (ClassCastException e) {
            log.error("Cast error", e);
            return Set.of();
        }
    }

    @Override
    public <T> Var<T> getVar(String name) {
        return null;
    }

    @Override
    public String summary() {
        return null;
    }

    @Override
    public Object backup() {
        return null;
    }

    @Override
    public boolean recover(Object backup) {
        return false;
    }

    @Override
    public String info(String name) {
        return null;
    }

    @Override
    public void commit() {

    }

    @Override
    public void clear() {

    }

    @Override
    public boolean contains(String name) {
        return false;
    }

    @Override
    public void close() throws IOException {

    }
}
