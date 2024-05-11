package ru.urfu.bot.services.handlers.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.urfu.bot.db.entities.User;
import ru.urfu.bot.db.exceptions.UserNotFoundException;
import ru.urfu.bot.db.repositories.JpaUserRepository;
import ru.urfu.bot.services.handlers.CommandHandler;
import ru.urfu.bot.utils.MessageConst;
import ru.urfu.bot.utils.dto.Command;

import java.time.OffsetTime;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Устанавливает время для отправки уведомлений для указанного пользователя.
 */
@Service
public class SetNotifyTimeService implements CommandHandler {

    private final JpaUserRepository userRepository;

    private final static Logger LOG = LoggerFactory.getLogger(SetNotifyTimeService.class);

    public SetNotifyTimeService(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public List<SendMessage> handle(Command command, String username, String chatId) {
        try {
            User user = userRepository.findByUserName(username).orElseThrow(UserNotFoundException::new);
            OffsetTime scheduledTime = OffsetTime.parse(command.data());
            user.setScheduledTime(scheduledTime);
            return List.of(new SendMessage(chatId, MessageConst.SET_SCHEDULED_TIME));
        } catch (DateTimeParseException e) {
            LOG.error(e.getMessage());
            return List.of(new SendMessage(chatId, MessageConst.INTERNAL_ERROR));
        } catch (UserNotFoundException e) {
            LOG.error("user {} not found", username);
            return List.of(new SendMessage(chatId, MessageConst.INTERNAL_ERROR));
        }
    }
}
