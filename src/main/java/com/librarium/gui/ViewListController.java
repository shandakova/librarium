package com.librarium.gui;

import com.librarium.entity.Book;
import com.librarium.entity.Lists;
import com.librarium.repository.BookRepository;
import com.librarium.repository.ListsRepository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class ViewListController implements Initializable {
    private BookRepository bookRepository;
    private ListsRepository listsRepository;
    private Lists list;
    @FXML
    private Label listName;
    @FXML
    private TableView<Book> bookTable;
    @FXML
    private Button addBtn;

    public void initData(BookRepository bookRepository, ListsRepository listsRepository, Lists list) {
        this.list = list;
        this.bookRepository = bookRepository;
        this.listsRepository = listsRepository;
        listName.setText(list.getName());
        bookTable.setItems(FXCollections.observableArrayList(list.getBooks()));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bookTable.setPlaceholder(new javafx.scene.control.Label(""));
        bookTable.setEditable(true);

        TableColumn<Book, String> colAuthor = new TableColumn<>("Автор");
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        TableColumn<Book, String> colTitle = new TableColumn<>("Название");
        colTitle.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Book, String> colAdd = new TableColumn<>("");
        colAdd.setCellFactory(param -> {
            TableCell<Book, String> cell = new TableCell<Book, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText("-");
                        setCursor(Cursor.HAND);
                    }
                }
            };

            cell.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                if (cell.getText().equals("-")) {
                    Book book = cell.getTableView().getItems().get(cell.getIndex());
                    book.getLists().remove(list);
                    bookRepository.saveAndFlush(book);
                    bookTable.getItems().remove(book);
                }
            });
            cell.setFont(Font.font(30));
            cell.setStyle("-fx-text-fill: red;");
            return cell;
        });
        colAdd.setMaxWidth(40);
        colAdd.setMinWidth(40);
        bookTable.getColumns().addAll(colAuthor, colTitle, colAdd);
    }

    public void clickedDelete() {
        Set<Book> books = new HashSet(bookRepository.findByLists(list));
        for (Book b : books) {
            b.getLists().remove(list);
            bookRepository.saveAndFlush(b);
        }
        list.setBooks(Collections.emptySet());
        listsRepository.saveAndFlush(list);
        listsRepository.delete(list);
        bookTable.getScene().getWindow().hide();
    }

    public void clickedAdd() {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/searchaddbookinlist.fxml"));
        try {
            Parent root = loader.load();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Добавление книги в лист");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(addBtn.getScene().getWindow());
        SearchAddBookInListController controller = loader.getController();
        controller.initData(list, listsRepository, bookRepository);
        stage.setOnHidden(e->update());
        stage.show();
    }
    private void update(){
        bookTable.getItems().remove(0,bookTable.getItems().size());
        Lists list = listsRepository.getOne(this.list.getId());
        bookTable.setItems(FXCollections.observableArrayList(bookRepository.findByLists(list)));
    }
}
