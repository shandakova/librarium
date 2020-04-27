package com.librarium.gui;

import com.librarium.entity.dto.Book;
import com.librarium.entity.dto.VolumeInfo;
import com.librarium.repository.BookRepository;
import com.librarium.service.BookService;
import io.sniffy.socket.DisableSockets;
import io.sniffy.test.junit.SniffyRule;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClientResponseException;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.matcher.base.WindowMatchers;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.loadui.testfx.controls.impl.VisibleNodesMatcher.visible;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;


public class RecommendationControllerTest extends GuiTest {
    @Rule
    public SniffyRule sniffyRule = new SniffyRule();
    final protected FXMLLoader loader = new FXMLLoader(RecommendationController.class.getResource("/fxml/recommendation.fxml"));
    protected Parent parent;
    private RecommendationController recommendationController;
    @Mock
    private BookRepository bookRepository = mock(BookRepository.class);
    @Mock
    private MyLibraryController mlc = mock(MyLibraryController.class);
    @Mock
    private BookService bookService = mock(BookService.class);

    @Before
    public void initMocks() {
        Mockito.doReturn(Arrays.asList()).when(bookRepository).findAll();
        String author = "author";
        String bookName = "name";
        VolumeInfo info = new VolumeInfo();
        info.setAuthors(Collections.singletonList(author));
        info.setTitle(bookName);
        Book dtoBook = new Book();
        dtoBook.setVolumeInfo(info);
        Mockito.when(bookService.getRecommendation(any())).thenReturn((List<com.librarium.entity.dto.Book>) Arrays.asList(dtoBook));
        Mockito.doNothing().when(mlc).update();
        Mockito.when(bookRepository.saveAndFlush(any(com.librarium.entity.Book.class))).thenAnswer(i -> i.getArguments()[0]);
        Mockito.when(bookService.castBookFromDtoBook(any(Book.class))).thenCallRealMethod();
    }

    public void setMocksController() {
        recommendationController.setBookRepository(bookRepository);
        recommendationController.setBookService(bookService);
        recommendationController.setMlc(mlc);
    }

    @Test
    public void recommendationController_clickRefreshButton_booksShowing() {
        waitUntil("#refresh", visible());
        click("#refresh");
        TableView tv = find("#recTable");
        sleep(1000);
        assertTrue(tv.getItems().size() != 0);
        Node node = new FxRobot().lookup(".table-row-cell").nth(0).query();
        Book book = (Book) tv.getItems().get(0);
        assertEquals("name", book.getVolumeInfo().getTitle());
        sleep(1000);
        recommendationController.setBookService(null);
        click("#refresh");
    }

    @Test
    public void recommendationController_addBook_bookAdded() {
        waitUntil("#refresh", visible());
        click("#refresh");
        TableView tv = find("#recTable");
        sleep(1000);
        assertTrue(tv.getItems().size() != 0);
        Node node = new FxRobot().lookup(".table-row-cell").nth(0).query();
        Bounds boundsInScreen = node.localToScreen(node.getBoundsInLocal());
        move(boundsInScreen.getMaxX() - 5, boundsInScreen.getMinY() + 10);
        click(MouseButton.PRIMARY);
        sleep(1000);
        FxAssert.verifyThat(new FxRobot().window("Информация"), WindowMatchers.isShowing());
        click("OK");
    }

    @Test
    public void recommendationController_addBookWithErrorLanguage_bookNotAdded() {
        initBookByErrorLanguage();
        waitUntil("#refresh", visible());
        click("#refresh");
        TableView tv = find("#recTable");
        sleep(1000);
        assertTrue(tv.getItems().size() != 0);
        Node node = new FxRobot().lookup(".table-row-cell").nth(0).query();
        Bounds boundsInScreen = node.localToScreen(node.getBoundsInLocal());
        move(boundsInScreen.getMaxX() - 5, boundsInScreen.getMinY() + 10);
        click(MouseButton.PRIMARY);
        sleep(1000);
        FxAssert.verifyThat(new FxRobot().window("Информация"), WindowMatchers.isShowing());
        click("OK");
        Mockito.verify(bookRepository, times(0)).saveAndFlush(any());
    }

    public void initBookByErrorLanguage() {
        String bookName = " étrangère";
        VolumeInfo info = new VolumeInfo();
        info.setTitle(bookName);
        Book dtoBook = new Book();
        dtoBook.setVolumeInfo(info);
        Mockito.when(bookService.getRecommendation(any())).thenReturn((List<com.librarium.entity.dto.Book>) Arrays.asList(dtoBook));
    }

    @DisableSockets
    @Test
    public void recommendationController_noInternet_errorWindowShowing() {
        FxAssert.verifyThat(new FxRobot().window("Ошибка"), WindowMatchers.isShowing());
        click("OK");
    }


    @Test
    public void recommendationController_ServiceNotAvailable_errorWindowShowing() {
        Mockito.when(bookService.getRecommendation(any())).thenThrow(new RestClientResponseException("Not found", 404, "404", HttpHeaders.EMPTY,
                new byte[0], Charset.defaultCharset()));
        waitUntil("#refresh", visible());
        click("#refresh");
        FxAssert.verifyThat(new FxRobot().window("Ошибка"), WindowMatchers.isShowing());
        click("OK");
    }


    @SneakyThrows
    @Override
    protected Parent getRootNode() {
        initMocks();
        if (parent == null) parent = loader.load();
        if (recommendationController == null) recommendationController = loader.getController();
        setMocksController();
        return parent;
    }
}