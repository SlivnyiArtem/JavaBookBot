package ru.urfu.bot.app.port;

import ru.urfu.bot.domain.entities.Book;

public interface BookRepository {

    Book save(Book book);

    void delete(Book book);
}
