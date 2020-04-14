package com.librarium.gui;

import com.librarium.entity.Book;
import com.librarium.entity.Lists;
import com.librarium.repository.BookRepository;
import com.librarium.repository.ListsRepository;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.loadui.testfx.GuiTest;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.matcher.base.WindowMatchers;

import java.awt.*;
import java.util.*;

import static org.loadui.testfx.controls.impl.VisibleNodesMatcher.visible;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AddListControllerTest extends GuiTest {

    private BookRepository bookRepository = mock(BookRepository.class);
    private ListsRepository listsRepository = mock(ListsRepository.class);
    private AddListController addListController;
    final protected FXMLLoader loader = new FXMLLoader(AddListController.class.getResource("/fxml/addlist.fxml"));
    protected Parent parent;

    @SneakyThrows
    @Before
    public void initMock() {
        Book book = new Book("some book");
        book.setId(1L);
        Lists list1 = new Lists("1");
        list1.setId(1L);
        Lists list2 = new Lists("2");
        list2.setId(2L);
        list1.setBooks(Collections.singleton(book));
        Set<Lists> lists = new HashSet<>();
        lists.add(list1);
        book.setLists(lists);
        Mockito.when(bookRepository.saveAndFlush(any(Book.class))).thenAnswer(i -> i.getArguments()[0]);
        Mockito.when(bookRepository.findById(1L)).thenAnswer(i -> Optional.of(book));
        Mockito.when(listsRepository.findAll()).thenAnswer(i -> Arrays.asList(list1, list2));
        Mockito.when(listsRepository.findByName("1")).thenAnswer(i -> Arrays.asList(list1));
        Mockito.when(listsRepository.findByName("2")).thenAnswer(i -> Arrays.asList(list2));
        Mockito.doNothing().when(bookRepository).flush();
        MockitoAnnotations.initMocks(this);
        addListController.initData(bookRepository, book, listsRepository);
        setupStage();
    }

    @After
    public void sleep() {
        sleep(1000);
    }

    @Test
    public void clickedAdd_ListNotChoose_warningWindow() {
        waitUntil("#list", visible());
        click("#add");
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        push(KeyCode.ENTER);
        sleep(1000);
        closeCurrentWindow();
    }

    @Test
    public void clickedAdd_bookAlreadyInList_warningWindow() {
        waitUntil("#list", visible());
        click("#list");
        Point p = MouseInfo.getPointerInfo().getLocation();
        move(p.x, p.y + 25);
        sleep(1000);
        click(MouseButton.PRIMARY);
        click("#add");
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        push(KeyCode.ENTER);
        sleep(1000);
        closeCurrentWindow();
    }


    @Test
    public void clickedAdd_bookNotInList_windowClose() {
        waitUntil("#list", visible());
        click("#list");
        Point p = MouseInfo.getPointerInfo().getLocation();
        move(p.x, p.y + 50);
        sleep(1000);
        click(MouseButton.PRIMARY);
        click("#add");
        sleep(1000);
        Mockito.verify(bookRepository, times(1)).saveAndFlush(any(Book.class));
    }

    @Test
    public void clickedCancel() {
        click("#cancel");
    }

    @SneakyThrows
    @Override
    protected Parent getRootNode() {
        if (parent == null) parent = loader.load();
        stage = new Stage();
        stage.setScene(parent.getScene());
        stage.show();
        if (addListController == null) addListController = loader.getController();
        return parent;
    }
}