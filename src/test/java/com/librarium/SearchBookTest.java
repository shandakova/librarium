package com.librarium;

import com.librarium.entity.Book;
import com.librarium.gui.MainWindowController;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import lombok.SneakyThrows;
import org.junit.BeforeClass;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.loadui.testfx.utils.FXTestUtils;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.matcher.base.WindowMatchers;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.loadui.testfx.GuiTest.find;
import static org.loadui.testfx.controls.impl.VisibleNodesMatcher.visible;

public class SearchBookTest {
    final static protected FXMLLoader loader = new FXMLLoader(MainWindowController.class.getResource("/fxml/mainwindow.fxml"));
    protected static Parent parent;
    private static GuiTest guiTest;

    @SneakyThrows
    @BeforeClass
    public static void init() {
        guiTest = new GuiTest() {
            @Override
            @SneakyThrows
            protected Parent getRootNode() {
                parent = (Parent) loader.load();
                return parent;
            }
        };
        FXTestUtils.launchApp(MainApplication.class);
        guiTest.sleep(40000);
        addManyBooks();
    }

    public static void addManyBooks() {
        guiTest.waitUntil("#searchBtn", visible());
        guiTest.click("#AddBook");
        guiTest.sleep(3000);
        ((TextField) find("#addTitle")).setText("Title1");
        ((TextField) find("#addGenre")).setText("Other");
        guiTest.click("#addBook");
        guiTest.click("OK");
        ((TextField) find("#addTitle")).setText("Title2");
        ((TextField) find("#addAuthor")).setText("Elon Musk");
        guiTest.click("#addBook");
        guiTest.click("OK");
        ((TextField) find("#addTitle")).setText("Title3");
        ((TextField) find("#addAuthor")).setText("Elon Musk");
        guiTest.click("#addBook");
        guiTest.click("OK");
        guiTest.click("#myLibrary");
    }

    @Test
    public void findAllBooks_3BooksExpected() {
        clickOnSearchComboBox(1);
        guiTest.click("#searchBtn");
        guiTest.sleep(1000);
        TableView tv = find("#searchTable");
        ObservableList books = tv.getItems();
        assertEquals(3, books.size());
        books.forEach(book -> {
            assertTrue(((Book) book).getName().contains("Title"));
        });
    }

    public void clickOnSearchComboBox(int typeOrder) {
        guiTest.click("#searchTypeComboBox");
        Point p = MouseInfo.getPointerInfo().getLocation();
        guiTest.move(p.x, p.y + 25 * typeOrder);
        guiTest.click(MouseButton.PRIMARY);
        guiTest.sleep(1000);
    }

    public void setTextToSearchTextField(String text) {
        TextField textField = find("#searchTextField");
        textField.setText(text);
    }

    @Test
    public void findByTitleBooks_1BooksExpected() {
        clickOnSearchComboBox(2);
        setTextToSearchTextField("Title2");
        guiTest.click("#searchBtn");
        guiTest.sleep(1000);
        TableView tv = find("#searchTable");
        ObservableList books = tv.getItems();
        assertEquals(1, books.size());
        Book book = (Book) tv.getItems().get(0);
        assertEquals("Title2", book.getName());
        assertEquals("Elon Musk", book.getAuthor());
    }

    @Test
    public void findByGenreBooks_1BooksExpected() {
        clickOnSearchComboBox(4);
        setTextToSearchTextField("Other");
        guiTest.click("#searchBtn");
        guiTest.sleep(1000);
        TableView tv = find("#searchTable");
        ObservableList books = tv.getItems();
        assertEquals(1, books.size());
        Book book = (Book) tv.getItems().get(0);
        assertEquals("Title1", book.getName());
        assertEquals("Other", book.getGenre());
    }

    @Test
    public void findByAuthorBooks_2BooksExpected() {
        clickOnSearchComboBox(3);
        setTextToSearchTextField("Elon Musk");
        guiTest.click("#searchBtn");
        guiTest.sleep(1000);
        TableView tv = find("#searchTable");
        ObservableList books = tv.getItems();
        assertEquals(2, books.size());
        books.forEach(book -> {
            assertTrue(((Book) book).getAuthor().contains("Elon Musk"));
        });
    }

    @Test
    public void findWrongAuthor_warningWindowShows() {
        clickOnSearchComboBox(3);
        setTextToSearchTextField("12412");
        guiTest.click("#searchBtn");
        TableView tv = find("#searchTable");
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        guiTest.click("OK");
    }

    @Test
    public void findEmptyAuthor_warningWindowShows() {
        clickOnSearchComboBox(3);
        setTextToSearchTextField("");
        guiTest.click("#searchBtn");
        TableView tv = find("#searchTable");
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        guiTest.click("OK");
    }
}