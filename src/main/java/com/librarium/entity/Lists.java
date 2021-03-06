package com.librarium.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Data
@Entity
public class Lists {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToMany(mappedBy = "lists", fetch = FetchType.EAGER)
    private Set<Book> books = new HashSet<>();

    public Lists(String name) {
        this.name = name;
    }

    public int getSize() {
        return books.size();
    }
}
