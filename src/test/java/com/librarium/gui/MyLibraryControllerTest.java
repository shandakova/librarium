package com.librarium.gui;

import com.librarium.entity.Book;
import com.librarium.repository.*;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.matcher.base.WindowMatchers;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.loadui.testfx.controls.impl.VisibleNodesMatcher.visible;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

public class MyLibraryControllerTest extends GuiTest {
    final protected FXMLLoader loader = new FXMLLoader(AddBookController.class.getResource("/fxml/mylibrary.fxml"));
    protected Parent parent;
    private MyLibraryController mlc;
    @Mock
    private BookApiRepository bar = mock(BookApiRepository.class);
    @Mock
    private BookRepository bookRepository = mock(BookRepository.class);
    @Mock
    private CommentRepository commentRepository = mock(CommentRepository.class);
    @Mock
    private ListsRepository listsRepository = mock(ListsRepository.class);
    @Mock
    private QuoteRepository quoteRepository = mock(QuoteRepository.class);
    @Mock
    private ListsController listsController = mock(ListsController.class);

    @Before
    public void initMocks() {
        Mockito.doReturn(Collections.singletonList(new Book("all"))).when(bookRepository).findAll();
        Mockito.when(bookRepository.findByNameContainsIgnoreCase(anyString())).then(i -> {
            String arg = (String) i.getArguments()[0];
            Book book = new Book(arg);
            return Collections.singletonList(book);
        });
        Mockito.when(bookRepository.findByAuthorContainsIgnoreCase(anyString())).then(i -> {
            String arg = (String) i.getArguments()[0];
            Book book = new Book("Title");
            book.setAuthor(arg);
            return Collections.singletonList(book);
        });
        Mockito.when(bookRepository.findByGenreContainsIgnoreCase(anyString())).then(i -> {
            String arg = (String) i.getArguments()[0];
            Book book = new Book("Title");
            book.setGenre(arg);
            return Collections.singletonList(book);
        });
        Mockito.when(bookRepository.save(any(Book.class))).thenAnswer(i -> i.getArgument(0));
        mlc.init(bookRepository, commentRepository, quoteRepository, listsRepository, listsController);
    }

    @Test
    public void myLibrary_clickFindAll_BookShows() {
        waitUntil("#searchBtn", visible());
        clickOnSearchComboBox(1);
        click("#searchBtn");
        TableView tv = find("#searchTable");
        Book book = (Book) tv.getItems().get(0);
        assertEquals("all", book.getName());
    }

    @Test
    public void myLibrary_clickFindTitle_BookShows() {
        waitUntil("#searchBtn", visible());
        clickOnSearchComboBox(2);
        setTextToSearchTextField("Normal Title");
        click("#searchBtn");
        TableView tv = find("#searchTable");
        Book book = (Book) tv.getItems().get(0);
        assertEquals("Normal Title", book.getName());
    }

    @Test
    public void myLibrary_clickFindAuthor_BookShows() {
        waitUntil("#searchBtn", visible());
        clickOnSearchComboBox(3);
        setTextToSearchTextField("Author");
        click("#searchBtn");
        TableView tv = find("#searchTable");
        Book book = (Book) tv.getItems().get(0);
        assertEquals("Author", book.getAuthor());
    }

    @Test
    public void myLibrary_clickFindGenre_BookShows() {
        waitUntil("#searchBtn", visible());
        clickOnSearchComboBox(4);
        setTextToSearchTextField("some genre");
        click("#searchBtn");
        TableView tv = find("#searchTable");
        Book book = (Book) tv.getItems().get(0);
        assertEquals("some genre", book.getGenre());
    }

    @Test
    public void myLibrary_clickFindWrongAuthor_warningWindowShows() {
        waitUntil("#searchBtn", visible());
        clickOnSearchComboBox(3);
        setTextToSearchTextField("12412");
        click("#searchBtn");
        TableView tv = find("#searchTable");
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        click("OK");
    }

    @Test
    public void myLibrary_clickManyTimesOnComboBox_searchTextFieldChange() {
        waitUntil("#searchBtn", visible());
        clickOnSearchComboBox(3);
        clickOnSearchComboBox(1);
        clickOnSearchComboBox(2);
    }

    @Test
    public void myLibrary_clickFindEmptyAuthor_warningWindowShows() {
        waitUntil("#searchBtn", visible());
        clickOnSearchComboBox(3);
        setTextToSearchTextField("");
        click("#searchBtn");
        TableView tv = find("#searchTable");
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        click("OK");
    }

    public void clickOnSearchComboBox(int typeOrder) {
        click("#searchTypeComboBox");
        Point p = MouseInfo.getPointerInfo().getLocation();
        move(p.x, p.y + 25 * typeOrder);
        click(MouseButton.PRIMARY);
        sleep(1000);
    }

    public void setTextToSearchTextField(String text) {
        TextField textField = find("#searchTextField");
        textField.setText(text);
    }

    @Test
    public void myLibrary_clickAtRating_BookShows() {
        waitUntil("#searchBtn", visible());
        clickOnSearchComboBox(1);
        click("#searchBtn");
        Node node = new FxRobot().lookup("#searchTable").lookup(".table-row-cell").nth(0).query();
        Bounds boundsInScreen = node.localToScreen(node.getBoundsInLocal());
        move(boundsInScreen.getMaxX() - 120, boundsInScreen.getMinY() + 10);
        click(MouseButton.PRIMARY);
        Mockito.verify(bookRepository, times(1)).save(any());
    }

    @Test
    public void myLibrary_clickAtNameOfBook_InfoBookShows() {
        waitUntil("#searchBtn", visible());
        clickOnSearchComboBox(1);
        click("#searchBtn");
        Node node = new FxRobot().lookup("#searchTable").lookup(".table-row-cell").nth(0).query();
        click(node);
        FxAssert.verifyThat(new FxRobot().window("Информация о книге"), WindowMatchers.isShowing());
        click(new FxRobot().window("Информация о книге"));
        double x = new FxRobot().window("Информация о книге").getX() + new FxRobot().window("Информация о книге").getWidth();
        double y = new FxRobot().window("Информация о книге").getY();
        move(x - 15, y + 10);
        click(MouseButton.PRIMARY);
        sleep(1000);
    }

    @SneakyThrows
    @Override
    protected Parent getRootNode() {
        if (parent == null) parent = loader.load();
        if (mlc == null) mlc = loader.getController();
        initMocks();
        return parent;
    }

}