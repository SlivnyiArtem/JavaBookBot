package ru.urfu.bot.domain.services;

import ru.urfu.bot.domain.entities.Book;

import java.util.List;

/**
 * интерфейс клиента для работы с API
 */

public interface BookApiClient {

    List<Book> findBooksByName(String name);

    Book findBookByIsbn(Long isbn);
}
