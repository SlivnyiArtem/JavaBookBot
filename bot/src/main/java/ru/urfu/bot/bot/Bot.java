package ru.urfu.bot.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import ru.urfu.bot.services.QueueProvider;

import java.util.concurrent.BlockingQueue;


/**
 * Telegram-Bot
 */
@Component
public class Bot extends TelegramLongPollingBot {

    private final String userName;

    private final BotUpdateDispatcher botUpdateDispatcher;

    private final Logger logger = LoggerFactory.getLogger(Bot.class);
    private final BlockingQueue<Update> receiveQueue;

    /**
     * Конфигурирует бота
     */
    @Autowired
    public Bot(BotProperties properties,
               BotUpdateDispatcher botUpdateDispatcher,
               QueueProvider queueProvider) {

        super(properties.telegramToken());
        this.userName = properties.telegramBotName();
        this.botUpdateDispatcher = botUpdateDispatcher;
        this.receiveQueue = queueProvider.getReceiveQueue();
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
            logger.error(e.getMessage());
        }
    }

    /**
     * Метод предназначен для обработки сообщений от пользователя
     */
    @Override
    public void onUpdateReceived(Update update) {
        List<SendMessage> response = botUpdateDispatcher.dispatch(update);
        response.forEach(message -> {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                logger.warn("can't execute message", e);
            }
        });
    }

    @Override
    public String getBotUsername() {
        return userName;
    }
}