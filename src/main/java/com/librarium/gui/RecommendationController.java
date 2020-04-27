package com.librarium.gui;

import com.librarium.entity.dto.Book;
import com.librarium.repository.BookRepository;
import com.librarium.service.BookService;
import com.librarium.utils.FieldParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestClientException;

import java.net.URL;
import java.net.URLConnection;
import java.util.ResourceBundle;

@Controller
public class RecommendationController implements Initializable {
    @FXML
    private TableView<Book> recTable;

    public void setBookRepository(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void setMlc(MyLibraryController mlc) {
        this.mlc = mlc;
    }

    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private MyLibraryController mlc;
    @Autowired
    private BookService bookService;

    @FXML
    private void refreshTable() {
        if (checkInternetConnection()) {
            try {
                if (bookService == null) return;
                recTable.setItems(FXCollections.observableArrayList(bookService.getRecommendation(bookRepository.findAll())));
                setCellFactory((TableColumn<Book, String>) recTable.getColumns().get(1));
            } catch (RestClientException e) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Ошибка");
                alert.setHeaderText(null);
                alert.getButtonTypes().set(0, new ButtonType("OK", ButtonBar.ButtonData.LEFT));
                alert.setContentText("Сервис поиска недоступен.");
                alert.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.getButtonTypes().set(0, new ButtonType("OK", ButtonBar.ButtonData.LEFT));
            alert.setContentText("Рекомендации без интернета не доступны!");
            alert.show();
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        recTable.setPlaceholder(new javafx.scene.control.Label(""));
        recTable.setEditable(true);
        ObservableList<Book> observeList = FXCollections.observableArrayList();
        recTable.setItems(observeList);

        TableColumn<Book, String> colInfo = new TableColumn<>("Рекомендации");
        colInfo.setCellValueFactory(new PropertyValueFactory<>("info"));

        TableColumn<Book, String> colAdd = new TableColumn<>("");
        setCellFactory(colAdd);
        colAdd.setMaxWidth(40);
        colAdd.setMinWidth(40);
        recTable.getColumns().addAll(colInfo, colAdd);
        refreshTable();
    }

    private void setCellFactory(TableColumn<Book, String> colAdd) {
        colAdd.setCellFactory(param -> {
            TableCell<Book, String> cell = new TableCell<Book, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText("+");
                        setCursor(Cursor.HAND);
                        setStyle("-fx-text-fill: green;");
                    }
                }
            };
            cell.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                Book book = cell.getTableView().getItems().get(cell.getIndex());
                if (cell.getStyle().contains("green")) {
                    cell.setCursor(Cursor.DEFAULT);
                    com.librarium.entity.Book b = bookService.castBookFromDtoBook(book);
                    if (checkDtoBookLanguage(book)) {
                        bookRepository.saveAndFlush(b);
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Информация");
                        alert.setHeaderText(null);
                        alert.getButtonTypes().set(0, new ButtonType("OK", ButtonBar.ButtonData.LEFT));
                        alert.setContentText("Книга с названием :" + b.getName() + " добавлена успешно");
                        alert.showAndWait();
                        cell.setDisable(true);
                        cell.setStyle("-fx-text-fill: gray;");
                        mlc.update();
                    }
                }
            });
            cell.setFont(Font.font(30));
            return cell;
        });
    }

    private boolean checkDtoBookLanguage(Book book) {
        if (FieldParser.haveNotCyrillicLatinSymbols(book.getInfo())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Информация");
            alert.setHeaderText(null);
            alert.getButtonTypes().set(0, new ButtonType("OK", ButtonBar.ButtonData.LEFT));
            alert.setContentText("Книга не добавлена из-за недопустимого символов.");
            alert.show();
            return false;
        }
        return true;
    }
}
