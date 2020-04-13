package com.librarium.gui;

import com.librarium.entity.Book;
import com.librarium.entity.Comment;
import com.librarium.repository.CommentRepository;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.loadui.testfx.GuiTest;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertTrue;
import static org.loadui.testfx.controls.impl.VisibleNodesMatcher.visible;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AddCommentControllerTest extends GuiTest {
    private CommentRepository commentRepository = mock(CommentRepository.class);
    private AddCommentController addCommentController;
    final protected FXMLLoader loader = new FXMLLoader(AddCommentController.class.getResource("/fxml/addcomment.fxml"));
    protected Parent parent;

    @SneakyThrows
    @Before
    public void initMock() {
        Mockito.when(commentRepository.saveAndFlush(any(Comment.class))).thenAnswer(i -> i.getArguments()[0]);
        MockitoAnnotations.initMocks(this);
        addCommentController.initData(new Book(), commentRepository);
        setupStage();
    }

    @SneakyThrows
    @Override
    protected Parent getRootNode() {
        if (parent == null) parent = loader.load();
        stage = new Stage();
        stage.setScene(parent.getScene());
        stage.show();
        if (addCommentController == null) addCommentController = loader.getController();
        return parent;
    }

    @Test
    public void saveComment_anyText_commentSaved() {
        waitUntil("#textArea", visible());
        TextArea textArea = find("#textArea");
        String text = "adsfdghj";
        textArea.setText(text);
        click("#saveComment");
        ArgumentCaptor<Comment> argument = ArgumentCaptor.forClass(Comment.class);
        Mockito.verify(commentRepository).saveAndFlush(argument.capture());
        assertTrue(argument.getValue().getComment().equals(text));
    }

    @Test
    public void cancel_click_stageNotShowing() {
        click("#cancel");
    }

}
