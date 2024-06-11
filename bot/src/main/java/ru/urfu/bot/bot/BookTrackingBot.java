package ru.urfu.bot.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.abilitybots.api.toggle.CustomToggle;
import org.telegram.telegrambots.abilitybots.api.util.AbilityExtension;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.urfu.bot.config.BotProperties;
import ru.urfu.bot.service.BotService;

import java.util.List;
import java.util.Map;


/**
 * Telegram-Bot
 */
@Component
public class BookTrackingBot extends AbilityBot implements SpringLongPollingBot {

    private final String botUserName;

    private final String botToken;

    private final Long creatorId;

    private static TelegramClient getTelegramClient(String botToken) {
        return new OkHttpTelegramClient(botToken);
    }

    private static CustomToggle getCustomToggle() {
        return new CustomToggle()
                .toggle("commands", "help");
    }

    /**
     * Конфигурирует бота
     */
    public BookTrackingBot(BotProperties.Telegram telegramProperties, BotService botService,
                           List<AbilityExtension> abilityExtensionList) {
        super(getTelegramClient(telegramProperties.token()), telegramProperties.botName(), botService, getCustomToggle());
        this.botUserName = telegramProperties.botName();
        this.botToken = telegramProperties.token();
        this.creatorId = telegramProperties.creatorId();
        this.addExtensions(abilityExtensionList);
    }

    @Override
    public long creatorId() {
        return creatorId;
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        this.onRegister();
    }
}