package com.librarium.gui;

import com.librarium.entity.Book;
import com.librarium.entity.Comment;
import com.librarium.repository.CommentRepository;
import javafx.application.Platform;
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
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.concurrent.TimeUnit;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.junit.Assert.assertTrue;
import static org.loadui.testfx.controls.impl.VisibleNodesMatcher.visible;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

public class AddCommentControllerTest extends GuiTest {
    @Mock
    private CommentRepository commentRepository = mock(CommentRepository.class);
    private AddCommentController addCommentController = mock(AddCommentController.class);
    //public Stage stage = mock(Stage.class);
    //public GuiTest guiTest;

    @SneakyThrows
    @Before
    public void initMock() {
        Mockito.when(commentRepository.saveAndFlush(any(Comment.class))).thenAnswer(i -> i.getArguments()[0]);
        //Mockito.doNothing().when(addCommentController).close(any(Stage.class));
        MockitoAnnotations.initMocks(this);
    }

    /*public GuiTest getGuiTest() {
        return new GuiTest() {*/
            @SneakyThrows
            @Override
            protected Parent getRootNode() {
                Parent parent = null;
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(AddCommentController.class.getResource("/fxml/addcomment.fxml"));
                parent = loader.load();
                //Scene scene = parent.getScene();
                if (parent.getScene() == null) {
                stage.setScene(new Scene(parent));
                } else {
                    stage.setScene(parent.getScene());
                }
                addCommentController = loader.getController();
                addCommentController.initData(new Book(), commentRepository);
                stage.show();
                return parent;
                /*Parent parent = null;
                FXMLLoader loader = new FXMLLoader(AddCommentController.class.getResource("/fxml/addcomment.fxml"));
                parent = loader.load();
                addCommentController = loader.getController();
                addCommentController.initData(new Book(), commentRepository);
                return parent;*/
            }
        /*};
    }*/

    @SneakyThrows
    @Test
    public void saveComment() {
        //guiTest = getGuiTest();
        setupStage();
        waitUntil("#textArea", visible());
        TextArea textArea = find("#textArea");
        String text = "adsfdghj";
        textArea.setText(text);
        click("#saveComment");
        ArgumentCaptor<Comment> argument = ArgumentCaptor.forClass(Comment.class);
        Mockito.verify(commentRepository).saveAndFlush(argument.capture());
        assertTrue(argument.getValue().getComment().equals(text));
        closeCurrentWindow();
    }

    @SneakyThrows
    @Test
    public void cancel() {
        //guiTest = getGuiTest();
        setupStage();
        //Platform.setImplicitExit(false);
        click("#cancel");
        closeCurrentWindow();
    }

}
