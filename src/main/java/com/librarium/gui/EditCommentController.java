package com.librarium.gui;

import com.librarium.entity.Comment;
import com.librarium.repository.CommentRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class EditCommentController implements Initializable {
    private CommentRepository commentRepository;
    private Comment comment;

    @FXML
    private TextArea editComment;
    @FXML
    private Button deleteComment;
    @FXML
    private Button updateComment;


    public void initData(CommentRepository commentRepository, Comment comment) {
        this.comment = comment;
        this.commentRepository = commentRepository;
        editComment.setText(comment.getComment());
    }

    @FXML
    private void clickedUpdateCommentButton() {
        comment.setComment(editComment.getText());
        commentRepository.saveAndFlush(comment);
        Stage stage = (Stage) updateComment.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void clickedDeleteCommentButton() {
        commentRepository.delete(comment);
        Stage stage = (Stage) deleteComment.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
