package com.librarium.repository;

import com.librarium.entity.Book;
import com.librarium.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional

public interface QuoteRepository extends JpaRepository<Quote, Long> {
    List<Quote> findByBook(Book book);
}
