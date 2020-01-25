package com.librarium.gui;

import com.librarium.entity.Book;
import com.librarium.repository.BookApiRepository;
import com.librarium.repository.BookRepository;
import com.librarium.utils.FieldParser;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
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
    @FXML
    private Button searchInternet;
    @Autowired
    private MyLibraryController mlc;
    @Autowired
    private BookApiRepository bar;

    private Integer rate = 0;

    @FXML
    private void clickedAddBookButton() {
        int actualYear = 2020;
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
        addRate.getItems().remove(0, addRate.getItems().size());
        addRate.setItems(FXCollections.observableArrayList(0));
        updateWindows();
    }

    public void fillField(Book book) {
        addAuthor.setText(book.getAuthor());
        addTitle.setText(book.getName());
        addGenre.setText(book.getGenre());
        addYear.setText(String.valueOf(book.getYear()));
        addISBN.setText(book.getISBN());
    }

    @FXML
    private void clickedSearchNetworkButton() {
        if (!checkInternetConnection()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.getButtonTypes().set(0, new ButtonType("OK", ButtonBar.ButtonData.LEFT));
            alert.setContentText("Поиск по сети невозможен!");
            alert.showAndWait();
        } else {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/internetsearch.fxml"));
            try {
                Parent root = loader.load();
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setTitle("Информация о книге");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(searchInternet.getScene().getWindow());
            InternetSearchController controller = loader.getController();
            controller.initData(this, bar);
            stage.show();
        }
    }

    private boolean checkInternetConnection() {
        try {
            URL url = new URL("https://www.googleapis.com/books/v1/volumes?q=test");
            URLConnection connection = url.openConnection();
            connection.connect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkAddFields(String author, String title, String genre, String year, String isbn) {
        String symbol;
        /*проверка названия*/
        if (FieldParser.isBlankString(title)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Предупреждение");
            alert.setHeaderText(null);
            alert.getButtonTypes().set(0, new ButtonType("OK", ButtonBar.ButtonData.LEFT));
            alert.setContentText("Поле \"Название\" не должно быть пустым!");
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
        colRating.setCellFactory(table -> new TableCell<Integer, Number>() {
            private final Rating rating = new Rating();
            private final ChangeListener<Number> ratingChangeListener;

            {
                ratingChangeListener = (observable, oldValue, newValue) -> rate = newValue.intValue();

            }

            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                rating.ratingProperty().removeListener(ratingChangeListener);
                if (empty) {
                    setGraphic(null);
                } else {
                    if (item != null) {
                        rating.setRating(item.intValue());
                    } else {
                        rating.setRating(0);
                    }
                    rating.ratingProperty().addListener(ratingChangeListener);
                    setStyle("-fx-alignment: CENTER;");
                    setGraphic(rating);
                }
            }
        });
        addRate.getColumns().addAll(colRating);
        addRate.setItems(FXCollections.observableArrayList(0));
    }

    public void updateWindows() {
        mlc.update();
    }

}
