package com.librarium.gui;

import com.librarium.entity.Book;
import com.librarium.entity.Lists;
import com.librarium.repository.BookRepository;
import com.librarium.repository.ListsRepository;
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
import javafx.util.Callback;
import org.controlsfx.control.Rating;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class SearchAddBookInListController implements Initializable {
    private BookRepository bookRepository;
    private ListsRepository listsRepository;
    private Lists list;
    private List<Book> books;
    @FXML
    private ComboBox searchfilter;
    @FXML
    private TextField searchfield;
    @FXML
    private TableView<Book> searchresult;
    @FXML
    private Button ok;

    @FXML
    private void changedSearchType() {
        if (searchfilter.getValue() == "Все") {
            searchfield.setDisable(true);
        } else {
            searchfield.setDisable(false);
        }
    }

    @FXML
    private void clickedSearchButton() {
        String type = (String) searchfilter.getValue();
        String searchReq = searchfield.getText();
        if (!checkSearchField(type, searchReq)) return;
        ObservableList<Book> searchList = FXCollections.observableArrayList();
        switch (type) {
            case ("Все"):
                searchList.addAll(bookRepository.findAll());
                break;
            case ("Название"):
                searchList.addAll(bookRepository.findByNameContainsIgnoreCase(searchReq));
                break;
            case ("Автор"):
                searchList.addAll(bookRepository.findByAuthorContainsIgnoreCase(searchReq));
                break;
            case ("Жанр"):
                searchList.addAll(bookRepository.findByGenreContainsIgnoreCase(searchReq));
                break;
        }
        searchresult.setItems(searchList);
    }

    private boolean checkSearchField(String type, String searchReq) {
        if (type.equals("Все")) {
            return true;
        } else {
            String symbol;
            if (FieldParser.isBlankString(searchReq)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Предупреждение");
                alert.setHeaderText(null);
                alert.getButtonTypes().set(0, new ButtonType("OK", ButtonBar.ButtonData.LEFT));
                alert.setContentText("Поле не должно быть пустым!");
                alert.showAndWait();
                return false;
            } else {
                if (type.equals("Название")) {
                    symbol = FieldParser.checkBookName(searchReq);
                } else {
                    if (type.equals("Автор")) {
                        symbol = FieldParser.checkAuthor(searchReq);
                    } else {
                        symbol = FieldParser.checkGenre(searchReq);
                    }
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
            }
        }
        return true;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> list = FXCollections.observableArrayList(
                Arrays.asList("Все", "Название", "Автор", "Жанр"));
        searchfilter.setItems(list);
        searchfilter.setValue("Все");
        searchfield.setDisable(true);
        initializeSearchTable();
    }

    public void initData(Lists list, ListsRepository listsRepository, BookRepository bookRepository) {
        this.list = list;
        this.listsRepository = listsRepository;
        this.bookRepository = bookRepository;
        ObservableList<Book> observeList = FXCollections.observableArrayList();
        observeList.addAll(bookRepository.findAll());

        searchresult.setItems(observeList);
        books = bookRepository.findByLists(list);
    }

    private void initializeSearchTable() {
        searchresult.setPlaceholder(new Label("Пока здесь нет книг :с"));
        searchresult.setEditable(true);

        TableColumn<Book, Integer> colAuthor = new TableColumn<>("Автор");
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));

        TableColumn<Book, String> colName = new TableColumn<>("Название");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setCellFactory(new Callback<TableColumn<Book, String>, TableCell<Book, String>>() {
            @Override
            public TableCell<Book, String> call(TableColumn<Book, String> col) {
                final TableCell<Book, String> cell = new TableCell<Book, String>() {
                    @Override
                    public void updateItem(String firstName, boolean empty) {
                        super.updateItem(firstName, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(firstName);
                        }
                    }
                };
                cell.setCursor(Cursor.HAND);
                return cell;
            }
        });

        TableColumn<Book, String> colGenre = new TableColumn<>("Жанр");
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));

        TableColumn<Book, Number> colRating = new TableColumn<>("Рейтинг");
        colRating.setCellValueFactory(new PropertyValueFactory<>("rate"));

        colRating.setCellFactory(table -> new TableCell<Book, Number>() {
            private final Rating rating;

            {
                rating = new Rating(5);
                rating.setMouseTransparent(true);
                rating.setFocusTraversable(false);
            }

            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(rating);
                }
            }

        });
        TableColumn<Book, String> colAdd = new TableColumn<>("");
        colAdd.setCellFactory(param -> {
            TableCell<Book, String> cell = new TableCell<Book, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        Book book = getTableView().getItems().get(getIndex());
                        if (books.contains(book)) {
                            setText("✓");
                        } else {
                            setText("+");
                        }

                    }
                }
            };
            cell.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                if (cell.getText().equals("+")) {
                    Book book = cell.getTableView().getItems().get(cell.getIndex());
                    book.getLists().add(list);
                    list.getBooks().add(book);
                    bookRepository.saveAndFlush(book);
                    listsRepository.saveAndFlush(list);
                    cell.setText("✓");
                    cell.setDisable(true);
                }
            });
            cell.setFont(Font.font(30));
            cell.setStyle("-fx-text-fill: green;");
            cell.setCursor(Cursor.HAND);
            return cell;
        });
        colAdd.setMaxWidth(40);
        colAdd.setMinWidth(40);
        searchresult.getColumns().addAll(colAuthor, colName, colGenre, colRating, colAdd);
    }

    public void clickedOkButton() {
        ok.getScene().getWindow().hide();
    }

}
