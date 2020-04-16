package com.librarium.gui;

import com.librarium.entity.Book;
import com.librarium.repository.BookApiRepository;
import com.librarium.repository.BookRepository;
import io.sniffy.socket.DisableSockets;
import io.sniffy.test.junit.SniffyRule;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.loadui.testfx.GuiTest;
import org.mockito.*;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.matcher.base.WindowMatchers;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.loadui.testfx.controls.impl.VisibleNodesMatcher.visible;
import static org.mockito.Mockito.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AddBookControllerTest extends GuiTest {
    @Rule
    public SniffyRule sniffyRule = new SniffyRule();
    final protected FXMLLoader loader = new FXMLLoader(AddListController.class.getResource("/fxml/addbook.fxml"));
    protected Parent parent;
    @InjectMocks
    private AddBookController addBookController;
    @Mock
    private MyLibraryController mlc;
    @Mock
    private BookApiRepository bar;
    @Mock
    private BookRepository bookRepository;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        doNothing().when(mlc).update();
        Mockito.when(bookRepository.save(any(Book.class))).thenAnswer(i -> i.getArguments()[0]);
        Mockito.when(bar.getBookListByTitle(anyString(), eq(30))).thenAnswer(i -> {
            String title = (String) i.getArguments()[0];
            Book book = new Book(title);
            if (title.contains("full")) {
                book.setYear(2111);
                book.setGenre("adw");
                book.setAuthor("автор");
                book.setISBN("234546");
            }
            return Arrays.asList(book);
        });

    }

    @Test
    public void clickAddBookButton_wrongAuthor_warningWindowShowing() {
        TextField addTitle = find("#addTitle");
        addTitle.setText("Normal Title");
        TextField addAuthor = find("#addAuthor");
        addAuthor.setText("12345121");
        click("#addBook");
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        click("OK");
    }

    @Test
    public void clickAddBookButton_wrongTitle_warningWindowShowing() {
        TextField addTitle = find("#addTitle");
        addTitle.setText("étrangère");
        click("#addBook");
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        click("OK");
    }

    @Test
    public void clickAddBookButton_emptyTitle_warningWindowShowing() {
        TextField addTitle = find("#addTitle");
        addTitle.setText("");
        click("#addBook");
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        click("OK");
    }

    @Test
    public void clickAddBookButton_wrongGenre_warningWindowShowing() {
        TextField addTitle = find("#addTitle");
        addTitle.setText("Normal Title");
        TextField addGenre = find("#addGenre");
        addGenre.setText("12345121");
        click("#addBook");
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        click("OK");
    }

    @Test
    public void clickAddBookButton_wrongYear_warningWindowShowing() {
        TextField addTitle = find("#addTitle");
        addTitle.setText("Normal Title");
        TextField addYear = find("#addYear");
        addYear.setText("awdwda");
        click("#addBook");
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        click("OK");
    }

    @Test
    public void clickAddBookButton_wrongISBN_warningWindowShowing() {
        TextField addTitle = find("#addTitle");
        addTitle.setText("Normal Title");
        TextField addISBN = find("#addISBN");
        addISBN.setText("awdwda");
        click("#addBook");
        FxAssert.verifyThat(new FxRobot().window("Предупреждение"), WindowMatchers.isShowing());
        click("OK");
    }

    @Test
    public void clickAddBookButton_allFieldsFilled_infoWindowShowing() {
        ((TextField) find("#addTitle")).setText("Normal Title");
        ((TextField) find("#addISBN")).setText("123546");
        ((TextField) find("#addYear")).setText("1234");
        ((TextField) find("#addGenre")).setText("другое");
        ((TextField) find("#addAuthor")).setText("author");
        click("#rate");
        click("#addBook");
        ArgumentCaptor<Book> argument = ArgumentCaptor.forClass(Book.class);
        Mockito.verify(bookRepository, times(1)).save(argument.capture());
        assertEquals(3, argument.getValue().getRate());
        assertEquals("Normal Title", argument.getValue().getName());
        assertEquals("другое", argument.getValue().getGenre());
        assertEquals("123546", argument.getValue().getISBN());
        assertEquals(1234, argument.getValue().getYear());
        assertEquals("author", argument.getValue().getAuthor());
        FxAssert.verifyThat(new FxRobot().window("Поздравляем!"), WindowMatchers.isShowing());
        click("OK");
        assertThatAllFieldsEmpty();
    }

    public void assertThatAllFieldsEmpty() {
        assertTrue(((TextField) find("#addTitle")).getText().isEmpty());
        assertTrue(((TextField) find("#addISBN")).getText().isEmpty());
        assertTrue(((TextField) find("#addGenre")).getText().isEmpty());
        assertTrue(((TextField) find("#addAuthor")).getText().isEmpty());
        assertTrue(((TextField) find("#addYear")).getText().isEmpty());
    }

    @Test
    public void clickAddBookButton_addFromInternet_infoWindowShowing() {
        click("#searchInternet");
        sleep(2000);
        TextField textField = find("#searchTextField");
        textField.setText("Some Title");
        click("#search");
        sleep(1000);
        Node node = new FxRobot().lookup("#searchTable").lookup(".table-row-cell").nth(0).query();
        Bounds boundsInScreen = node.localToScreen(node.getBoundsInLocal());
        move(boundsInScreen.getMaxX() - 5, boundsInScreen.getMinY());
        click(MouseButton.PRIMARY);
        click("#addBook");
        sleep(1000);
        ArgumentCaptor<Book> argument = ArgumentCaptor.forClass(Book.class);
        Mockito.verify(bookRepository, times(1)).save(argument.capture());
        assertEquals("Some Title", argument.getValue().getName());
        FxAssert.verifyThat(new FxRobot().window("Поздравляем!"), WindowMatchers.isShowing());
        click("OK");
        assertThatAllFieldsEmpty();
        Mockito.verify(mlc, times(1)).update();
        sleep(1000);
    }

    @Test
    public void clickAddBookButton_addFromInternetFullBook_infoWindowShowing() {
        click("#searchInternet");
        sleep(2000);
        TextField textField = find("#searchTextField");
        textField.setText("Some full Title");
        click("#search");
        Node node = new FxRobot().lookup("#searchTable").lookup(".table-row-cell").nth(0).query();
        Bounds boundsInScreen = node.localToScreen(node.getBoundsInLocal());
        move(boundsInScreen.getMaxX() - 5, boundsInScreen.getMinY());
        click(MouseButton.PRIMARY);
        click("#addBook");
        ArgumentCaptor<Book> argument = ArgumentCaptor.forClass(Book.class);
        Mockito.verify(bookRepository, times(1)).save(argument.capture());
        assertEquals("Some full Title", argument.getValue().getName());
        assertEquals("234546", argument.getValue().getISBN());
        assertEquals("автор", argument.getValue().getAuthor());
        assertEquals(2111, argument.getValue().getYear());
        assertEquals("adw", argument.getValue().getGenre());

        FxAssert.verifyThat(new FxRobot().window("Поздравляем!"), WindowMatchers.isShowing());
        click("OK");
        assertThatAllFieldsEmpty();
        Mockito.verify(mlc, times(1)).update();
        sleep(1000);
    }

    @Test
    @DisableSockets
    public void searchInInternetButton_NoConnection_errorWindowShowing() {
        sleep(2000);
        waitUntil("#searchInternet", visible());
        click("#searchInternet");
        sleep(1000);
        FxAssert.verifyThat(new FxRobot().window("Ошибка"), WindowMatchers.isShowing());
        click("OK");
    }


    @SneakyThrows
    @Override
    protected Parent getRootNode() {
        if (parent == null) parent = loader.load();
        if (addBookController == null) addBookController = loader.getController();
        return parent;
    }

    @Before
    public void sleep() {
        sleep(1000);
    }
}