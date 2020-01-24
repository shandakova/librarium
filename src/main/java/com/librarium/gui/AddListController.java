package com.librarium.gui;

import com.librarium.entity.Book;
import com.librarium.repository.BookRepository;
import com.librarium.repository.ListsRepository;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class AddListController implements Initializable {
    private BookRepository bookRepository;
    private ListsRepository listsRepository;
    private Book book;

    public void initData(BookRepository bookRepository, Book book, ListsRepository listsRepository) {
        this.book = book;
        this.bookRepository = bookRepository;
        this.listsRepository = listsRepository;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
