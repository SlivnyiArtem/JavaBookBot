package ru.urfu.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.urfu.bot.infrastructure.config.BotProperties;

@SpringBootApplication
@EnableConfigurationProperties(BotProperties.class)
public class BotApplication {

    public static void main(String[] args) {
        SpringApplication.run(BotApplication.class, args);
    }

}