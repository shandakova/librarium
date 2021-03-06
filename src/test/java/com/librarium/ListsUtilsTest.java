package com.librarium;

import com.librarium.gui.MainWindowController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
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
public class ListsUtilsTest {
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
    public void listAdd_AddBook_clickSave() {
        guiTest.sleep(10000);
        guiTest.click("#AddBook");
        ((TextField) guiTest.find("#addTitle")).setText("Normal Title");
        ((TextField) guiTest.find("#addISBN")).setText("123546");
        ((TextField) guiTest.find("#addYear")).setText("1234");
        ((TextField) guiTest.find("#addGenre")).setText("другое");
        ((TextField) guiTest.find("#addAuthor")).setText("author");
        guiTest.click("#rate");
        guiTest.click("#addBook");
        FxAssert.verifyThat(new FxRobot().window("Поздравляем!"), WindowMatchers.isShowing());
        guiTest.click("OK");

        guiTest.click("#Lists");

        guiTest.click("#addButton");
        ((TextField) guiTest.find("#textField")).setText("");
        guiTest.click("#okBtn");
        FxAssert.verifyThat(new FxRobot().window("Ошибка"), WindowMatchers.isShowing());
        guiTest.click("OK");
        ((TextField) guiTest.find("#textField")).setText("Normal Title");
        guiTest.click("#okBtn");

        guiTest.click("#myLibrary");
        guiTest.click("#searchTable");
        Point p = MouseInfo.getPointerInfo().getLocation();
        guiTest.move(p.x - 10, p.y - 285);
        guiTest.click(MouseButton.PRIMARY);
        guiTest.click("#addList");
        guiTest.waitUntil("#list", visible());
        guiTest.click("#add");
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        guiTest.click("OK");
        guiTest.click("#list");
        p = MouseInfo.getPointerInfo().getLocation();
        guiTest.move(p.x, p.y + 30);
        guiTest.sleep(1000);
        guiTest.click(MouseButton.PRIMARY);
        guiTest.click("#add");
    }
}
