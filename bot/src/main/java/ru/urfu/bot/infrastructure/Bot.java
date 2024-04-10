package ru.urfu.bot.infrastructure;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.urfu.bot.app.UserMessageProcessor;

@Component
public class Bot extends TelegramLongPollingBot {


    private final String userName;

    private final UserMessageProcessor userMessageProcessor;


    public Bot(
            TelegramBotsApi telegramBotsApi,
            BotProperties config,
            UserMessageProcessor userMessageProcessor) throws TelegramApiException {

        super(config.telegramToken());
        this.userName = config.telegramBotName();
        this.userMessageProcessor = userMessageProcessor;
        telegramBotsApi.registerBot(this);
    }

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage message = userMessageProcessor.process(update);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return userName;
    }
}