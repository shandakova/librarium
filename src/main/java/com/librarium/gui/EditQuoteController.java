package com.librarium.gui;

import com.librarium.entity.Quote;
import com.librarium.repository.BookRepository;
import com.librarium.repository.QuoteRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class EditQuoteController implements Initializable {
    private BookRepository bookRepository;
    private QuoteRepository quoteRepository;
    private Quote quote;

    @FXML
    private TextField editQuote;
    @FXML
    private Button deleteQuote;
    @FXML
    private Button updateQuote;

    public void initData(BookRepository bookRepository, QuoteRepository quoteRepository, Quote quote) {
        this.quote = quote;
        this.bookRepository = bookRepository;
        this.quoteRepository = quoteRepository;
        editQuote.setText(quote.getQuotation());
    }

    @FXML
    private void clickedUpdateQuoteButton() {
        quote.setQuotation(editQuote.getText());
        quoteRepository.save(quote);
        Stage stage = (Stage) updateQuote.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void clickedDeleteQuoteButton() {
        quoteRepository.delete(quote);
        Stage stage = (Stage) deleteQuote.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
