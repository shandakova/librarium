package com.librarium.gui;

import com.librarium.entity.Book;
import com.librarium.repository.BookRepository;
import com.librarium.utils.FieldParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Optional;


public class EditPageController {
    private BookRepository bookRepository;
    private Book book;
    private BookInformationController bic;
    @FXML
    private TextField title;
    @FXML
    private TextField author;
    @FXML
    private TextField isbn;
    @FXML
    private TextField year;
    @FXML
    private TextField genre;
    @FXML
    private Button delBtn;
    @FXML
    private Button okBtn;

    public void initData(BookRepository bookRepository, Book book, BookInformationController bic) {
        this.book = book;
        this.bookRepository = bookRepository;
        this.bic = bic;
        title.setText(book.getName());
        author.setText(book.getAuthor());
        isbn.setText(book.getISBN());
        year.setText(String.valueOf(book.getYear()));
        genre.setText(book.getGenre());
    }

    public void clickedOkButton(ActionEvent actionEvent) {
        if (FieldParser.isBlankString(title.getText())) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Предупреждение");
            alert.setHeaderText(null);
            alert.getButtonTypes().set(0, new ButtonType("OK", ButtonBar.ButtonData.LEFT));
            alert.setContentText("Поле не должно быть пустым!");
            alert.showAndWait();
            return;
        }
        if (FieldParser.checkBookName(title.getText()).length() > 0) {
            showAlertSymbol(FieldParser.checkBookName(title.getText()));
            return;
        }
        if (FieldParser.checkAuthor(author.getText()).length() > 0) {
            showAlertSymbol(FieldParser.checkBookName(author.getText()));
            return;
        }
        if (FieldParser.checkGenre(genre.getText()).length() > 0) {
            showAlertSymbol(FieldParser.checkBookName(author.getText()));
            return;
        }
        if (FieldParser.checkISBN(isbn.getText()).length() > 0) {
            showAlertSymbol(FieldParser.checkBookName(author.getText()));
            return;
        }
        if (FieldParser.checkYear(year.getText()).length() > 0) {
            showAlertSymbol(FieldParser.checkBookName(author.getText()));
            return;
        }
        book.setAuthor(author.getText());
        book.setName(title.getText());
        book.setGenre(genre.getText());
        book.setISBN(isbn.getText());
        book.setYear(Integer.parseInt(year.getText()));
        bookRepository.saveAndFlush(book);
        bic.update();
        okBtn.getScene().getWindow().hide();
    }

    private void showAlertSymbol(String symbol) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Предупреждение");
        alert.setHeaderText(null);
        alert.getButtonTypes().set(0, new ButtonType("OK", ButtonBar.ButtonData.LEFT));
        alert.setContentText("Недопустимый символ: " + symbol + "!");
        alert.showAndWait();
    }

    public void clickedDeleteButton(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Удаление");
        alert.setHeaderText("Вы уверены?");
        ButtonType yes = new ButtonType("Да");
        ButtonType no = new ButtonType("Нет");
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(yes, no);
        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == yes) {
            bookRepository.deleteById(book.getId());
            delBtn.getScene().getWindow().hide();
            bic.updatePagesAndClose();
            alert.close();
        } else {
            alert.close();
        }
    }
}
