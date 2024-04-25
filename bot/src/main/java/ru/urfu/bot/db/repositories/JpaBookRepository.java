package ru.urfu.bot.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.urfu.bot.db.entities.Book;

@Repository
public interface JpaBookRepository extends JpaRepository<Book, Long> { }
