package ru.urfu.bot.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

/**
 * Устанавливает переменные для приложения
 * @param telegramToken - токен телеграм бота
 * @param telegramBotName - имя бота
 * @param apiKey - ключ для аутентификации в Google Books API (пока не используется)
 * @param bookApiBaseUrl - url для общения с api
 */
@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
@PropertySource("classpath:application.yml")
public record BotProperties(
        @NotEmpty
        String telegramToken,
        @NotEmpty
        String telegramBotName,
        @NotEmpty
        String apiKey,
        @NotEmpty
        String bookApiBaseUrl
) {
}