package com.librarium.gui;

import com.librarium.entity.Book;
import com.librarium.repository.BookRepository;
import com.librarium.repository.QuoteRepository;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class AddQuoteController implements Initializable {
    private BookRepository bookRepository;
    private QuoteRepository quoteRepository;
    private Book book;

    public void initData(QuoteRepository quoteRepository, Book book, BookRepository bookRepository) {
        this.book = book;
        this.bookRepository = bookRepository;
        this.quoteRepository = quoteRepository;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
