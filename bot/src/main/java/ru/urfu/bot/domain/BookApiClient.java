package ru.urfu.bot.domain;

import reactor.core.publisher.Mono;
import ru.urfu.bot.domain.entities.Book;

import java.util.List;

public interface BookApiClient {

    List<Book> findBooksByName(String name);

    Book findBookByIsbn(Long isbn);
}
