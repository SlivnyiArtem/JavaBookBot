package ru.urfu.bot.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.urfu.bot.config.BotProperties;
import ru.urfu.bot.services.QueueProvider;

import java.util.concurrent.BlockingQueue;


/**
 * Telegram-Bot
 */
@Component
public class Bot extends TelegramLongPollingBot {

    private final static Logger LOG = LoggerFactory.getLogger(Bot.class);

    private final String userName;

    private final BlockingQueue<Update> receiveQueue;

    /**
     * Конфигурирует и запускает бота
     */
    public Bot(
            TelegramBotsApi telegramBotsApi,
            BotProperties properties,
            QueueProvider queueProvider) throws TelegramApiException {

        super(properties.telegramToken());
        this.userName = properties.telegramBotName();
        this.receiveQueue = queueProvider.getReceiveQueue();
        telegramBotsApi.registerBot(this);
    }

    /**
     * Метод предназначен для обработки сообщений от пользователя
     */
    @Override
    public void onUpdateReceived(Update update) {
        try {
            receiveQueue.put(update);
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return userName;
    }
}