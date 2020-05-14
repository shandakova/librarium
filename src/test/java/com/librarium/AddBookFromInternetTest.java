package com.librarium;

import com.librarium.entity.Book;
import com.librarium.gui.MainWindowController;
import io.sniffy.socket.DisableSockets;
import io.sniffy.test.junit.SniffyRule;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import lombok.SneakyThrows;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.loadui.testfx.GuiTest;
import org.loadui.testfx.utils.FXTestUtils;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.matcher.base.WindowMatchers;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.loadui.testfx.controls.impl.VisibleNodesMatcher.visible;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AddBookFromInternetTest {
    final static protected FXMLLoader loader = new FXMLLoader(MainWindowController.class.getResource("/fxml/mainwindow.fxml"));
    protected static Parent parent;
    private static GuiTest guiTest;
    @Rule
    public SniffyRule sniffyRule = new SniffyRule();

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
        guiTest.sleep(35000);
        guiTest.click("#AddBook");

    }

    @Test
    @DisableSockets
    public void noConnection_errorWindowShowing() {
        guiTest.sleep(1000);
        FxAssert.verifyThat(new FxRobot().window("Ошибка"), WindowMatchers.isShowing());
        guiTest.click("OK");
    }

    @Before
    public void clickOnInternetSearch() {
        guiTest.waitUntil("#searchInternet", visible());
        guiTest.click("#searchInternet");
    }

    @Test
    public void searchAuthorInInternet() {
        guiTest.waitUntil("#searchTextField", visible());
        TextField textField = guiTest.find("#searchTextField");
        clickOnSearchComboBox(3);
        String text = "Rowling";
        textField.setText(text);
        guiTest.click("#search");
        TableView tv = guiTest.find("#searchTable");
        guiTest.sleep(1000);
        assertTrue(tv.getItems().size() != 0);
        Book book = (Book) tv.getItems().get(0);
        clickOnFirstItemInTable();
        String author = ((TextField) guiTest.find("#addAuthor")).getText();
        String name = ((TextField) guiTest.find("#addTitle")).getText();
        System.out.println(author);
        assertTrue(author.contains(book.getAuthor()));
        assertTrue(name.contains(book.getName()));
    }

    @Test
    public void searchTitleInInternet() {
        guiTest.waitUntil("#searchTextField", visible());
        TextField textField = guiTest.find("#searchTextField");
        clickOnSearchComboBox(2);
        String text = "Flower";
        textField.setText(text);
        guiTest.click("#search");
        TableView tv = guiTest.find("#searchTable");
        guiTest.sleep(1000);
        assertTrue(tv.getItems().size() != 0);
        Book book = (Book) tv.getItems().get(0);
        clickOnFirstItemInTable();
        String author = ((TextField) guiTest.find("#addAuthor")).getText();
        String name = ((TextField) guiTest.find("#addTitle")).getText();
        System.out.println(author);
        assertTrue(author.contains(book.getAuthor()));
        assertTrue(name.contains(book.getName()));
    }

    @Test
    public void searchISBNInInternet() {
        guiTest.waitUntil("#searchTextField", visible());
        TextField textField = guiTest.find("#searchTextField");
        clickOnSearchComboBox(1);
        String text = "9780472096732";
        textField.setText(text);
        guiTest.click("#search");
        TableView tv = guiTest.find("#searchTable");
        guiTest.sleep(1000);
        assertTrue(tv.getItems().size() != 0);
        Book book = (Book) tv.getItems().get(0);
        clickOnFirstItemInTable();
        String name = ((TextField) guiTest.find("#addTitle")).getText();
        String isbn = ((TextField) guiTest.find("#addISBN")).getText();
        assertTrue(isbn.contains(book.getISBN()));
        assertTrue(name.contains(book.getName()));
    }

    @Test
    public void searchGenreInInternet() {
        guiTest.waitUntil("#searchTextField", visible());
        TextField textField = guiTest.find("#searchTextField");
        clickOnSearchComboBox(4);
        String text = "Computers";
        textField.setText(text);
        guiTest.click("#search");
        TableView tv = guiTest.find("#searchTable");
        guiTest.sleep(1000);
        assertTrue(tv.getItems().size() != 0);
        Book book = (Book) tv.getItems().get(0);
        clickOnFirstItemInTable();
        String name = ((TextField) guiTest.find("#addTitle")).getText();
        String genre = ((TextField) guiTest.find("#addGenre")).getText();
        assertTrue(genre.contains(book.getGenre()));
        assertTrue(name.contains(book.getName()));
    }

    @Test
    public void searchEmptyString_warningWindowShowing() {
        guiTest.waitUntil("#searchTextField", visible());
        TextField textField = guiTest.find("#searchTextField");
        clickOnSearchComboBox(1);
        textField.setText("");
        guiTest.click("#search");
        guiTest.sleep(1000);
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        guiTest.click("OK");
        AnchorPane anchorPane = guiTest.find("#anchorSearch");
        double x = anchorPane.getScene().getWindow().getX() + anchorPane.getScene().getWindow().getWidth();
        double y = anchorPane.getScene().getWindow().getY();
        guiTest.move(x - 15, y + 10);
        guiTest.click(MouseButton.PRIMARY);
    }

    @Test
    public void searchErrorIsbn_warningWindowShowing() {
        guiTest.waitUntil("#searchTextField", visible());
        TextField textField = guiTest.find("#searchTextField");
        clickOnSearchComboBox(1);
        textField.setText("dsfghg");
        guiTest.click("#search");
        guiTest.sleep(1000);
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        guiTest.click("OK");
        AnchorPane anchorPane = guiTest.find("#anchorSearch");
        double x = anchorPane.getScene().getWindow().getX() + anchorPane.getScene().getWindow().getWidth();
        double y = anchorPane.getScene().getWindow().getY();
        guiTest.move(x - 15, y + 10);
        guiTest.click(MouseButton.PRIMARY);
    }

    public void clickOnFirstItemInTable() {
        Node node = new FxRobot().lookup("#searchTable").lookup(".table-row-cell").nth(0).query();
        Bounds boundsInScreen = node.localToScreen(node.getBoundsInLocal());
        guiTest.move(boundsInScreen.getMaxX() - 25, boundsInScreen.getMinY() + 7);
        guiTest.click(MouseButton.PRIMARY);
        guiTest.sleep(1000);
    }

    public void clickOnSearchComboBox(int typeOrder) {
        guiTest.click("#searchTypeComboBox");
        Point p = MouseInfo.getPointerInfo().getLocation();
        guiTest.move(p.x, p.y + 25 * typeOrder);
        guiTest.click(MouseButton.PRIMARY);
        guiTest.sleep(1000);
    }


}