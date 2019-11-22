package com.librarium.repository;

import com.librarium.entity.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface BookRepository extends CrudRepository<Book, Long> {
    List<Book> findByNameContainsIgnoreCase(String name);

    List<Book> findByAuthorContainsIgnoreCase(String name);

    List<Book> findByGenreContainsIgnoreCase(String name);
}
