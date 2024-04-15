package ru.urfu.bot.infrastructure.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.urfu.bot.app.port.BookRepository;
import ru.urfu.bot.domain.entities.Book;

@Repository
public interface JpaBookRepository extends JpaRepository<Book, Long>, BookRepository {
}
