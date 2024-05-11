package ru.urfu.bot.utils.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.LinkPreviewOptions;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.ReplyParameters;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Расширение типа {@link SendMessage}. Сообщение имеет дату и время отправки.
 */
@JsonIgnoreProperties("receivingTime")
public class SendScheduledMessage extends SendMessage {
    private final OffsetDateTime receivingTime;

    public SendScheduledMessage(String chatId, String text, OffsetDateTime receivingTime) {
        super(chatId, text);
        this.receivingTime = receivingTime;
    }

    public SendScheduledMessage(OffsetDateTime receivingTime) {
        this.receivingTime = receivingTime;
    }

    public SendScheduledMessage(String chatId, Integer messageThreadId, String text, String parseMode,
                                Boolean disableWebPagePreview, Boolean disableNotification, Integer replyToMessageId,
                                ReplyKeyboard replyMarkup, List<MessageEntity> entities, Boolean allowSendingWithoutReply,
                                Boolean protectContent, LinkPreviewOptions linkPreviewOptions, ReplyParameters replyParameters,
                                OffsetDateTime receivingTime) {
        super(chatId, messageThreadId, text, parseMode, disableWebPagePreview, disableNotification,
                replyToMessageId, replyMarkup, entities, allowSendingWithoutReply, protectContent, linkPreviewOptions,
                replyParameters);
        this.receivingTime = receivingTime;
    }

    /**
     * @return дата и время получения сообщения пользователем
     */
    public OffsetDateTime getReceivingTime() {
        return receivingTime;
    }

}
