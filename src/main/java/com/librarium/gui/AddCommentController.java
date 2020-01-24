package com.librarium.gui;

import com.librarium.entity.Book;
import com.librarium.repository.BookRepository;
import com.librarium.repository.CommentRepository;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class AddCommentController implements Initializable {
    private BookRepository bookRepository;
    private CommentRepository commentRepository;
    private Book book;

    public void initData(BookRepository bookRepository, Book book, CommentRepository commentRepository) {
        this.book = book;
        this.bookRepository = bookRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
