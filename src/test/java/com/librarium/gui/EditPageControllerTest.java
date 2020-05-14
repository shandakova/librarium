package com.librarium.gui;

import com.librarium.entity.Book;
import com.librarium.repository.BookRepository;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.matcher.base.WindowMatchers;

import java.awt.*;

import static junit.framework.TestCase.assertTrue;
import static org.loadui.testfx.controls.impl.VisibleNodesMatcher.visible;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

public class EditPageControllerTest extends GuiTest {
    @Mock
    private BookRepository bookRepository = mock(BookRepository.class);
    private EditPageController editPageController = mock(EditPageController.class);
    private BookInformationController bic = mock(BookInformationController.class);
    private Alert alert = mock(Alert.class);
    public Book book = new Book();

    @SneakyThrows
    @Before
    public void initMock() {
        Mockito.when(bookRepository.saveAndFlush(any(Book.class))).thenAnswer(i -> i.getArguments()[0]);
        MockitoAnnotations.initMocks(this);
    }

    @SneakyThrows
    @Override
    protected Parent getRootNode() {
        Parent parent = null;
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(AddCommentController.class.getResource("/fxml/editpage.fxml"));
        parent = loader.load();
        if (parent.getScene() == null) {
            stage.setScene(new Scene(parent));
        } else {
            stage.setScene(parent.getScene());
        }
        editPageController = loader.getController();
        book.setRate(5);
        book.setISBN("1324");
        book.setYear(1999);
        book.setGenre("romance");
        book.setName("test");
        book.setAuthor("Me");
        editPageController.initData(bookRepository, book, bic);
        stage.show();
        return parent;
    }

    @SneakyThrows
    @Test
    public void editBook_anyAuthor_bookUpdated() {
        setupStage();
        waitUntil("#author", visible());
        TextField textField = find("#author");
        String text = "adsfdghj";
        textField.setText(text);
        click("#okBtn");
        ArgumentCaptor<Book> argument = ArgumentCaptor.forClass(Book.class);
        Mockito.verify(bookRepository).saveAndFlush(argument.capture());
        bic.update();
        assertTrue(argument.getValue().getAuthor().equals(text));
        closeCurrentWindow();
    }

    @SneakyThrows
    @Test
    public void editBook_wrongAuthor_alertShowing() {
        setupStage();
        waitUntil("#author", visible());
        TextField textField = find("#author");
        String text = "124";
        textField.setText(text);
        click("#okBtn");
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        closeCurrentWindow();
    }

    @SneakyThrows
    @Test
    public void editBook_wrongTitle_alertShowing() {
        setupStage();
        waitUntil("#title", visible());
        TextField textField = find("#title");
        String text = "é";
        textField.setText(text);
        click("#okBtn");
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        closeCurrentWindow();
    }

    @SneakyThrows
    @Test
    public void editBook_wrongGenre_alertShowing() {
        setupStage();
        waitUntil("#genre", visible());
        TextField textField = find("#genre");
        String text = "2";
        textField.setText(text);
        click("#okBtn");
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        closeCurrentWindow();
    }

    @SneakyThrows
    @Test
    public void editBook_wrongYear_alertShowing() {
        setupStage();
        waitUntil("#year", visible());
        TextField textField = find("#year");
        String text = "26";
        textField.setText(text);
        click("#okBtn");
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        closeCurrentWindow();
    }

    @SneakyThrows
    @Test
    public void editBook_wrongISBN_alertShowing() {
        setupStage();
        waitUntil("#isbn", visible());
        TextField textField = find("#isbn");
        String text = "74e";
        textField.setText(text);
        click("#okBtn");
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        closeCurrentWindow();
    }

    @SneakyThrows
    @Test
    public void editBook_blankTitle_alertShowing() {
        setupStage();
        waitUntil("#title", visible());
        TextField textField = find("#title");
        String text = "";
        textField.setText(text);
        click("#okBtn");
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        closeCurrentWindow();
    }

    @SneakyThrows
    @Test
    public void deleteBook_click_alertShowing() {
        setupStage();
        waitUntil("#delBtn", visible());
        click("#delBtn");
        FxAssert.verifyThat(new FxRobot().window("Удаление"), WindowMatchers.isShowing());
        Point p = MouseInfo.getPointerInfo().getLocation();
        move(p.x + 35, p.y - 245);
        click(MouseButton.PRIMARY);
        closeCurrentWindow();
    }
}
