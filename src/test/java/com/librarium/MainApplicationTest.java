package com.librarium;

import com.librarium.gui.MainWindowController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.SneakyThrows;
import org.junit.BeforeClass;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.loadui.testfx.utils.FXTestUtils;


public class MainApplicationTest {
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
        guiTest.sleep(25000);
    }

    @Test
    public void test() {
        guiTest.click("#searchBtn");
        guiTest.sleep(1000);
    }

}