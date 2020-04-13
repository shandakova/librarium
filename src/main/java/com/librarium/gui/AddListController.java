package com.librarium.gui;

import com.librarium.entity.Book;
import com.librarium.entity.Lists;
import com.librarium.repository.BookRepository;
import com.librarium.repository.ListsRepository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;

import java.util.ArrayList;
import java.util.List;

public class AddListController {
    private BookRepository bookRepository;
    private ListsRepository listsRepository;
    private Book book;
    @FXML
    private ComboBox list;

    public void initData(BookRepository bookRepository, Book book, ListsRepository listsRepository) {
        this.book = book;
        this.bookRepository = bookRepository;
        this.listsRepository = listsRepository;
        List<Lists> lists = listsRepository.findAll();
        List<String> names = new ArrayList<>();
        for (Lists l : lists) {
            names.add(l.getName());
        }
        names.add("");
        list.setItems(FXCollections.observableArrayList(names));
        list.setValue("");
    }


    public void clickedAdd() {
        String name = (String) list.getValue();
        if (name.equals("")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Предупреждение");
            alert.setHeaderText(null);
            alert.getButtonTypes().set(0, new ButtonType("OK", ButtonBar.ButtonData.LEFT));
            alert.setContentText("Лист для добавления не выбран! ");
            alert.showAndWait();
            return;
        }
        Lists l = listsRepository.findByName(name).get(0);
        if (l.getBooks().contains(book)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Предупреждение");
            alert.setHeaderText(null);
            alert.getButtonTypes().set(0, new ButtonType("OK", ButtonBar.ButtonData.LEFT));
            alert.setContentText("Книга уже находится в этом листе!");
            alert.showAndWait();
        } else {
            bookRepository.flush();
            book = bookRepository.findById(book.getId()).get();
            book.getLists().add(l);
            bookRepository.saveAndFlush(book);
            list.getScene().getWindow().hide();
        }
    }

    public void clickedCancel() {
        list.getScene().getWindow().hide();
    }
}
