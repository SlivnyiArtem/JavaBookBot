package ru.urfu.bot.services.handlers.commands;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.urfu.bot.db.entities.Chat;
import ru.urfu.bot.db.entities.User;
import ru.urfu.bot.db.repositories.JpaChatRepository;
import ru.urfu.bot.db.repositories.JpaUserRepository;
import ru.urfu.bot.services.handlers.CommandHandler;
import ru.urfu.bot.utils.MessageConst;
import ru.urfu.bot.utils.dto.Command;

import java.util.List;

/**
 * Запускает бота и записывает пользователя и чат в бд.
 */
@Service
public class StartBotService implements CommandHandler {

    private final JpaUserRepository userRepository;

    private final JpaChatRepository chatRepository;

    public StartBotService(JpaUserRepository userRepository, JpaChatRepository chatRepository) {
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
    }

    @Override
    @Transactional
    public List<SendMessage> handle(Command command, String username, String chatId) {
        if (!chatRepository.existsById(Long.parseLong(chatId))) {
            User user = userRepository.findByUserName(username).orElse(new User(username));
            Chat chat = new Chat(Long.parseLong(chatId));

            user.getChats().add(chat);
            chat.setUser(user);

            userRepository.save(user);
            chatRepository.save(chat);
        }

        return List.of(new SendMessage(chatId, MessageConst.HELP));
    }
}
