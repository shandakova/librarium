package com.librarium.gui;

import com.librarium.entity.Book;
import com.librarium.repository.BookRepository;
import com.librarium.utils.FieldParser;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.controlsfx.control.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

@Controller
public class AddBookController implements Initializable {
    @Autowired
    private BookRepository bookRepository;
    @FXML
    private TextField addAuthor;
    @FXML
    private TextField addTitle;
    @FXML
    private TextField addGenre;
    @FXML
    private TextField addYear;
    @FXML
    private TextField addISBN;
    @FXML
    private TableView<Integer> addRate;

    Integer rate = 5;

    @FXML
    private void clickedAddBookButton() {
        Integer actualYear = 2020;
        String author = addAuthor.getText();
        String title = addTitle.getText();
        String genre = addGenre.getText();
        String year = addYear.getText();
        String isbn = addISBN.getText();
        if (!checkAddFields(author, title, genre, year, isbn)) return;
        Book book = new Book();
        if (!FieldParser.isBlankString(year)) {
            actualYear = Integer.parseInt(year);
            book.setYear(actualYear);
        }
        String actualISBN = FieldParser.parseISBN(isbn);
        book.setAuthor(author);
        book.setName(title);
        book.setGenre(genre);
        book.setISBN(actualISBN);
        book.setRate(rate);
        bookRepository.save(book);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Поздравляем!");
        alert.setHeaderText(null);
        alert.getButtonTypes().set(0, new ButtonType("OK", ButtonBar.ButtonData.LEFT));
        alert.setContentText("Книга добавлена!");
        alert.showAndWait();
        addAuthor.setText("");
        addTitle.setText("");
        addGenre.setText("");
        addYear.setText("");
        addISBN.setText("");
    }

    @FXML
    private void clickedSearchNetworkButton() {

    }

    private boolean checkAddFields(String author, String title, String genre, String year, String isbn) {
            String symbol;
            /*проверка названия*/
            if (FieldParser.isBlankString(title)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Предупреждение");
                alert.setHeaderText(null);
                alert.getButtonTypes().set(0, new ButtonType("OK", ButtonBar.ButtonData.LEFT));
                alert.setContentText("Поле не должно быть пустым!");
                alert.showAndWait();
                return false;
            } else {
                    symbol = FieldParser.checkBookName(title);
                }
                if (symbol.length() > 0) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Предупреждение");
                    alert.setHeaderText(null);
                    alert.getButtonTypes().set(0, new ButtonType("OK", ButtonBar.ButtonData.LEFT));
                    alert.setContentText("Недопустимый символ: " + symbol + "!");
                    alert.showAndWait();
                    return false;
                }
            /*проверка автора*/
            symbol = FieldParser.checkAuthor(author);
            if (symbol.length() > 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Предупреждение");
                alert.setHeaderText(null);
                alert.getButtonTypes().set(0, new ButtonType("OK", ButtonBar.ButtonData.LEFT));
                alert.setContentText("Недопустимый символ: " + symbol + "!");
                alert.showAndWait();
                return false;
            }
            /*проверка жанра*/
            symbol = FieldParser.checkGenre(genre);
            if (symbol.length() > 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Предупреждение");
                alert.setHeaderText(null);
                alert.getButtonTypes().set(0, new ButtonType("OK", ButtonBar.ButtonData.LEFT));
                alert.setContentText("Недопустимый символ: " + symbol + "!");
                alert.showAndWait();
                return false;
            }
            /*проверка года*/
        if (!FieldParser.isBlankString(year)) {
            symbol = FieldParser.checkYear(year);
            if (symbol.length() > 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Предупреждение");
                alert.setHeaderText(null);
                alert.getButtonTypes().set(0, new ButtonType("OK", ButtonBar.ButtonData.LEFT));
                alert.setContentText("Недопустимый символ: " + symbol + "!");
                alert.showAndWait();
                return false;
            }
        }
            /*проверка ISBN*/
            symbol = FieldParser.checkISBN(isbn);
            if (symbol.length() > 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Предупреждение");
                alert.setHeaderText(null);
                alert.getButtonTypes().set(0, new ButtonType("OK", ButtonBar.ButtonData.LEFT));
                alert.setContentText("Недопустимый символ: " + symbol + "!");
                alert.showAndWait();
                return false;
            }
        return true;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TableColumn<Integer, Number> colRating = new TableColumn<>("Рейтинг");
        colRating.setCellValueFactory(new PropertyValueFactory<>("rate"));

        colRating.setCellFactory(table -> new TableCell<Integer, Number>() {
        private final Rating rating;
        private final ChangeListener<Number> ratingChangeListener;

        {
            rating = new Rating(5);
            ratingChangeListener = (observable, oldValue, newValue) -> {
                rate = newValue.intValue();
            };
        }

        @Override
        protected void updateItem(Number item, boolean empty) {
            super.updateItem(item, empty);
            rating.ratingProperty().removeListener(ratingChangeListener);
            if (empty) {
                setGraphic(null);
            } else {
                rating.setRating(item.doubleValue());
                rating.ratingProperty().addListener(ratingChangeListener);
                setGraphic(rating);
            }
        }
        });
        addRate.getColumns().addAll(colRating);
    }

}
