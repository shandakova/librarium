package com.librarium.gui;

import com.librarium.entity.Book;
import com.librarium.repository.BookRepository;

public class EditPageController {
    private BookRepository bookRepository;
    private Book book;

    public void initData(BookRepository bookRepository, Book book) {
        this.book = book;
        this.bookRepository = bookRepository;
    }
}
