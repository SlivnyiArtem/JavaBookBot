package ru.urfu.bot.domain.port.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.urfu.bot.domain.entities.Book;

@Repository
public interface JpaBookRepository extends JpaRepository<Book, Long> {
    Book save(Book book);
    void delete(Book book);
}
