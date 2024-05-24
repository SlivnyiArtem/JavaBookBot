package ru.urfu.bot.utils;

import com.fasterxml.jackson.annotation.JsonValue;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;

import java.time.OffsetDateTime;

/**
 * Декоратор над классом {@link BotApiMethodMessage}. Сообщение имеет дату и время отправки.
 */
public class SendScheduledMessage extends BotApiMethodMessage {

    private final OffsetDateTime receivingTime;

    private final BotApiMethodMessage botApiMethodMessage;

    public SendScheduledMessage(BotApiMethodMessage botApiMethodMessage, OffsetDateTime receivingTime) {
        this.botApiMethodMessage = botApiMethodMessage;
        this.receivingTime = receivingTime;
    }

    /**
     * @return дата и время получения сообщения пользователем
     */
    public OffsetDateTime getReceivingTime() {
        return receivingTime;
    }

    /**
     * @return базовое сообщения
     */
    @JsonValue
    public BotApiMethodMessage getBotApiMethodMessage() {
        return botApiMethodMessage;
    }

    @Override
    public String getMethod() {
        return botApiMethodMessage.getMethod();
    }
}
