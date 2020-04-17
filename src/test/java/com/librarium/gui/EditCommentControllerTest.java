package com.librarium.gui;

import com.librarium.entity.Comment;
import com.librarium.repository.CommentRepository;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static junit.framework.TestCase.assertTrue;
import static org.loadui.testfx.controls.impl.VisibleNodesMatcher.visible;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

public class EditCommentControllerTest extends GuiTest {
    @Mock
    private CommentRepository commentRepository = mock(CommentRepository.class);
    private EditCommentController editQuoteController = mock(EditCommentController.class);
    public Comment comment = new Comment();
    
    @Before
    public void initMock() {
        Mockito.when(commentRepository.saveAndFlush(any(Comment.class))).thenAnswer(i -> i.getArguments()[0]);
        MockitoAnnotations.initMocks(this);
    }

    @SneakyThrows
    @Override
    protected Parent getRootNode() {
        Parent parent = null;
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(AddCommentController.class.getResource("/fxml/editcomment.fxml"));
        parent = loader.load();
        if (parent.getScene() == null) {
            stage.setScene(new Scene(parent));
        } else {
            stage.setScene(parent.getScene());
        }
        editQuoteController = loader.getController();
        //Quote quote = new Quote();
        comment.setComment("fhsduifh");
        editQuoteController.initData(commentRepository, comment);
        stage.show();
        return parent;
    }

    @SneakyThrows
    @Test
    public void editComment_anyText_commentUpdated() {
        setupStage();
        waitUntil("#editComment", visible());
        TextArea textArea = find("#editComment");
        String text = "adsfdghj";
        textArea.setText(text);
        click("#updateComment");
        ArgumentCaptor<Comment> argument = ArgumentCaptor.forClass(Comment.class);
        Mockito.verify(commentRepository).saveAndFlush(argument.capture());
        assertTrue(argument.getValue().getComment().equals(text));
        closeCurrentWindow();
    }

    @SneakyThrows
    @Test
    public void deleteComment_anyText_commentDeleted() {
        setupStage();
        waitUntil("#deleteComment", visible());
        click("#deleteComment");
        ArgumentCaptor<Comment> argument = ArgumentCaptor.forClass(Comment.class);
        Mockito.verify(commentRepository).delete(argument.capture());
        assertTrue(argument.getValue().getComment().equals("fhsduifh"));
        closeCurrentWindow();
    }
}
