package ru.urfu.bot.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.urfu.bot.config.BotProperties;
import ru.urfu.bot.services.UserMessageProcessor;

import java.util.List;


/**
 * Telegram-Bot
 */
@Component
public class Bot extends TelegramLongPollingBot {

    private final String userName;

    private final UserMessageProcessor userMessageProcessor;


    /**
     * Конфигурирует и запускает бота
     */
    public Bot(
            TelegramBotsApi telegramBotsApi,
            BotProperties properties,
            UserMessageProcessor userMessageProcessor) throws TelegramApiException {

        super(properties.telegramToken());
        this.userName = properties.telegramBotName();
        this.userMessageProcessor = userMessageProcessor;
        telegramBotsApi.registerBot(this);
    }

    /**
     * Метод предназначен для обработки сообщений от пользователя
     */
    @Override
    public void onUpdateReceived(Update update) {
        List<SendMessage> response = userMessageProcessor.process(update);
        response.forEach(message -> {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public String getBotUsername() {
        return userName;
    }
}