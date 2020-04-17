package com.librarium.gui;

import com.librarium.entity.Lists;
import com.librarium.repository.ListsRepository;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.matcher.base.WindowMatchers;

import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;
import static org.loadui.testfx.controls.impl.VisibleNodesMatcher.visible;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

public class NewListControllerTest extends GuiTest {
    private ListsRepository listsRepository = mock(ListsRepository.class);
    private NewListController newListController = mock(NewListController.class);

    @Before
    public void initMock() {
        Mockito.when(listsRepository.saveAndFlush(any(Lists.class))).thenAnswer(i -> i.getArguments()[0]);
        Mockito.doNothing().when(listsRepository).flush();
        Lists list = new Lists();
        list.setName("test");
        Mockito.when(listsRepository.findByName("test")).thenAnswer(i -> Arrays.asList(list));
        MockitoAnnotations.initMocks(this);
    }

    @SneakyThrows
    @Override
    protected Parent getRootNode() {
        Parent parent = null;
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(AddListController.class.getResource("/fxml/newlist.fxml"));
        parent = loader.load();
        if (parent.getScene() == null) {
            stage.setScene(new Scene(parent));
        } else {
            stage.setScene(parent.getScene());
        }
        newListController = loader.getController();
        newListController.initData(listsRepository);
        stage.show();
        return parent;
    }

    @Test
    public void clickedOk_nullName_showWarning() {
        waitUntil("#textField", visible());
        TextField textField = find("#textField");
        textField.setText("");
        click("#okBtn");
        FxAssert.verifyThat(new FxRobot().window("Ошибка"), WindowMatchers.isShowing());
        push(KeyCode.ENTER);
        closeCurrentWindow();
    }

    @Test
    public void clickedOk_sameName_showWarning() {
        waitUntil("#textField", visible());
        TextField textField = find("#textField");
        textField.setText("test");
        click("#okBtn");
        FxAssert.verifyThat(new FxRobot().window("Ошибка"), WindowMatchers.isShowing());
        push(KeyCode.ENTER);
        closeCurrentWindow();
    }

    @Test
    public void clickedOk_correctName_savedList() {
        waitUntil("#textField", visible());
        TextField textField = find("#textField");
        String text = "adsfdghj";
        textField.setText(text);
        click("#okBtn");
        ArgumentCaptor<Lists> argument = ArgumentCaptor.forClass(Lists.class);
        Mockito.verify(listsRepository).saveAndFlush(argument.capture());
        assertTrue(argument.getValue().getName().equals(text));
        closeCurrentWindow();
    }
}
