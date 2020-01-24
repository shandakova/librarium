package com.librarium.repository;

import com.librarium.entity.Book;
import com.librarium.entity.Quote;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface QuoteRepository extends CrudRepository<Quote, Long> {
    List<Quote> findByBook(Book book);
}
