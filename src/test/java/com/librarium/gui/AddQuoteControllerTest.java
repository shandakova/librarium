package com.librarium.gui;

import com.librarium.entity.Book;
import com.librarium.entity.Quote;
import com.librarium.repository.QuoteRepository;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static junit.framework.TestCase.assertTrue;
import static org.loadui.testfx.controls.impl.VisibleNodesMatcher.visible;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

public class AddQuoteControllerTest extends GuiTest {
    @Mock
    private QuoteRepository quoteRepository = mock(QuoteRepository.class);
    private AddQuoteController addQuoteController = mock(AddQuoteController.class);

    @SneakyThrows
    @Before
    public void initMock() {
        Mockito.when(quoteRepository.saveAndFlush(any(Quote.class))).thenAnswer(i -> i.getArguments()[0]);
        MockitoAnnotations.initMocks(this);
    }

    @SneakyThrows
    @Override
    protected Parent getRootNode() {
        Parent parent = null;
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(AddCommentController.class.getResource("/fxml/addquote.fxml"));
        parent = loader.load();
        //Scene scene = parent.getScene();
        if (parent.getScene() == null) {
            stage.setScene(new Scene(parent));
        } else {
            stage.setScene(parent.getScene());
        }
        addQuoteController = loader.getController();
        addQuoteController.initData(quoteRepository, new Book());
        stage.show();
        return parent;
    }

    @SneakyThrows
    @Test
    public void saveQuote_anyText_quoteSaved() {
        setupStage();
        waitUntil("#textArea", visible());
        TextArea textArea = find("#textArea");
        String text = "adsfdghj";
        textArea.setText(text);
        click("#saveQuote");
        ArgumentCaptor<Quote> argument = ArgumentCaptor.forClass(Quote.class);
        Mockito.verify(quoteRepository).saveAndFlush(argument.capture());
        assertTrue(argument.getValue().getQuotation().equals(text));
        closeCurrentWindow();
    }

    @SneakyThrows
    @Test
    public void cancel_click_stageNotShowing() {
        setupStage();
        click("#cancel");
        closeCurrentWindow();
    }
}
