package com.librarium.gui;

import com.librarium.entity.Book;
import com.librarium.entity.Lists;
import com.librarium.repository.BookRepository;
import com.librarium.repository.ListsRepository;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
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

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.loadui.testfx.controls.impl.VisibleNodesMatcher.visible;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

public class ViewListControllerTest extends GuiTest {
    private BookRepository bookRepository = mock(BookRepository.class);
    private ListsRepository listsRepository = mock(ListsRepository.class);
    private ViewListController viewListController = mock(ViewListController.class);
    public Book book = new Book();

    @Before
    public void initMock() {
        Mockito.when(bookRepository.saveAndFlush(any(Book.class))).thenAnswer(i -> i.getArguments()[0]);
        Mockito.when(listsRepository.saveAndFlush(any(Lists.class))).thenAnswer(i -> i.getArguments()[0]);
        MockitoAnnotations.initMocks(this);
    }

    @SneakyThrows
    @Override
    protected Parent getRootNode() {
        Parent parent = null;
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(AddCommentController.class.getResource("/fxml/viewlist.fxml"));
        parent = loader.load();
        if (parent.getScene() == null) {
            stage.setScene(new Scene(parent));
        } else {
            stage.setScene(parent.getScene());
        }
        viewListController = loader.getController();
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
        Mockito.when(listsRepository.findAll()).thenAnswer(i -> Arrays.asList(list1, list2));
        Mockito.when(listsRepository.findByBooks(book)).thenAnswer(i -> Arrays.asList(list1, list2));
        Mockito.when(bookRepository.findByLists(list1)).thenAnswer(i -> Arrays.asList(book));
        Mockito.when(bookRepository.findByLists(list2)).thenAnswer(i -> Arrays.asList(book));
        viewListController.initData(bookRepository, listsRepository, list1);
        stage.show();
        return parent;
    }

    @Test
    public void deleteList_click_listDeleted() {
        waitUntil("#deleteBtn", visible());
        click("#deleteBtn");
        ArgumentCaptor<Lists> argument = ArgumentCaptor.forClass(Lists.class);
        Mockito.verify(listsRepository).delete(argument.capture());
        assertTrue(argument.getValue().getName().equals("1"));
        closeCurrentWindow();
    }

    @Test
    public void addBook_click_openWindow() {
        waitUntil("#addBtn", visible());
        click("#addBtn");
        FxAssert.verifyThat(new FxRobot().window("Добавление книги в лист"), WindowMatchers.isShowing());
        closeCurrentWindow();
    }

    @Test
    public void book_clickMinus_bookRemoved() {
        waitUntil("#bookTable", visible());
        click("#bookTable");
        Point p = MouseInfo.getPointerInfo().getLocation();
        move(p.x + 270, p.y - 200);
        click(MouseButton.PRIMARY);
        Lists list1 = new Lists("1");
        ArgumentCaptor<Book> argument = ArgumentCaptor.forClass(Book.class);
        Mockito.verify(bookRepository).saveAndFlush(argument.capture());
        assertFalse(argument.getValue().getLists().contains(list1));
        closeCurrentWindow();
    }
}
