package com.librarium.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Data
@Entity
public class Quote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    private String quotation;

    public Quote(Book book, String quotation) {
        this.book = book;
        this.quotation = quotation;
    }
}
