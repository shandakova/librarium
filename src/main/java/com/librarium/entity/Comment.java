package com.librarium.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "book_id")
    private Book book;
    private String comment;

    public Comment(Book book, String quotation) {
        this.book = book;
        this.comment = quotation;
    }
}
