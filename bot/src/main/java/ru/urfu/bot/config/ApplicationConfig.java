package ru.urfu.bot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.urfu.bot.bot.Bot;
import ru.urfu.bot.handler.UpdateHandler;
import ru.urfu.bot.service.BookTrackingService;
import ru.urfu.bot.utils.QueueProvider;
import ru.urfu.bot.worker.BookWorker;
import ru.urfu.bot.worker.DispatchUpdateWorker;
import ru.urfu.bot.worker.SendMessageWorker;

import java.util.List;

/**
 * Конфигурация бинов
 */
@Configuration
@EnableScheduling
public class ApplicationConfig {

    @Bean
    public Bot bot(BotProperties.Telegram telegramProperties, QueueProvider queueProvider) {
        return new Bot(telegramProperties, queueProvider.getReceivedQueue());
    }

    @Bean
    public BookWorker bookWorker(BookTrackingService bookTrackingService, QueueProvider queueProvider) {
        return new BookWorker(bookTrackingService, queueProvider.getSendQueue());
    }

    @Bean
    public DispatchUpdateWorker dispatchUpdateWorker(List<UpdateHandler> updateHandlers, QueueProvider queueProvider) {
        return new DispatchUpdateWorker(updateHandlers, queueProvider.getSendQueue(), queueProvider.getReceivedQueue());
    }

    @Bean
    public SendMessageWorker sendMessageWorker(Bot bot, QueueProvider queueProvider) {
        return new SendMessageWorker(queueProvider.getSendQueue(), bot);
    }
}
