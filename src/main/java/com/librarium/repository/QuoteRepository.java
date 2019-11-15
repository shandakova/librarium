package com.librarium.repository;

import com.librarium.entity.Quote;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface QuoteRepository extends CrudRepository<Quote, Long> {
}
