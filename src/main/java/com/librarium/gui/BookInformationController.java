package com.librarium.gui;

import com.librarium.entity.Book;
import com.librarium.entity.Comment;
import com.librarium.entity.Lists;
import com.librarium.entity.Quote;
import com.librarium.repository.BookRepository;
import com.librarium.repository.CommentRepository;
import com.librarium.repository.ListsRepository;
import com.librarium.repository.QuoteRepository;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.Rating;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

@Controller
public class BookInformationController implements Initializable {
    private BookRepository bookRepository;
    private CommentRepository commentRepository;
    private QuoteRepository quoteRepository;
    private ListsRepository listsRepository;
    private Book book;
    @FXML
    private Label author;
    @FXML
    private Label title;
    @FXML
    private Label genre;
    @FXML
    private Label year;
    @FXML
    private TableView<Book> rate;
    @FXML
    private TableView<Quote> quoteTable;
    @FXML
    private TableView<Comment> commentTable;
    @FXML
    private TableView<Lists> listTable;
    @FXML
    private Button closeInformation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void initData(BookRepository bookRepository, CommentRepository commentRepository, QuoteRepository quoteRepository, ListsRepository listsRepository, Book book) {
        this.book = book;
        this.bookRepository = bookRepository;
        this.commentRepository = commentRepository;
        this.listsRepository = listsRepository;
        this.quoteRepository = quoteRepository;
        author.setText(book.getAuthor());
        title.setText(book.getName());
        genre.setText(book.getGenre());
        year.setText(String.valueOf(book.getYear()));
        TableColumn<Book, Number> colRating = new TableColumn<>("Рейтинг");
        colRating.setCellValueFactory(new PropertyValueFactory<>("rate"));

        colRating.setCellFactory(table -> new TableCell<Book, Number>() {
        private final Rating rating;
        private final ChangeListener<Number> ratingChangeListener;

        {
            rating = new Rating(book.getRate());
            ratingChangeListener = (observable, oldValue, newValue) -> {
                TableColumn<?, Number> column = getTableColumn();
                Book book = this.getTableView().getItems().get(this.getIndex());
                book.setRate(newValue.intValue());
                bookRepository.save(book);
            };
        }

        @Override
        protected void updateItem(Number item, boolean empty) {
            super.updateItem(item, empty);
            rating.ratingProperty().removeListener(ratingChangeListener);
            if (empty) {
                setGraphic(null);
            } else {
                rating.setRating(item.doubleValue());
                rating.ratingProperty().addListener(ratingChangeListener);
                setGraphic(rating);
            }
        }
        });
        rate.getColumns().addAll(colRating);

        quoteTable.setPlaceholder(new Label("Пока здесь нет цитат :с"));
        quoteTable.setEditable(false);
        ObservableList<Quote> observeList = FXCollections.observableArrayList();
        observeList.addAll(quoteRepository.findByBook(book));

        quoteTable.setItems(observeList);
        TableColumn<Quote, String> colQuote = new TableColumn<>("Цитата");
        colQuote.setCellValueFactory(new PropertyValueFactory<>("quotation"));
        colQuote.setCellFactory(new Callback<TableColumn<Quote, String>, TableCell<Quote, String>>() {
            @Override
            public TableCell<Quote, String> call(TableColumn<Quote, String> col) {
                final TableCell<Quote, String> cell = new TableCell<Quote, String>() {
                    @Override
                    public void updateItem(String firstName, boolean empty) {
                        super.updateItem(firstName, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(firstName);
                        }
                    }
                };
                cell.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        Quote quote = cell.getTableView().getItems().get(cell.getIndex());
                        Stage stage = new Stage();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editquote.fxml"));
                        try {
                            Parent root = loader.load();
                            stage.setScene(new Scene(root));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        stage.setTitle("Редактирование цитаты");
                        stage.initModality(Modality.WINDOW_MODAL);
                        stage.initOwner(
                                ((Node) event.getSource()).getScene().getWindow());
                        EditQuoteController controller = loader.getController();
                        controller.initData(bookRepository, quoteRepository, quote);

                        stage.show();
                    }
                });
                return cell;
            }
        });
        quoteTable.getColumns().addAll(colQuote);

        commentTable.setPlaceholder(new Label("Пока здесь нет комментариев :с"));
        commentTable.setEditable(false);
        ObservableList<Comment> observeListCom = FXCollections.observableArrayList();
        observeListCom.addAll(commentRepository.findByBook(book));

        commentTable.setItems(observeListCom);
        TableColumn<Comment, String> colComment = new TableColumn<>("Комментарий");
        colComment.setCellValueFactory(new PropertyValueFactory<>("comment"));
        colComment.setCellFactory(new Callback<TableColumn<Comment, String>, TableCell<Comment, String>>() {
            @Override
            public TableCell<Comment, String> call(TableColumn<Comment, String> col) {
                final TableCell<Comment, String> cell = new TableCell<Comment, String>() {
                    @Override
                    public void updateItem(String firstName, boolean empty) {
                        super.updateItem(firstName, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(firstName);
                        }
                    }
                };
                cell.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        Comment comment = cell.getTableView().getItems().get(cell.getIndex());
                        Stage stage = new Stage();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editcomment.fxml"));
                        try {
                            Parent root = loader.load();
                            stage.setScene(new Scene(root));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        stage.setTitle("Редактирование комментария");
                        stage.initModality(Modality.WINDOW_MODAL);
                        stage.initOwner(
                                ((Node) event.getSource()).getScene().getWindow());
                        EditCommentController controller = loader.getController();
                        controller.initData(bookRepository, commentRepository, comment);

                        stage.show();
                    }
                });
                return cell;
            }
        });
        commentTable.getColumns().addAll(colComment);

        listTable.setPlaceholder(new Label("Пока здесь нет листов :с"));
        listTable.setEditable(false);
        ObservableList<Lists> observeListList = FXCollections.observableArrayList();
        observeListList.addAll(listsRepository.findByBooks(book));

        listTable.setItems(observeListList);
        TableColumn<Lists, String> colList = new TableColumn<>("Лист");
        colList.setCellValueFactory(new PropertyValueFactory<>("name"));
        colList.setCellFactory(new Callback<TableColumn<Lists, String>, TableCell<Lists, String>>() {
            @Override
            public TableCell<Lists, String> call(TableColumn<Lists, String> col) {
                final TableCell<Lists, String> cell = new TableCell<Lists, String>() {
                    @Override
                    public void updateItem(String firstName, boolean empty) {
                        super.updateItem(firstName, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(firstName);
                        }
                    }
                };
                cell.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        Lists list = cell.getTableView().getItems().get(cell.getIndex());
                        Stage stage = new Stage();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/viewlist.fxml"));
                        try {
                            Parent root = loader.load();
                            stage.setScene(new Scene(root));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        stage.setTitle("Просмотр листа");
                        stage.initModality(Modality.WINDOW_MODAL);
                        stage.initOwner(
                                ((Node) event.getSource()).getScene().getWindow());
                        ViewListController controller = loader.getController();
                        controller.initData(bookRepository, listsRepository, list);

                        stage.show();
                    }
                });
                return cell;
            }
        });
        listTable.getColumns().addAll(colList);
    }

    @FXML
    private void clickedEditBookButton() {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editpage.fxml"));
        try {
            Parent root = loader.load();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Окно редактирования");
        stage.initModality(Modality.WINDOW_MODAL);
        /*stage.initOwner(
                ((Node) event.getSource()).getScene().getWindow());*/ //???????????
        EditPageController controller = loader.getController();
        controller.initData(bookRepository, book);

        stage.show();
    }

    @FXML
    private void clickedAddQuoteButton() {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/addquote.fxml"));
        try {
            Parent root = loader.load();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Окно добавления цитаты");
        stage.initModality(Modality.WINDOW_MODAL);
        /*stage.initOwner(
                ((Node) event.getSource()).getScene().getWindow());*/ //???????????
        AddQuoteController controller = loader.getController();
        controller.initData(quoteRepository, book, bookRepository);

        stage.show();
    }

    @FXML
    private void clickedAddCommentButton() {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/addcomment.fxml"));
        try {
            Parent root = loader.load();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Окно добавления комментария");
        stage.initModality(Modality.WINDOW_MODAL);
        /*stage.initOwner(
                ((Node) event.getSource()).getScene().getWindow());*/ //???????????
        AddCommentController controller = loader.getController();
        controller.initData(bookRepository, book, commentRepository);

        stage.show();
    }

    @FXML
    private void clickedAddListButton() {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/addlist.fxml"));
        try {
            Parent root = loader.load();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Окно добавления книги в лист");
        stage.initModality(Modality.WINDOW_MODAL);
        /*stage.initOwner(
                ((Node) event.getSource()).getScene().getWindow());*/ //??????????? button instead of event
        AddListController controller = loader.getController();
        controller.initData(bookRepository, book, listsRepository);

        stage.show();
    }

    @FXML
    private void clickedCloseButton() {
        Stage stage = (Stage) closeInformation.getScene().getWindow();
        stage.close();
    }
}
