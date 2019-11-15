package com.librarium.repository;
import java.util.List;

import com.librarium.entity.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface BookRepository extends CrudRepository<Book, Long> {
    List<Book> findByNameContainsIgnoreCase(String name);
    List<Book> findByAuthorContainsIgnoreCase(String name);
    List<Book> findByGenreContainsIgnoreCase(String name);
}
