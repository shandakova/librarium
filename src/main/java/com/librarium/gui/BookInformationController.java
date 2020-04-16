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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.SneakyThrows;
import org.controlsfx.control.Rating;
import org.springframework.stereotype.Controller;

import java.io.IOException;


@Controller
public class BookInformationController {
    private BookRepository bookRepository;
    private CommentRepository commentRepository;
    private QuoteRepository quoteRepository;
    private ListsRepository listsRepository;
    private Book book;
    private MyLibraryController mlc;
    private ListsController lc;
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
    private Button editBook;
    @FXML
    private Button addComment;
    @FXML
    private Button addList;

    public void initData(BookRepository bookRepository, CommentRepository commentRepository, QuoteRepository quoteRepository, ListsRepository listsRepository, Book book, MyLibraryController mlc, ListsController lc) {
        this.book = book;
        this.bookRepository = bookRepository;
        this.commentRepository = commentRepository;
        this.listsRepository = listsRepository;
        this.quoteRepository = quoteRepository;
        this.mlc = mlc;
        this.lc = lc;
        author.setText(book.getAuthor());
        author.setWrapText(true);
        title.setText(book.getName());
        title.setWrapText(true);
        genre.setText(book.getGenre());
        year.setText(String.valueOf(book.getYear()));
        TableColumn<Book, Number> colRating = new TableColumn<>("Рейтинг");
        colRating.setCellValueFactory(new PropertyValueFactory<>("rate"));

        colRating.setCellFactory(table -> new TableCell<Book, Number>() {
            private final Rating rating = new Rating(5);
            private final ChangeListener<Number> ratingChangeListener;

            {
                ratingChangeListener = (observable, oldValue, newValue) -> {
                    Book book = this.getTableView().getItems().get(this.getIndex());
                    book.setRate(newValue.intValue());
                    bookRepository.saveAndFlush(book);
                };
                setStyle("-fx-alignment: CENTER;");
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
        ObservableList<Book> observeListb = FXCollections.observableArrayList();
        observeListb.addAll(book);
        rate.setItems(observeListb);

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
                    @SneakyThrows
                    @Override
                    public void handle(MouseEvent event) {
                        Quote quote = cell.getTableView().getItems().get(cell.getIndex());
                        Stage stage = new Stage();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editquote.fxml"));
                        //try {
                            Parent root = loader.load();
                            stage.setScene(new Scene(root));
                        /*} catch (IOException e) {
                            e.printStackTrace();
                        }*/
                        stage.setTitle("Редактирование цитаты");
                        stage.initModality(Modality.WINDOW_MODAL);
                        stage.initOwner(
                                ((Node) event.getSource()).getScene().getWindow());
                        EditQuoteController controller = loader.getController();
                        controller.initData(quoteRepository, quote);
                        stage.setOnHidden(e ->
                                updateQuoteTable());
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
                    @SneakyThrows
                    @Override
                    public void handle(MouseEvent event) {
                        Comment comment = cell.getTableView().getItems().get(cell.getIndex());
                        Stage stage = new Stage();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editcomment.fxml"));
                        //try {
                            Parent root = loader.load();
                            stage.setScene(new Scene(root));
                        /*} catch (IOException e) {
                            e.printStackTrace();
                        }*/
                        stage.setTitle("Редактирование комментария");
                        stage.initModality(Modality.WINDOW_MODAL);
                        stage.initOwner(
                                ((Node) event.getSource()).getScene().getWindow());
                        EditCommentController controller = loader.getController();
                        controller.initData(commentRepository, comment);
                        stage.setOnHidden(e ->
                                updateCommentTable());
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
                    @SneakyThrows
                    @Override
                    public void handle(MouseEvent event) {
                        Lists list = cell.getTableView().getItems().get(cell.getIndex());
                        Stage stage = new Stage();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/viewlist.fxml"));
                        //try {
                            Parent root = loader.load();
                            stage.setScene(new Scene(root));
                        /*} catch (IOException e) {
                            e.printStackTrace();
                        }*/
                        stage.setTitle("Просмотр листа");
                        stage.initModality(Modality.WINDOW_MODAL);
                        stage.initOwner(
                                ((Node) event.getSource()).getScene().getWindow());
                        ViewListController controller = loader.getController();
                        controller.initData(bookRepository, listsRepository, list);
                        stage.setOnHidden(e -> {
                            updateListTable();
                            lc.clickedSearchButton();
                        });
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
        stage.initOwner(editBook.getScene().getWindow());
        EditPageController controller = loader.getController();
        controller.initData(bookRepository, book, this);
        stage.show();
    }

    @SneakyThrows
    @FXML
    private void clickedAddQuoteButton() {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/addquote.fxml"));
        //try {
            Parent root = loader.load();
            stage.setScene(new Scene(root));
        /*} catch (IOException e) {
            e.printStackTrace();
        }*/
        stage.setTitle("Добавление цитаты");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(editBook.getScene().getWindow());
        AddQuoteController controller = loader.getController();
        controller.initData(quoteRepository, book);
        stage.setOnHidden(e ->
                updateQuoteTable());
        stage.show();
    }

    @SneakyThrows
    @FXML
    private void clickedAddCommentButton() {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/addcomment.fxml"));
        //try {
            Parent root = loader.load();
            stage.setScene(new Scene(root));
        /*} catch (IOException e) {
            e.printStackTrace();
        }*/
        stage.setTitle("Добавление комментария");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(addComment.getScene().getWindow());
        AddCommentController controller = loader.getController();
        controller.initData(book, commentRepository);
        stage.setOnHidden(e -> updateCommentTable());
        stage.show();
    }

    @SneakyThrows
    @FXML
    private void clickedAddListButton() {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/addlist.fxml"));
        //try {
            Parent root = loader.load();
            stage.setScene(new Scene(root));
        /*} catch (IOException e) {
            e.printStackTrace();
        }*/
        stage.setTitle("Добавление книги в лист");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(addList.getScene().getWindow());
        AddListController controller = loader.getController();
        controller.initData(bookRepository, book, listsRepository);
        stage.setOnHidden(e -> updateListTable());
        stage.show();
    }

    private void updateQuoteTable() {
        quoteTable.getItems().remove(0, quoteTable.getItems().size());
        quoteTable.setItems(FXCollections.observableArrayList(quoteRepository.findByBook(book)));
    }

    private void updateCommentTable() {
        commentTable.getItems().remove(0, commentTable.getItems().size());
        commentTable.setItems(FXCollections.observableArrayList(commentRepository.findByBook(book)));
    }

    private void updateListTable() {
        listTable.getItems().remove(0, listTable.getItems().size());
        listTable.setItems(FXCollections.observableArrayList(listsRepository.findByBooks(book)));
    }

    public void update() {
        book = bookRepository.findById(book.getId()).get();
        author.setText(book.getAuthor());
        author.setWrapText(true);
        title.setText(book.getName());
        title.setWrapText(true);
        genre.setText(book.getGenre());
        year.setText(String.valueOf(book.getYear()));
        mlc.update();
    }

    public void updatePagesAndClose() {
        mlc.update();
        editBook.getScene().getWindow().hide();
    }
}
