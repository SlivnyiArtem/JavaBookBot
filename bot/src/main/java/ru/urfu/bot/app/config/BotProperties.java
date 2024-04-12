package ru.urfu.bot.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

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