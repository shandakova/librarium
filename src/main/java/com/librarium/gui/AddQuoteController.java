package com.librarium.gui;

import com.librarium.entity.Book;
import com.librarium.entity.Quote;
import com.librarium.repository.QuoteRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AddQuoteController implements Initializable {
    private QuoteRepository quoteRepository;
    private Book book;

    @FXML
    private TextArea textArea;
    @FXML
    private Button cancel;
    @FXML
    private Button saveQuote;

    public void initData(QuoteRepository quoteRepository, Book book) {
        this.book = book;
        this.quoteRepository = quoteRepository;
    }

    @FXML
    private void clickedSaveQuoteButton() {
        Quote quote = new Quote();
        quote.setBook(book);
        quote.setQuotation(textArea.getText());
        quoteRepository.saveAndFlush(quote);
        quoteRepository.flush();
        Stage stage = (Stage) saveQuote.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void clickedCancelButton() {
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
