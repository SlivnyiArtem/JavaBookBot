package ru.urfu.bot.domain.port;

import ru.urfu.bot.domain.entities.Book;

import java.util.List;

public interface UserBookService {

    void addChat(String username, Long chatId);

    boolean containsChat(Long id);

    List<Book> findBooksByTitle(String title);


    Book findBookByIsbn(Long isbn);

    void addBook(String username, Book book);

    void removeBook(String username, Book book);

    List<Book> getUserBooks(String username);
}
