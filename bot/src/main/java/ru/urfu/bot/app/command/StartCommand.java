package ru.urfu.bot.app.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.bot.app.JpaChatRepository;
import ru.urfu.bot.app.JpaUserRepository;
import ru.urfu.bot.domain.Chat;
import ru.urfu.bot.domain.User;

@Component
public class StartCommand implements Command {

    private final JpaUserRepository userRepository;

    private final JpaChatRepository chatRepository;

    public StartCommand(
            JpaUserRepository userRepository,
            JpaChatRepository chatRepository) {

        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
    }

    @Override
    public SendMessage handle(Update update) {
        String userName = update.getMessage().getChat().getUserName();
        Long chatId = update.getMessage().getChatId();

        User user = userRepository.findByUserName(userName).orElse(new User(userName));
        Chat chat = chatRepository.findById(chatId).orElse(new Chat(chatId));

        user.getChats().add(chat);
        chat.setUser(user);

        userRepository.save(user);
        chatRepository.save(chat);

        return new SendMessage(chatId.toString(), "Bot started");
    }
}
