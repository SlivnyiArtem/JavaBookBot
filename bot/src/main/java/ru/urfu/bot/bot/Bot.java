package ru.urfu.bot.bot;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.urfu.bot.config.BotProperties;

import java.util.concurrent.BlockingQueue;


/**
 * Telegram-Bot
 */
@Component
public class Bot extends TelegramLongPollingBot {

    private final String userName;
    private final Logger logger = LoggerFactory.getLogger(Bot.class);
    private final BlockingQueue<Update> receivedQueue;

    /**
     * Конфигурирует бота
     */
    @Autowired
    public Bot(BotProperties.Telegram telegramProperties,
               BlockingQueue<Update> receivedQueue) {
        super(telegramProperties.token());
        this.userName = telegramProperties.botName();
        this.receivedQueue = receivedQueue;
    }

    /**
     * Запускает бота
     */
    @PostConstruct
    public void start() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(this);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Метод предназначен для обработки сообщений от пользователя
     */
    @Override
    public void onUpdateReceived(Update update) {
        try {
            receivedQueue.put(update);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return userName;
    }
}