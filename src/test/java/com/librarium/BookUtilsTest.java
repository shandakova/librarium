package com.librarium;

import com.librarium.gui.MainWindowController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import lombok.SneakyThrows;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.loadui.testfx.GuiTest;
import org.loadui.testfx.utils.FXTestUtils;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.matcher.base.WindowMatchers;

import java.awt.*;

import static org.loadui.testfx.controls.impl.VisibleNodesMatcher.visible;
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BookUtilsTest {
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
        guiTest.sleep(10000);
    }

    @Test
    public void addBook_wrongAuthor_warningWindowShowing() {
        guiTest.click("#AddBook");
        guiTest.sleep(3000);
        ((TextField) guiTest.find("#addTitle")).setText("Normal Title");
        ((TextField) guiTest.find("#addAuthor")).setText("12345121");
        guiTest.click("#addBook");
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        guiTest.click("OK");
        guiTest.doubleClick("#addAuthor");
        guiTest.push(KeyCode.BACK_SPACE);
        guiTest.doubleClick("#addTitle");
        guiTest.click("#addTitle");
        guiTest.push(KeyCode.BACK_SPACE);
    }

    @Test
    public void addBook_wrongTitle_warningWindowShowing() {
        guiTest.click("#AddBook");
        guiTest.sleep(3000);
        ((TextField) guiTest.find("#addTitle")).setText("étrangère");
        guiTest.click("#addBook");
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        guiTest.click("OK");
        guiTest.doubleClick("#addTitle");
        guiTest.push(KeyCode.BACK_SPACE);
    }

    @Test
    public void addBook_emptyTitle_warningWindowShowing() {
        guiTest.click("#AddBook");
        guiTest.sleep(3000);
        ((TextField) guiTest.find("#addTitle")).setText("");
        guiTest.click("#addBook");
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        guiTest.click("OK");
    }

    @Test
    public void addBook_wrongGenre_warningWindowShowing() {
        guiTest.click("#AddBook");
        guiTest.sleep(3000);
        ((TextField) guiTest.find("#addTitle")).setText("Normal Title");
        ((TextField) guiTest.find("#addGenre")).setText("12345121");
        guiTest.click("#addBook");
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        guiTest.click("OK");
        guiTest.doubleClick("#addTitle");
        guiTest.click("#addTitle");
        guiTest.push(KeyCode.BACK_SPACE);
        guiTest.doubleClick("#addGenre");
        guiTest.push(KeyCode.BACK_SPACE);
    }

    @Test
    public void addBook_wrongYear_warningWindowShowing() {
        guiTest.click("#AddBook");
        guiTest.sleep(3000);
        ((TextField) guiTest.find("#addTitle")).setText("Normal Title");
        ((TextField) guiTest.find("#addYear")).setText("awdwda");
        guiTest.click("#addBook");
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        guiTest.click("OK");
        guiTest.doubleClick("#addTitle");
        guiTest.click("#addTitle");
        guiTest.push(KeyCode.BACK_SPACE);
        guiTest.doubleClick("#addYear");
        guiTest.push(KeyCode.BACK_SPACE);
    }

    @Test
    public void addBook_wrongISBN_warningWindowShowing() {
        guiTest.click("#AddBook");
        guiTest.sleep(3000);
        ((TextField) guiTest.find("#addTitle")).setText("Normal Title");
        ((TextField) guiTest.find("#addISBN")).setText("awdwda");
        guiTest.click("#addBook");
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        guiTest.click("OK");
        guiTest.doubleClick("#addTitle");
        guiTest.click("#addTitle");
        guiTest.push(KeyCode.BACK_SPACE);
        guiTest.doubleClick("#addISBN");
        guiTest.push(KeyCode.BACK_SPACE);
    }

    @Test
    public void addBook_allFieldsFilled_infoWindowShowing() {
        guiTest.click("#AddBook");
        guiTest.sleep(10000);
        ((TextField) guiTest.find("#addTitle")).setText("Normal Title");
        ((TextField) guiTest.find("#addISBN")).setText("123546");
        ((TextField) guiTest.find("#addYear")).setText("1234");
        ((TextField) guiTest.find("#addGenre")).setText("другое");
        ((TextField) guiTest.find("#addAuthor")).setText("author");
        guiTest.click("#rate");
        guiTest.click("#addBook");
        FxAssert.verifyThat(new FxRobot().window("Поздравляем!"), WindowMatchers.isShowing());
        guiTest.click("OK");
    }

    @Test
    public void editBook_changeAuthor() {
        guiTest.click("#AddBook");
        guiTest.sleep(10000);
        ((TextField) guiTest.find("#addTitle")).setText("Normal Title");
        ((TextField) guiTest.find("#addISBN")).setText("123546");
        ((TextField) guiTest.find("#addYear")).setText("1234");
        ((TextField) guiTest.find("#addGenre")).setText("другое");
        ((TextField) guiTest.find("#addAuthor")).setText("author");
        guiTest.click("#rate");
        guiTest.click("#addBook");
        FxAssert.verifyThat(new FxRobot().window("Поздравляем!"), WindowMatchers.isShowing());
        guiTest.click("OK");
        guiTest.click("#myLibrary");
        guiTest.click("#searchTable");
        Point p = MouseInfo.getPointerInfo().getLocation();
        guiTest.move(p.x - 10, p.y - 285);
        guiTest.click(MouseButton.PRIMARY);
        guiTest.waitUntil("#editBook", visible());
        guiTest.click("#editBook");
        FxAssert.verifyThat(new FxRobot().window("Окно редактирования"), WindowMatchers.isShowing());
        guiTest.waitUntil("#author", visible());
        ((TextField) guiTest.find("#author")).setText("adsfdghj");
        guiTest.click("#okBtn");
    }

    @Test
    public void editBook_wrongAuthor_alertShowing() {
        guiTest.click("#myLibrary");
        guiTest.click("#searchTable");
        Point p = MouseInfo.getPointerInfo().getLocation();
        guiTest.move(p.x - 10, p.y - 285);
        guiTest.click(MouseButton.PRIMARY);
        guiTest.waitUntil("#editBook", visible());
        guiTest.click("#editBook");
        FxAssert.verifyThat(new FxRobot().window("Окно редактирования"), WindowMatchers.isShowing());
        guiTest.waitUntil("#author", visible());
        ((TextField) guiTest.find("#author")).setText("124");
        guiTest.click("#okBtn");
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        guiTest.click("OK");
        guiTest.closeCurrentWindow();
        guiTest.closeCurrentWindow();
    }

    @Test
    public void editBook_wrongTitle_alertShowing() {
        guiTest.click("#AddBook");
        guiTest.sleep(10000);
        ((TextField) guiTest.find("#addTitle")).setText("Normal Title");
        ((TextField) guiTest.find("#addISBN")).setText("123546");
        ((TextField) guiTest.find("#addYear")).setText("1234");
        ((TextField) guiTest.find("#addGenre")).setText("другое");
        ((TextField) guiTest.find("#addAuthor")).setText("author");
        guiTest.click("#rate");
        guiTest.click("#addBook");
        FxAssert.verifyThat(new FxRobot().window("Поздравляем!"), WindowMatchers.isShowing());
        guiTest.click("OK");
        guiTest.click("#myLibrary");
        guiTest.click("#searchTable");
        Point p = MouseInfo.getPointerInfo().getLocation();
        guiTest.move(p.x - 10, p.y - 285);
        guiTest.click(MouseButton.PRIMARY);
        guiTest.waitUntil("#editBook", visible());
        guiTest.click("#editBook");
        FxAssert.verifyThat(new FxRobot().window("Окно редактирования"), WindowMatchers.isShowing());
        guiTest.waitUntil("#title", visible());
        ((TextField) guiTest.find("#title")).setText("é");
        guiTest.click("#okBtn");
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        guiTest.click("OK");
        guiTest.closeCurrentWindow();
        guiTest.closeCurrentWindow();
    }

    @Test
    public void editBook_wrongGenre_alertShowing() {
        guiTest.click("#myLibrary");
        guiTest.click("#searchTable");
        Point p = MouseInfo.getPointerInfo().getLocation();
        guiTest.move(p.x - 10, p.y - 285);
        guiTest.click(MouseButton.PRIMARY);
        guiTest.waitUntil("#editBook", visible());
        guiTest.click("#editBook");
        FxAssert.verifyThat(new FxRobot().window("Окно редактирования"), WindowMatchers.isShowing());
        guiTest.waitUntil("#genre", visible());
        ((TextField) guiTest.find("#genre")).setText("2");
        guiTest.click("#okBtn");
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        guiTest.click("OK");
        guiTest.closeCurrentWindow();
        guiTest.closeCurrentWindow();
    }

    @Test
    public void editBook_wrongYear_alertShowing() {
        guiTest.click("#myLibrary");
        guiTest.click("#searchTable");
        Point p = MouseInfo.getPointerInfo().getLocation();
        guiTest.move(p.x - 10, p.y - 285);
        guiTest.click(MouseButton.PRIMARY);
        guiTest.waitUntil("#editBook", visible());
        guiTest.click("#editBook");
        FxAssert.verifyThat(new FxRobot().window("Окно редактирования"), WindowMatchers.isShowing());
        guiTest.waitUntil("#year", visible());
        ((TextField) guiTest.find("#year")).setText("26");
        guiTest.click("#okBtn");
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        guiTest.click("OK");
        guiTest.closeCurrentWindow();
        guiTest.closeCurrentWindow();
    }

    @Test
    public void editBook_wrongISBN_alertShowing() {
        guiTest.click("#myLibrary");
        guiTest.click("#searchTable");
        Point p = MouseInfo.getPointerInfo().getLocation();
        guiTest.move(p.x - 10, p.y - 285);
        guiTest.click(MouseButton.PRIMARY);
        guiTest.waitUntil("#editBook", visible());
        guiTest.click("#editBook");
        FxAssert.verifyThat(new FxRobot().window("Окно редактирования"), WindowMatchers.isShowing());
        guiTest.waitUntil("#isbn", visible());
        ((TextField) guiTest.find("#isbn")).setText("24e");
        guiTest.click("#okBtn");
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        guiTest.click("OK");
        guiTest.closeCurrentWindow();
        guiTest.closeCurrentWindow();
    }

    @Test
    public void editBook_blankTitle_alertShowing() {
        guiTest.click("#myLibrary");
        guiTest.click("#searchTable");
        Point p = MouseInfo.getPointerInfo().getLocation();
        guiTest.move(p.x - 10, p.y - 285);
        guiTest.click(MouseButton.PRIMARY);
        guiTest.waitUntil("#editBook", visible());
        guiTest.click("#editBook");
        FxAssert.verifyThat(new FxRobot().window("Окно редактирования"), WindowMatchers.isShowing());
        guiTest.waitUntil("#title", visible());
        ((TextField) guiTest.find("#title")).setText("");
        guiTest.click("#okBtn");
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        guiTest.click("OK");
        guiTest.closeCurrentWindow();
        guiTest.closeCurrentWindow();
    }

    @Test
    public void removeBook_clickNo_alertShowing() {
        guiTest.click("#AddBook");
        guiTest.sleep(10000);
        ((TextField) guiTest.find("#addTitle")).setText("Normal Title");
        ((TextField) guiTest.find("#addISBN")).setText("123546");
        ((TextField) guiTest.find("#addYear")).setText("1234");
        ((TextField) guiTest.find("#addGenre")).setText("другое");
        ((TextField) guiTest.find("#addAuthor")).setText("author");
        guiTest.click("#rate");
        guiTest.click("#addBook");
        FxAssert.verifyThat(new FxRobot().window("Поздравляем!"), WindowMatchers.isShowing());
        guiTest.click("OK");
        guiTest.click("#myLibrary");
        guiTest.click("#searchTable");
        Point p = MouseInfo.getPointerInfo().getLocation();
        guiTest.move(p.x - 10, p.y - 285);
        guiTest.click(MouseButton.PRIMARY);
        guiTest.waitUntil("#editBook", visible());
        guiTest.click("#editBook");
        FxAssert.verifyThat(new FxRobot().window("Окно редактирования"), WindowMatchers.isShowing());
        guiTest.sleep(3000);
        guiTest.click("#delBtn");
        FxAssert.verifyThat(new FxRobot().window("Удаление"), WindowMatchers.isShowing());
        guiTest.click("Нет");
        guiTest.closeCurrentWindow();
        guiTest.closeCurrentWindow();
    }

    @Test
    public void removeBook_clickYes_alertShowing() {
        guiTest.click("#AddBook");
        guiTest.sleep(10000);
        ((TextField) guiTest.find("#addTitle")).setText("Normal Title");
        ((TextField) guiTest.find("#addISBN")).setText("123546");
        ((TextField) guiTest.find("#addYear")).setText("1234");
        ((TextField) guiTest.find("#addGenre")).setText("другое");
        ((TextField) guiTest.find("#addAuthor")).setText("author");
        guiTest.click("#rate");
        guiTest.click("#addBook");
        FxAssert.verifyThat(new FxRobot().window("Поздравляем!"), WindowMatchers.isShowing());
        guiTest.click("OK");
        guiTest.click("#myLibrary");
        guiTest.click("#searchTable");
        Point p = MouseInfo.getPointerInfo().getLocation();
        guiTest.move(p.x - 10, p.y - 285);
        guiTest.click(MouseButton.PRIMARY);
        guiTest.waitUntil("#editBook", visible());
        guiTest.click("#editBook");
        FxAssert.verifyThat(new FxRobot().window("Окно редактирования"), WindowMatchers.isShowing());
        //guiTest.sleep(3000);
        guiTest.click("#delBtn");
        FxAssert.verifyThat(new FxRobot().window("Удаление"), WindowMatchers.isShowing());
        guiTest.click("Да");
    }
}
