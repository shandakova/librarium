package com.librarium.gui;

import com.librarium.entity.Comment;
import com.librarium.repository.BookRepository;
import com.librarium.repository.CommentRepository;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class EditCommentController implements Initializable {
    private BookRepository bookRepository;
    private CommentRepository commentRepository;
    private Comment comment;

    public void initData(BookRepository bookRepository, CommentRepository commentRepository, Comment comment) {
        this.comment = comment;
        this.bookRepository = bookRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
