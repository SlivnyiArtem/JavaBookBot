package ru.urfu.bot.data.repInterfaces;

import ru.urfu.bot.domain.entities.Book;

/**
 * Интерфейс Хранилища книг
 */

public interface BookRepository {

    Book save(Book book);

    void delete(Book book);
}
