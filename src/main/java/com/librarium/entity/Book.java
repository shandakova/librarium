package com.librarium.entity;


import javax.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"quotes", "comments", "lists"})
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private String author;
    private String genre;
    private int year;
    private int rate;
    private String ISBN;
    @OneToMany(mappedBy = "book", orphanRemoval = true, cascade = CascadeType.MERGE,fetch = FetchType.EAGER)
    private Set<Quote> quotes = new HashSet<>();

    @OneToMany(mappedBy = "book", orphanRemoval = true, cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private Set<Comment> comments = new HashSet<>();

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
            name = "books_lists",
            joinColumns = {@JoinColumn(name = "book_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "list_id", referencedColumnName = "id")}
    )
    private Set<Lists> lists = new HashSet<>();

    public Book(String name) {
        this.name = name;
        this.id = this.getId();
    }

    public void addQuote(Quote quote) {
        quotes.add(quote);
    }

    public int getNumberQuotes() {
        return quotes.size();
    }
    public String toString(){
        return "";
    }
}