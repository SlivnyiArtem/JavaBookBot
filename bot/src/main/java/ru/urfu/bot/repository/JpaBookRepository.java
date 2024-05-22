package ru.urfu.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.urfu.bot.domain.Book;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий базы данных для сохраненных книг
 */
@Repository
public interface JpaBookRepository extends JpaRepository<Book, Long> {

    /**
     * Ищет книгу по ISBN в коллекции пользователя
     * @param id ISBN код
     * @param username имя пользователя
     * @return книга
     */
    Optional<Book> findByIsbnAndUsers_UserName(Long id, String username);

    /**
     * Возвращает все книги пользователя
     * @param username имя пользователя
     * @return список книг
     */
    List<Book> findAllByUsers_UserName(String username);
}
