package com.librarium.repository;

import com.librarium.entity.Book;
import com.librarium.entity.Comment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface CommentRepository extends CrudRepository<Comment, Long> {
    List<Comment> findByBook(Book book);
}
