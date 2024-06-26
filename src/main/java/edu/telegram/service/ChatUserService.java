package edu.telegram.service;

import edu.telegram.domain.Chat;
import edu.telegram.domain.User;
import edu.telegram.exception.DataNotFoundException;
import edu.telegram.repository.JpaChatRepository;
import edu.telegram.repository.JpaUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetTime;

/**
 * Сервис отвечает за общие операции над пользователем и чатом.
 */
@Service
public class ChatUserService {

    private final JpaUserRepository userRepository;

    private final JpaChatRepository chatRepository;

    private static final String USER_NOT_FOUND_EXCEPTION_MSG = "user %s not found in DB";

    @Autowired
    public ChatUserService(JpaUserRepository userRepository, JpaChatRepository chatRepository) {
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
    }

    /**
     * Сохраняет пользователя и чат в базу данных
     * @param username имя пользователя
     * @param chatId ID чата
     * @return true, если чат сохранен; false, если чат и пользователь уже существуют
     */
    @Transactional
    public boolean addUserChatIfAbsent(String username, Long chatId) {
        if (chatRepository.findById(chatId).isEmpty()) {
            User user = userRepository.findByUserName(username).orElse(new User(username));
            Chat chat = new Chat(chatId);

            user.getChats().add(chat);
            chat.setUser(user);

            userRepository.save(user);
            chatRepository.save(chat);
            return true;
        }
        return false;
    }

    /**
     * Устанавливает врямя получения уведомлений для пользователя
     * @param username имя пользователя
     * @param offsetTime врямя уведомления
     */
    @Transactional
    public void setNotificationTime(String username, OffsetTime offsetTime) throws DataNotFoundException {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new DataNotFoundException(USER_NOT_FOUND_EXCEPTION_MSG.formatted(username)));
        user.setScheduledTime(offsetTime);
        userRepository.save(user);
    }
}
