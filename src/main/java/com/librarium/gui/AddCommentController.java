package com.librarium.gui;

import com.librarium.entity.Book;
import com.librarium.entity.Comment;
import com.librarium.entity.Quote;
import com.librarium.repository.BookRepository;
import com.librarium.repository.CommentRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AddCommentController implements Initializable {
    private BookRepository bookRepository;
    private CommentRepository commentRepository;
    private Book book;

    @FXML
    private TextField addComment;
    @FXML
    private Button cancel;
    @FXML
    private Button saveComment;

    public void initData(BookRepository bookRepository, Book book, CommentRepository commentRepository) {
        this.book = book;
        this.bookRepository = bookRepository;
        this.commentRepository = commentRepository;
    }

    @FXML
    private void clickedSaveCommentButton() {
        Comment comment = new Comment();
        comment.setBook(book);
        comment.setComment(addComment.getText());
        commentRepository.save(comment);
        Stage stage = (Stage) saveComment.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void clickedCancelButton() {
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
