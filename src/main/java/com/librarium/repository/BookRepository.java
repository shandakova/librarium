package com.librarium.repository;

import com.librarium.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByNameContainsIgnoreCase(String name);

    List<Book> findByAuthorContainsIgnoreCase(String name);

    List<Book> findByGenreContainsIgnoreCase(String name);
}
