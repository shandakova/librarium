package com.librarium.gui;

import com.librarium.entity.Quote;
import com.librarium.repository.QuoteRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class EditQuoteController implements Initializable {
    private QuoteRepository quoteRepository;
    private Quote quote;

    @FXML
    private TextArea editQuote;
    @FXML
    private Button deleteQuote;
    @FXML
    private Button updateQuote;

    public void initData(QuoteRepository quoteRepository, Quote quote) {
        this.quote = quote;
        this.quoteRepository = quoteRepository;
        editQuote.setText(quote.getQuotation());
    }

    @FXML
    private void clickedUpdateQuoteButton() {
        quote.setQuotation(editQuote.getText());
        quoteRepository.saveAndFlush(quote);
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
