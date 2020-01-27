package com.librarium.gui;

import com.librarium.entity.Lists;
import com.librarium.entity.Book;
import com.librarium.repository.BookRepository;
import com.librarium.repository.ListsRepository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
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
            };
            cell.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                Book book = cell.getTableView().getItems().get(cell.getIndex());
                list.getBooks().remove(book);
                book.getLists().remove(list);
                bookRepository.saveAndFlush(book);
                listsRepository.saveAndFlush(list);
                bookTable.getItems().remove(book);
            });
            cell.setText("-");
            cell.setFont(Font.font(30));
            cell.setStyle("-fx-text-fill: red;");
            cell.setCursor(Cursor.HAND);
            return cell;
        });
        colAdd.setMaxWidth(40);
        colAdd.setMinWidth(40);
        bookTable.getColumns().addAll(colAuthor,colTitle, colAdd);
    }

    public void clickedDelete() {
        Set<Book> books = list.getBooks();
        for (Book b: books){
            b.getLists().remove(list);
            bookRepository.saveAndFlush(b);
        }
        list.setBooks(Collections.<Book>emptySet());
        listsRepository.saveAndFlush(list);
        listsRepository.delete(list);
        bookTable.getScene().getWindow().hide();
    }
    public void clickedAdd() {

    }
}
