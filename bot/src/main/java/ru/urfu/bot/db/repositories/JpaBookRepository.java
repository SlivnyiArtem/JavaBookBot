package ru.urfu.bot.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.urfu.bot.db.entities.Book;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaBookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn13AndUsers_UserName(Long id, String username);

    List<Book> findAllByUsers_UserName(String username);
}
