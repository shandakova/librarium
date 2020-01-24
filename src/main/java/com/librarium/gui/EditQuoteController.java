package com.librarium.gui;

import com.librarium.entity.Quote;
import com.librarium.repository.BookRepository;
import com.librarium.repository.QuoteRepository;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class EditQuoteController implements Initializable {
    private BookRepository bookRepository;
    private QuoteRepository quoteRepository;
    private Quote quote;

    public void initData(BookRepository bookRepository, QuoteRepository quoteRepository, Quote quote) {
        this.quote = quote;
        this.bookRepository = bookRepository;
        this.quoteRepository = quoteRepository;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
