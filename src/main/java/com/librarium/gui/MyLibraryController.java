package com.librarium.gui;

import com.librarium.entity.Book;
import com.librarium.repository.BookRepository;
import com.librarium.repository.CommentRepository;
import com.librarium.repository.ListsRepository;
import com.librarium.repository.QuoteRepository;
import com.librarium.utils.FieldParser;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.SneakyThrows;
import org.controlsfx.control.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.ResourceBundle;

@Controller
public class MyLibraryController implements Initializable {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private QuoteRepository quoteRepository;
    @Autowired
    private ListsRepository listsRepository;
    @FXML
    private ComboBox searchTypeComboBox;
    @FXML
    private TextField searchTextField;
    @FXML
    private TableView<Book> searchTable;
    private MyLibraryController mlc = this;
    @Autowired
    private ListsController lc;

    public void init(BookRepository bookRepository, CommentRepository commentRepository, QuoteRepository quoteRepository,
                     ListsRepository listsRepository, ListsController lc) {
        this.bookRepository = bookRepository;
        this.commentRepository = commentRepository;
        this.quoteRepository = quoteRepository;
        this.listsRepository = listsRepository;
        this.lc = lc;
    }

    @FXML
    private void changedSearchType() {
        searchTextField.setDisable(searchTypeComboBox.getValue() == "Все");
    }

    @FXML
    private void clickedSearchButton() {
        String type = (String) searchTypeComboBox.getValue();
        String searchReq = searchTextField.getText();
        if (!checkSearchField(type, searchReq)) return;
        ObservableList<Book> searchList = FXCollections.observableArrayList();
        if ("Все".equals(type)) {
            searchList.addAll(bookRepository.findAll());
        } else if ("Название".equals(type)) {
            searchList.addAll(bookRepository.findByNameContainsIgnoreCase(searchReq));
        } else if ("Автор".equals(type)) {
            searchList.addAll(bookRepository.findByAuthorContainsIgnoreCase(searchReq));
        } else {
            searchList.addAll(bookRepository.findByGenreContainsIgnoreCase(searchReq));
        }
        searchTable.setItems(searchList);
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
        searchTypeComboBox.setItems(list);
        searchTypeComboBox.setValue("Все");
        searchTextField.setDisable(true);
        initializeSearchTable();
    }

    private void initializeSearchTable() {
        searchTable.setPlaceholder(new Label("Пока здесь нет книг :с"));
        searchTable.setEditable(true);
        ObservableList<Book> observeList = FXCollections.observableArrayList();
        if (bookRepository != null) observeList.addAll((Collection<? extends Book>) bookRepository.findAll());

        searchTable.setItems(observeList);
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
                        setText(firstName);
                    }
                };
                cell.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    @SneakyThrows
                    public void handle(MouseEvent event) {
                        Book book = cell.getTableView().getItems().get(cell.getIndex());
                        Stage stage = new Stage();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/bookInformation.fxml"));
                        stage.setScene(new Scene(loader.load()));
                        stage.setTitle("Информация о книге");
                        stage.initModality(Modality.WINDOW_MODAL);
                        stage.initOwner(
                                ((Node) event.getSource()).getScene().getWindow());
                        BookInformationController controller = loader.getController();
                        controller.initData(bookRepository, commentRepository, quoteRepository, listsRepository, book, mlc, lc);
                        stage.setOnCloseRequest(e -> update());
                        stage.show();
                    }
                });
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
            private final ChangeListener<Number> ratingChangeListener;

            {
                rating = new Rating(5);
                ratingChangeListener = (observable, oldValue, newValue) -> {
                    TableColumn<?, Number> column = getTableColumn();
                    Book book = this.getTableView().getItems().get(this.getIndex());
                    book.setRate(newValue.intValue());
                    bookRepository.save(book);
                };
            }

            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                rating.ratingProperty().removeListener(ratingChangeListener);
                setGraphic(null);
                if (!empty) {
                    rating.setRating(item.doubleValue());
                    rating.ratingProperty().addListener(ratingChangeListener);
                    setGraphic(rating);
                }
            }
        });
        searchTable.getColumns().addAll(colAuthor, colName, colGenre, colRating);
    }

    public void update() {
        searchTable.getItems().remove(0, searchTable.getItems().size());
        clickedSearchButton();
    }
}
