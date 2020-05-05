package com.librarium.gui;

import com.librarium.entity.Book;
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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.matcher.base.WindowMatchers;

import java.awt.*;
import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static org.loadui.testfx.controls.impl.VisibleNodesMatcher.visible;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

public class SearchAddBookInListControllerTest extends GuiTest {
    private BookRepository bookRepository = mock(BookRepository.class);
    private ListsRepository listsRepository = mock(ListsRepository.class);
    private SearchAddBookInListController searchAddBookInListController = mock(SearchAddBookInListController.class);
    public Book book = new Book();
    public Book book2 = new Book();

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
        FXMLLoader loader = new FXMLLoader(AddCommentController.class.getResource("/fxml/searchaddbookinlist.fxml"));
        parent = loader.load();
        if (parent.getScene() == null) {
            stage.setScene(new Scene(parent));
        } else {
            stage.setScene(parent.getScene());
        }
        searchAddBookInListController = loader.getController();
        book.setRate(5);
        book.setISBN("1324");
        book.setYear(1999);
        book.setGenre("romance");
        book.setName("test");
        book.setAuthor("Me");
        book2.setRate(5);
        book2.setISBN("1324");
        book2.setYear(1999);
        book2.setGenre("romancy");
        book2.setName("test");
        book2.setAuthor("Me");
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
        Mockito.when(bookRepository.findAll()).thenAnswer(i -> Arrays.asList(book, book2));
        Mockito.when(bookRepository.findByNameContainsIgnoreCase(book.getName())).thenAnswer(i -> Arrays.asList(book));
        Mockito.when(bookRepository.findByAuthorContainsIgnoreCase(book.getAuthor())).thenAnswer(i -> Arrays.asList(book));
        Mockito.when(bookRepository.findByGenreContainsIgnoreCase(book.getGenre())).thenAnswer(i -> Arrays.asList(book));
        Mockito.when(bookRepository.findByGenreContainsIgnoreCase(book2.getGenre())).thenAnswer(i -> Arrays.asList(book2));
        Mockito.when(listsRepository.findByName("1")).thenAnswer(i -> Arrays.asList(list1));
        Mockito.when(listsRepository.findByNameContainsIgnoreCase("1")).thenAnswer(i -> Arrays.asList(list1));
        searchAddBookInListController.initData(list1, listsRepository, bookRepository);
        stage.show();
        return parent;
    }

    @Test
    public void searchAll_click_showAll() {
        waitUntil("#searchfilter", visible());
        click("#searchfilter");
        Point p = MouseInfo.getPointerInfo().getLocation();
        move(p.x, p.y + 35);
        click(MouseButton.PRIMARY);
        click("#search");
        closeCurrentWindow();
    }

    @Test
    public void searchName_null_showWarning() {
        waitUntil("#searchfilter", visible());
        click("#searchfilter");
        Point p = MouseInfo.getPointerInfo().getLocation();
        move(p.x, p.y + 55);
        click(MouseButton.PRIMARY);
        click("#search");
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        push(KeyCode.ENTER);
        closeCurrentWindow();
    }

    @Test
    public void searchName_valid_showResult() {
        waitUntil("#searchfilter", visible());
        click("#searchfilter");
        Point p = MouseInfo.getPointerInfo().getLocation();
        move(p.x, p.y + 55);
        click(MouseButton.PRIMARY);
        TextArea textArea = find("#searchfield");
        String text = "test";
        textArea.setText(text);
        click("#search");
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        Mockito.verify(bookRepository).findByNameContainsIgnoreCase(argument.capture());
        assertEquals(argument.getValue(), text);
        closeCurrentWindow();
    }

    @Test
    public void searchAuthor_wrong_showWarning() {
        waitUntil("#searchfilter", visible());
        click("#searchfilter");
        Point p = MouseInfo.getPointerInfo().getLocation();
        move(p.x, p.y + 85);
        click(MouseButton.PRIMARY);
        TextArea textArea = find("#searchfield");
        String text = "1";
        textArea.setText(text);
        click("#search");
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        push(KeyCode.ENTER);
        closeCurrentWindow();
    }

    @Test
    public void searchAuthor_valid_showResult() {
        waitUntil("#searchfilter", visible());
        click("#searchfilter");
        Point p = MouseInfo.getPointerInfo().getLocation();
        move(p.x, p.y + 85);
        click(MouseButton.PRIMARY);
        TextArea textArea = find("#searchfield");
        String text = "Me";
        textArea.setText(text);
        click("#search");
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        Mockito.verify(bookRepository).findByAuthorContainsIgnoreCase(argument.capture());
        assertEquals(argument.getValue(), text);
        closeCurrentWindow();
    }

    @Test
    public void searchGenre_valid_showResult() {
        waitUntil("#searchfilter", visible());
        click("#searchfilter");
        Point p = MouseInfo.getPointerInfo().getLocation();
        move(p.x, p.y + 105);
        click(MouseButton.PRIMARY);
        TextArea textArea = find("#searchfield");
        String text = "romance";
        textArea.setText(text);
        click("#search");
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        Mockito.verify(bookRepository).findByGenreContainsIgnoreCase(argument.capture());
        assertEquals(argument.getValue(), text);
        closeCurrentWindow();
    }

    @Test
    public void plus_clicked_updateBook() {
        waitUntil("#searchresult", visible());
        click("#searchresult");
        Point p = MouseInfo.getPointerInfo().getLocation();
        move(p.x + 350, p.y - 70);
        click(MouseButton.PRIMARY);
        closeCurrentWindow();
    }

    @Test
    public void ok_clicked_closeWindow() {
        waitUntil("#ok", visible());
        click("#ok");
        closeCurrentWindow();
    }
}
