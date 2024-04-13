package ru.urfu.bot.app;

import org.springframework.stereotype.Service;
import ru.urfu.bot.domain.entities.Chat;
import ru.urfu.bot.domain.entities.User;
import ru.urfu.bot.infrastructure.db.repositories.JpaBookRepository;
import ru.urfu.bot.infrastructure.db.repositories.JpaChatRepository;
import ru.urfu.bot.infrastructure.db.repositories.JpaUserRepository;

@Service
public class UserBookService {

    private final JpaUserRepository userRepository;
    private final JpaChatRepository chatRepository;
    private final JpaBookRepository bookRepository;

    public UserBookService(
            JpaUserRepository userRepository,
            JpaChatRepository chatRepository,
            JpaBookRepository bookRepository) {

        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
    }

    public void registerChat(String userName, Long chatId) {
        if (chatRepository.findById(chatId).isEmpty()) {
            User user = userRepository.findByUserName(userName).orElse(new User(userName));
            Chat chat = new Chat(chatId);

            user.getChats().add(chat);
            chat.setUser(user);

            userRepository.save(user);
            chatRepository.save(chat);
        }
    }


}
