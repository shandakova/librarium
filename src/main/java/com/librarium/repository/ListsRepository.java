package com.librarium.repository;

import com.librarium.entity.Lists;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ListsRepository extends CrudRepository<Lists,Long> {
}
