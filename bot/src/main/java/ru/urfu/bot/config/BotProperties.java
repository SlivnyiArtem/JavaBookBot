package ru.urfu.bot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Duration;

/**
 * @param telegram Настроики телеграм-бота
 * @param scheduler Настройки планировщика
 * @param bookApi Настройки клиента для API с книгами
 */
@Validated
@ConfigurationProperties(value = "app", ignoreUnknownFields = false)
@PropertySource("classpath:application.yml")
public record BotProperties(
        @NotNull @Bean
        Telegram telegram,
        @NotNull @Bean
        Scheduler scheduler,
        @NotNull @Bean
        BookApi bookApi
) {

        /**
         * @param token токен
         * @param botName имя
         * @param creatorId id пользователя
         */
        public record Telegram(@NotBlank String token, @NotBlank String botName, @NotNull Long creatorId) {

        }

        /**
         * @param updateInfoDelay частота проверки обновлений у книг
         * @param checkReleaseDelay частота проверки выхода книг
         * @param receivedProcessDelay частота обработки входящих обновлений
         * @param sendDelay частота отправки сообщений
         */
        public record Scheduler(@NotNull Duration updateInfoDelay, @NotNull Duration checkReleaseDelay,
                         @NotNull Duration receivedProcessDelay, @NotNull Duration sendDelay) {
        }

        /**
         * @param apiKey ключ авторизации (не используется)
         * @param baseUrl хост
         */
        public record BookApi(@NotBlank String apiKey, @NotBlank String baseUrl) {

        }
}