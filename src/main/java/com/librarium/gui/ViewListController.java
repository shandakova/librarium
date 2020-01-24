package com.librarium.gui;

import com.librarium.entity.Lists;
import com.librarium.repository.BookRepository;
import com.librarium.repository.ListsRepository;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class ViewListController implements Initializable {
    private BookRepository bookRepository;
    private ListsRepository listsRepository;
    private Lists list;

    public void initData(BookRepository bookRepository, ListsRepository listsRepository, Lists list) {
        this.list = list;
        this.bookRepository = bookRepository;
        this.listsRepository = listsRepository;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
