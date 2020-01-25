package com.librarium.gui;

import com.librarium.entity.Book;
import com.librarium.repository.BookApiRepository;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestClientException;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

@Controller
public class InternetSearchController implements Initializable {
    @FXML
    private ComboBox searchTypeComboBox;
    @FXML
    private TextField searchTextField;
    @FXML
    private TableView<Book> searchTable;
    private BookApiRepository bookApiRepository;
    private AddBookController abc;

    @FXML
    private void clickedSearchButton() {
        String type = (String) searchTypeComboBox.getValue();
        String searchReq = searchTextField.getText();
        if (!checkSearchField(type, searchReq)) return;
        ObservableList<Book> searchList = FXCollections.observableArrayList();
        try {
            switch (type) {
                case ("ISBN"):
                    searchList.addAll(bookApiRepository.getBookListByIsbn(searchReq, 30));
                    break;
                case ("Название"):
                    searchList.addAll(bookApiRepository.getBookListByTitle(searchReq, 30));
                    break;
                case ("Автор"):
                    searchList.addAll(bookApiRepository.getBookListByAuthor(searchReq, 30));
                    break;
                case ("Жанр"):
                    searchList.addAll(bookApiRepository.getBookListByGenre(searchReq, 30));
                    break;
            }
        } catch (RestClientException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.getButtonTypes().set(0, new ButtonType("OK", ButtonBar.ButtonData.LEFT));
            alert.setContentText("Сервис поиска недоступен.");
            alert.show();
        }
        if (searchList.size() == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Информация");
            alert.setHeaderText(null);
            alert.getButtonTypes().set(0, new ButtonType("OK", ButtonBar.ButtonData.LEFT));
            alert.setContentText("Не найдено ничего");
            alert.show();
        }
        searchTable.setItems(searchList);
    }

    private boolean checkSearchField(String type, String searchReq) {
        String symbol;
        if (FieldParser.isBlankString(searchReq)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Предупреждение");
            alert.setHeaderText(null);
            alert.getButtonTypes().set(0, new ButtonType("OK", ButtonBar.ButtonData.LEFT));
            alert.setContentText("Поле не должно быть пустым!");
            alert.showAndWait();
            return false;
        } else if (type.equals("Название")) {
            symbol = FieldParser.checkBookName(searchReq);
        } else if (type.equals("Автор")) {
            symbol = FieldParser.checkAuthor(searchReq);
        } else if (type.equals("Жанр")) {
            symbol = FieldParser.checkGenre(searchReq);
        } else {
            symbol = FieldParser.checkISBN(searchReq);
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
        return true;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> list = FXCollections.observableArrayList(
                Arrays.asList("ISBN", "Название", "Автор", "Жанр"));
        searchTypeComboBox.setItems(list);
        searchTypeComboBox.setValue("Название");
        searchTextField.setDisable(false);
        initializeSearchTable();
    }

    private void initializeSearchTable() {
        searchTable.setPlaceholder(new Label(""));
        searchTable.setEditable(true);
        ObservableList<Book> observeList = FXCollections.observableArrayList();
        searchTable.setItems(observeList);
        TableColumn<Book, Integer> colAuthor = new TableColumn<>("Автор");
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));

        TableColumn<Book, String> colName = new TableColumn<>("Название");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Book, String> colGenre = new TableColumn<>("Жанр");
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));

        TableColumn<Book, String> colIsbn = new TableColumn<>("ISBN");
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("ISBN"));

        TableColumn<Book, String> colAdd = new TableColumn<>("");
        colAdd.setCellFactory(param -> {
            TableCell<Book, String> cell = new TableCell<Book, String>() {
            };
            cell.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                Book book = cell.getTableView().getItems().get(cell.getIndex());
                abc.fillField(book);
                cell.getScene().getWindow().hide();
            });
            cell.setText("+");
            cell.setFont(Font.font(15));
            cell.setStyle("-fx-text-fill: green;");
            cell.setCursor(Cursor.HAND);
            return cell;
        });
        colAdd.setMaxWidth(30);
        colAdd.setMinWidth(30);
        searchTable.getColumns().addAll(colAuthor, colName, colGenre, colIsbn, colAdd);
    }

    public void initData(AddBookController abc, BookApiRepository bar) {
        this.abc = abc;
        this.bookApiRepository = bar;
    }
}
