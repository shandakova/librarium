package com.librarium.repository;

import com.librarium.entity.Book;
import com.librarium.entity.Lists;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface ListsRepository extends JpaRepository<Lists, Long> {
    List<Lists> findByBooks(Book book);
}
