package com.librarium.repository;

import com.librarium.entity.Book;
import com.librarium.entity.Lists;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface ListsRepository extends CrudRepository<Lists, Long> {
    List<Lists> findByBooks(Book book);
}
