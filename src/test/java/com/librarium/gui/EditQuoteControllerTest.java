package com.librarium.gui;

import com.librarium.entity.Quote;
import com.librarium.repository.QuoteRepository;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
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

public class EditQuoteControllerTest extends GuiTest {
    @Mock
    private QuoteRepository quoteRepository = mock(QuoteRepository.class);
    private EditQuoteController editQuoteController = mock(EditQuoteController.class);
    public Quote quote = new Quote();

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
        FXMLLoader loader = new FXMLLoader(AddCommentController.class.getResource("/fxml/editquote.fxml"));
        parent = loader.load();
        if (parent.getScene() == null) {
            stage.setScene(new Scene(parent));
        } else {
            stage.setScene(parent.getScene());
        }
        editQuoteController = loader.getController();
        quote.setQuotation("fhsduifh");
        editQuoteController.initData(quoteRepository, quote);
        stage.show();
        return parent;
    }

    @SneakyThrows
    @Test
    public void editQuote_anyText_quoteUpdated() {
        setupStage();
        waitUntil("#editQuote", visible());
        TextArea textArea = find("#editQuote");
        String text = "adsfdghj";
        textArea.setText(text);
        click("#updateQuote");
        ArgumentCaptor<Quote> argument = ArgumentCaptor.forClass(Quote.class);
        Mockito.verify(quoteRepository).saveAndFlush(argument.capture());
        assertTrue(argument.getValue().getQuotation().equals(text));
        closeCurrentWindow();
    }

    @SneakyThrows
    @Test
    public void deleteQuote_anyText_quoteDeleted() {
        setupStage();
        waitUntil("#deleteQuote", visible());
        click("#deleteQuote");
        ArgumentCaptor<Quote> argument = ArgumentCaptor.forClass(Quote.class);
        Mockito.verify(quoteRepository).delete(argument.capture());
        assertTrue(argument.getValue().getQuotation().equals("fhsduifh"));
        closeCurrentWindow();
    }
}
