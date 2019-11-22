package com.librarium.entity;

import javax.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;

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
