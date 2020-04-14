package com.librarium.gui;

import com.librarium.entity.Book;
import com.librarium.repository.BookApiRepository;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClientResponseException;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.matcher.base.WindowMatchers;

import java.awt.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.loadui.testfx.controls.impl.VisibleNodesMatcher.visible;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

public class InternetSearchControllerTest extends GuiTest {
    final protected FXMLLoader loader = new FXMLLoader(AddListController.class.getResource("/fxml/internetsearch.fxml"));
    protected Parent parent;
    private AddBookController addBookController = mock(AddBookController.class);
    private BookApiRepository bookApiRepository = mock(BookApiRepository.class);
    private InternetSearchController internetSearchController;

    @Before
    public void initMocks() {
        Mockito.doNothing().when(addBookController).fillField(any(Book.class));
        Mockito.when(bookApiRepository.getBookListByAuthor(anyString(), eq(30))).thenAnswer(i -> {
            String author = (String) i.getArguments()[0];
            Book book = new Book("");
            book.setAuthor(author);
            return Arrays.asList(book);
        });
        Mockito.when(bookApiRepository.getBookListByGenre(anyString(), eq(30))).thenAnswer(i -> {
            String genre = (String) i.getArguments()[0];
            Book book = new Book("");
            book.setGenre(genre);
            return Arrays.asList(book);
        });
        Mockito.when(bookApiRepository.getBookListByIsbn(contains("1"), eq(30))).thenAnswer(i -> {
            String isbn = (String) i.getArguments()[0];
            Book book = new Book("");
            book.setISBN(isbn);
            return Arrays.asList(book);
        });
        Mockito.when(bookApiRepository.getBookListByIsbn(contains("0"), eq(30))).thenAnswer(i -> {
            throw new RestClientResponseException("adwawd", 405, "405", HttpHeaders.EMPTY,
                    new byte[0], Charset.defaultCharset());
        });
        Mockito.when(bookApiRepository.getBookListByIsbn(contains("5"), eq(30))).thenAnswer(i -> Collections.EMPTY_LIST);
        Mockito.when(bookApiRepository.getBookListByTitle(anyString(), eq(30))).thenAnswer(i -> {
            String title = (String) i.getArguments()[0];
            Book book = new Book(title);
            return Arrays.asList(book);
        });
        MockitoAnnotations.initMocks(this);
        internetSearchController.initData(addBookController, bookApiRepository);
    }

    @Test
    public void clickedSearchButton_searchNormalISBN_TablesShows() {
        waitUntil("#searchTextField", visible());
        TextField textField = find("#searchTextField");
        clickOnSearchComboBox(1);
        String text = "12142-12321-1231";
        textField.setText(text);
        click("#search");
        TableView tv = find("#searchTable");
        sleep(1000);
        assertTrue(tv.getItems().size() != 0);
        Mockito.verify(bookApiRepository, times(1)).getBookListByIsbn(anyString(), eq(30));
    }

    @Test
    public void clickedSearchButton_searchNormalAuthor_TablesShows() {
        waitUntil("#searchTextField", visible());
        TextField textField = find("#searchTextField");
        clickOnSearchComboBox(3);
        String text = "Author автор";
        textField.setText(text);
        click("#search");
        TableView tv = find("#searchTable");
        sleep(1000);
        assertTrue(tv.getItems().size() != 0);
        Mockito.verify(bookApiRepository, times(1)).getBookListByAuthor(anyString(), eq(30));
    }

    @Test
    public void clickedSearchButton_searchNormalGenre_TablesShows() {
        waitUntil("#searchTextField", visible());
        TextField textField = find("#searchTextField");
        clickOnSearchComboBox(4);
        String text = "Genre genre";
        textField.setText(text);
        click("#search");
        TableView tv = find("#searchTable");
        sleep(1000);
        assertTrue(tv.getItems().size() != 0);
        Mockito.verify(bookApiRepository, times(1)).getBookListByGenre(anyString(), eq(30));
    }

    @Test
    public void clickedSearchButton_emptySearchRequest_warningWindowShowing() {
        waitUntil("#searchTextField", visible());
        TextField textField = find("#searchTextField");
        clickOnSearchComboBox(1);
        textField.setText("");
        click("#search");
        sleep(1000);
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        click("OK");
    }

    @Test
    public void clickedSearchButton_exceptionThrows_warningWindowShowing() {
        waitUntil("#searchTextField", visible());
        TextField textField = find("#searchTextField");
        clickOnSearchComboBox(1);
        textField.setText("0000");
        click("#search");
        sleep(1000);
        FxAssert.verifyThat(new FxRobot().window("Ошибка"), WindowMatchers.isShowing());
        click("OK");
    }

    @Test
    public void clickedSearchButton_zeroBooksFound_infoWindowShowing() {
        waitUntil("#searchTextField", visible());
        TextField textField = find("#searchTextField");
        clickOnSearchComboBox(1);
        textField.setText("555");
        click("#search");
        sleep(1000);
        FxAssert.verifyThat(new FxRobot().window("Информация"), WindowMatchers.isShowing());
        click("OK");
    }

    @Test
    public void clickedSearchButton_wrongISBN_infoWindowShowing() {
        waitUntil("#searchTextField", visible());
        TextField textField = find("#searchTextField");
        clickOnSearchComboBox(1);
        textField.setText("9342-adawd-wdw");
        click("#search");
        sleep(1000);
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        click("OK");
    }


    public void clickOnSearchComboBox(int typeOrder) {
        click("#searchTypeComboBox");
        Point p = MouseInfo.getPointerInfo().getLocation();
        move(p.x, p.y + 25 * typeOrder);
        click(MouseButton.PRIMARY);
        sleep(1000);
    }

    @Test
    public void clickedSearchButton_searchNormalTitle_TablesShows() {
        waitUntil("#searchTextField", visible());
        TextField textField = find("#searchTextField");
        clickOnSearchComboBox(2);
        String text = "Title Название";
        textField.setText(text);
        click("#search");
        TableView tv = find("#searchTable");
        sleep(1000);
        assertTrue(tv.getItems().size() != 0);
        Node node = new FxRobot().lookup(".table-row-cell").nth(0).query();
        Bounds boundsInScreen = node.localToScreen(node.getBoundsInLocal());
        move(boundsInScreen.getMaxX() - 5, boundsInScreen.getMinY());
        click(MouseButton.PRIMARY);
        Mockito.verify(bookApiRepository, times(1)).getBookListByTitle(anyString(), eq(30));
    }

    @SneakyThrows
    @Override
    protected Parent getRootNode() {
        if (parent == null) parent = loader.load();
        if (internetSearchController == null) internetSearchController = loader.getController();
        return parent;
    }


}