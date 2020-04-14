package com.librarium.gui;

import com.librarium.entity.Book;
import com.librarium.entity.Comment;
import com.librarium.entity.Lists;
import com.librarium.entity.Quote;
import com.librarium.repository.BookRepository;
import com.librarium.repository.CommentRepository;
import com.librarium.repository.ListsRepository;
import com.librarium.repository.QuoteRepository;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import lombok.SneakyThrows;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static org.loadui.testfx.controls.impl.VisibleNodesMatcher.visible;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

public class BookInformationControllerTest extends GuiTest {
    @Mock
    private BookRepository bookRepository = mock(BookRepository.class);
    private CommentRepository commentRepository = mock(CommentRepository.class);
    private QuoteRepository quoteRepository = mock(QuoteRepository.class);
    private ListsRepository listsRepository = mock(ListsRepository.class);
    private MyLibraryController mlc = mock(MyLibraryController.class);
    private BookInformationController bookInformationController = mock(BookInformationController.class);
    private Alert alert = mock(Alert.class);
    public Book book = new Book();

    @SneakyThrows
    @Before
    public void initMock() {
        Mockito.when(bookRepository.saveAndFlush(any(Book.class))).thenAnswer(i -> i.getArguments()[0]);
        Mockito.doNothing().when(mlc).update();
        MockitoAnnotations.initMocks(this);
    }

    @SneakyThrows
    @Override
    protected Parent getRootNode() {
        Parent parent = null;
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(AddCommentController.class.getResource("/fxml/bookInformation.fxml"));
        parent = loader.load();
        if (parent.getScene() == null) {
            stage.setScene(new Scene(parent));
        } else {
            stage.setScene(parent.getScene());
        }
        bookInformationController = loader.getController();
        book.setRate(5);
        book.setISBN("1324");
        book.setYear(1999);
        book.setGenre("romance");
        book.setName("test");
        book.setAuthor("Me");
        Quote quote = new Quote();
        quote.setQuotation("235");
        quote.setBook(book);
        Set<Quote> quotes = new HashSet<>();
        quotes.add(quote);
        book.setQuotes(quotes);
        Lists list1 = new Lists("1");
        list1.setBooks(Collections.singleton(book));
        Set<Lists> lists = new HashSet<>();
        lists.add(list1);
        book.setLists(lists);
        Comment comment = new Comment();
        comment.setComment("fsd");
        comment.setBook(book);
        Set<Comment> comments = new HashSet<>();
        comments.add(comment);
        book.setComments(comments);
        Mockito.when(quoteRepository.findAll()).thenAnswer(i -> Arrays.asList(quote));
        Mockito.when(quoteRepository.findByBook(book)).thenAnswer(i -> Arrays.asList(quote));
        Mockito.when(listsRepository.findAll()).thenAnswer(i -> Arrays.asList(list1));
        Mockito.when(listsRepository.findByBooks(book)).thenAnswer(i -> Arrays.asList(list1));
        Mockito.when(commentRepository.findAll()).thenAnswer(i -> Arrays.asList(comment));
        Mockito.when(commentRepository.findByBook(book)).thenAnswer(i -> Arrays.asList(comment));
        bookInformationController.initData(bookRepository, commentRepository, quoteRepository, listsRepository, book, mlc, new ListsController());
        stage.show();
        return parent;
    }

    @SneakyThrows
    @Test
    public void editBook_click_openWindow() {
        setupStage();
        waitUntil("#editBook", visible());
        click("#editBook");
        FxAssert.verifyThat(new FxRobot().window("Окно редактирования"), WindowMatchers.isShowing());
        closeCurrentWindow();
    }

    @SneakyThrows
    @Test
    public void addQuote_click_openWindow() {
        setupStage();
        waitUntil("#addQuote", visible());
        click("#addQuote");
        FxAssert.verifyThat(new FxRobot().window("Добавление цитаты"), WindowMatchers.isShowing());
        closeCurrentWindow();
    }

    @SneakyThrows
    @Test
    public void addComment_click_openWindow() {
        setupStage();
        waitUntil("#addComment", visible());
        click("#addComment");
        FxAssert.verifyThat(new FxRobot().window("Добавление комментария"), WindowMatchers.isShowing());
        closeCurrentWindow();
    }

    @SneakyThrows
    @Test
    public void addList_click_openWindow() {
        setupStage();
        waitUntil("#addList", visible());
        click("#addList");
        FxAssert.verifyThat(new FxRobot().window("Добавление книги в лист"), WindowMatchers.isShowing());
        closeCurrentWindow();
    }

    @SneakyThrows
    @Test
    public void onQuote_click_openWindow() {
        setupStage();
        waitUntil("#quoteTable", visible());
        click("#quoteTable");
        Point p = MouseInfo.getPointerInfo().getLocation();
        move(p.x, p.y - 20);
        sleep(1000);
        click(MouseButton.PRIMARY);
        FxAssert.verifyThat(new FxRobot().window("Редактирование цитаты"), WindowMatchers.isShowing());
        closeCurrentWindow();
    }

    @SneakyThrows
    @Test
    public void onComment_click_openWindow() {
        setupStage();
        waitUntil("#commentTable", visible());
        click("#commentTable");
        Point p = MouseInfo.getPointerInfo().getLocation();
        move(p.x, p.y - 50);
        sleep(1000);
        click(MouseButton.PRIMARY);
        FxAssert.verifyThat(new FxRobot().window("Редактирование комментария"), WindowMatchers.isShowing());
        closeCurrentWindow();
    }

    @SneakyThrows
    @Test
    public void onList_click_openWindow() {
        setupStage();
        waitUntil("#listTable", visible());
        click("#listTable");
        Point p = MouseInfo.getPointerInfo().getLocation();
        move(p.x, p.y - 45);
        sleep(1000);
        click(MouseButton.PRIMARY);
        FxAssert.verifyThat(new FxRobot().window("Просмотр листа"), WindowMatchers.isShowing());
        closeCurrentWindow();
    }

    @SneakyThrows
    @Test
    public void onRate_click_updateBook() {
        setupStage();
        waitUntil("#rate", visible());
        click("#rate");
        ArgumentCaptor<Book> argument = ArgumentCaptor.forClass(Book.class);
        Mockito.verify(bookRepository).saveAndFlush(argument.capture());
        assertEquals(argument.getValue().getRate(), 3);
        closeCurrentWindow();
    }
}
