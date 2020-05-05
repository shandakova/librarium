package com.librarium.gui;

import com.librarium.entity.Book;
import com.librarium.entity.Comment;
import com.librarium.entity.Lists;
import com.librarium.repository.BookRepository;
import com.librarium.repository.ListsRepository;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.junit.Assert;
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
import java.net.URL;
import java.util.*;

import static junit.framework.TestCase.assertTrue;
import static org.loadui.testfx.controls.impl.VisibleNodesMatcher.visible;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

public class ListsControllerTest extends GuiTest {
    final protected FXMLLoader loader = new FXMLLoader(AddBookController.class.getResource("/fxml/lists.fxml"));
    protected Parent parent;
    private ListsController listsController;
    public Book book = new Book();
    @Mock
    private BookRepository bookRepository = mock(BookRepository.class);
    @Mock
    private ListsRepository listsRepository = mock(ListsRepository.class);

    @Before
    public void initMocks() {
        Mockito.when(bookRepository.saveAndFlush(any(Book.class))).thenAnswer(i -> i.getArguments()[0]);
        Mockito.when(listsRepository.saveAndFlush(any(Lists.class))).thenAnswer(i -> i.getArguments()[0]);
        book.setRate(5);
        book.setISBN("1324");
        book.setYear(1999);
        book.setGenre("romance");
        book.setName("test");
        book.setAuthor("Me");
        Lists list1 = new Lists("1");
        list1.setBooks(Collections.singleton(book));
        Set<Lists> lists = new HashSet<>();
        lists.add(list1);
        Lists list2 = new Lists("2");
        list2.setBooks(Collections.singleton(book));
        lists.add(list2);
        book.setLists(lists);
        Mockito.doReturn(Collections.singletonList(list1)).when(listsRepository).findAll();
        Mockito.when(listsRepository.findByBooks(book)).thenAnswer(i -> Arrays.asList(list1, list2));
        Mockito.when(bookRepository.findByLists(list1)).thenAnswer(i -> Arrays.asList(book));
        Mockito.when(listsRepository.findByName("1")).thenAnswer(i -> Arrays.asList(list1));
        Mockito.when(listsRepository.findByName("2")).thenAnswer(i -> Arrays.asList(list2));
        Mockito.when(listsRepository.findByNameContainsIgnoreCase("1")).thenAnswer(i -> Arrays.asList(list1));
        listsController.init(bookRepository, listsRepository);
    }

    @SneakyThrows
    @Override
    protected Parent getRootNode() {
        if (parent == null) parent = loader.load();
        if (listsController == null) listsController = loader.getController();
        initMocks();
        return parent;
    }

    @Test
    public void list_click_openWindow() {
        waitUntil("#searchTable", visible());
        click("#searchTypeComboBox");
        click("#searchButton");
        click("#searchTable");
        Point p = MouseInfo.getPointerInfo().getLocation();
        move(p.x - 30, p.y - 230);
        click(MouseButton.PRIMARY);
        FxAssert.verifyThat(new FxRobot().window("Информация о листе"), WindowMatchers.isShowing());
        closeCurrentWindow();
    }

    @Test
    public void searchName_null_warningWindow() {
        waitUntil("#searchTypeComboBox", visible());
        click("#searchTypeComboBox");
        Point p = MouseInfo.getPointerInfo().getLocation();
        move(p.x, p.y + 55);
        click(MouseButton.PRIMARY);
        TextArea textArea = find("#searchTextField");
        String text = "";
        textArea.setText(text);
        click("#searchButton");
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        push(KeyCode.ENTER);
        closeCurrentWindow();
    }

    @Test
    public void searchName_valid_showResult() {
        waitUntil("#searchTypeComboBox", visible());
        click("#searchTypeComboBox");
        Point p = MouseInfo.getPointerInfo().getLocation();
        move(p.x, p.y + 55);
        click(MouseButton.PRIMARY);
        TextArea textArea = find("#searchTextField");
        String text = "1";
        textArea.setText(text);
        click("#searchButton");
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        Mockito.verify(listsRepository).findByNameContainsIgnoreCase(argument.capture());
        Assert.assertTrue(argument.getValue().equals(text));
        closeCurrentWindow();
    }

    @Test
    public void addButton_click_openWindow() {
        waitUntil("#addButton", visible());
        click("#addButton");
        FxAssert.verifyThat(new FxRobot().window("Добавить лист"), WindowMatchers.isShowing());
        closeCurrentWindow();
    }
}
