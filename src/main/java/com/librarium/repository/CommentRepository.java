package com.librarium.repository;

import com.librarium.entity.Comment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface CommentRepository extends CrudRepository<Comment, Long> {
}
