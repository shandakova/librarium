package com.librarium.gui;

import com.librarium.entity.Book;
import com.librarium.entity.Comment;
import com.librarium.repository.CommentRepository;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

public class AddCommentControllerTest {
    @Mock
    private CommentRepository commentRepository = mock(CommentRepository.class);
    private AddCommentController addCommentController;
//    GuiTest guiTest;

    @SneakyThrows
    @Before
    public void initMock() {
        Mockito.when(commentRepository.saveAndFlush(any(Comment.class))).thenAnswer(i -> i.getArguments()[0]);
        MockitoAnnotations.initMocks(this);
    }

    public GuiTest getGuiTest() {
        return new GuiTest() {
            @SneakyThrows
            @Override
            protected Parent getRootNode() {
                Parent parent = null;
                FXMLLoader loader = new FXMLLoader(AddCommentController.class.getResource("/fxml/addcomment.fxml"));
                parent = (Parent) loader.load();
                addCommentController = loader.getController();
                addCommentController.initData(new Book(), commentRepository);
                return parent;
            }
        };
    }

    @SneakyThrows
    @Test
    public void saveComment() {
        GuiTest guiTest = getGuiTest();
        guiTest.setupStage();
        TextArea textArea = guiTest.find("#textArea");
        String text = "adsfdghj";
        textArea.setText(text);
        guiTest.click("#saveComment");
        ArgumentCaptor<Comment> argument = ArgumentCaptor.forClass(Comment.class);
        Mockito.verify(commentRepository).saveAndFlush(argument.capture());
        assertTrue(argument.getValue().getComment().equals(text));
    }

    @SneakyThrows
    @Test
    public void cancel() {
        GuiTest guiTest = getGuiTest();
        guiTest.setupStage();
        guiTest.click("#cancel");
    }

}
