package ru.urfu.bot.domain.port.repInterfaces;

import ru.urfu.bot.domain.port.repositories.JpaBookRepository;
import ru.urfu.bot.domain.entities.Book;

/**
 * Интерфейс Хранилища книг
 */

public interface BookRepository extends JpaBookRepository {

    Book save(Book book);

    void delete(Book book);
}
