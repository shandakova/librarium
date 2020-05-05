package com.librarium.gui;

import com.librarium.entity.Book;
import com.librarium.entity.Lists;
import com.librarium.repository.BookRepository;
import com.librarium.repository.ListsRepository;
import com.librarium.utils.FieldParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.ResourceBundle;

@Controller
public class ListsController implements Initializable {
    @Autowired
    private ListsRepository listsRepository;
    @Autowired
    private BookRepository bookRepository;
    @FXML
    private ComboBox searchTypeComboBox;
    @FXML
    private TextArea searchTextField;
    @FXML
    private TableView<Lists> searchTable;

    @FXML
    private void changedSearchType() {
        if (searchTypeComboBox.getValue() == "Все") {
            searchTextField.setDisable(true);
        } else {
            searchTextField.setDisable(false);
        }
    }

    public void init(BookRepository bookRepository, ListsRepository listsRepository) {
        this.bookRepository = bookRepository;
        this.listsRepository = listsRepository;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTable();

    }

    public void initializeTable() {
        ObservableList<String> list = FXCollections.observableArrayList(
                Arrays.asList("Все", "Название"));
        searchTypeComboBox.setItems(list);
        searchTypeComboBox.setValue("Все");
        searchTextField.setDisable(true);
        searchTable.setPlaceholder(new Label("Пока здесь нет листов :с"));
        searchTable.setEditable(true);
        ObservableList<Lists> observeList = FXCollections.observableArrayList();
        if (listsRepository != null) observeList.addAll((Collection<? extends Lists>) listsRepository.findAll());

        searchTable.setItems(observeList);
        TableColumn<Lists, String> colName = new TableColumn<>("Название");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setCellFactory(new Callback<TableColumn<Lists, String>, TableCell<Lists, String>>() {
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
                        System.out.println(list);
                        Stage stage = new Stage();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/viewlist.fxml"));
                        try {
                            Parent root = loader.load();
                            stage.setScene(new Scene(root));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        stage.setTitle("Информация о листе");
                        stage.initModality(Modality.WINDOW_MODAL);
                        stage.initOwner(
                                ((Node) event.getSource()).getScene().getWindow());
                        ViewListController controller = loader.getController();
                        controller.initData(bookRepository, listsRepository, list);
                        stage.setOnHidden(e -> {
                            clickedSearchButton();
                        });
                        stage.show();
                    }
                });
                cell.setCursor(Cursor.HAND);
                return cell;
            }
        });
        TableColumn<Lists, Number> colSize = new TableColumn<>("Число книг в листе");
        colSize.setCellValueFactory(new PropertyValueFactory<>("size"));

        searchTable.getColumns().addAll(colName, colSize);
    }

    public void clickedSearchButton() {
        if (searchTypeComboBox.getValue() == "Все") {
            searchTable.setItems(FXCollections.observableArrayList());
            searchTable.setItems(FXCollections.observableArrayList(listsRepository.findAll()));
        } else {
            if (FieldParser.isBlankString(searchTextField.getText())) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Предупреждение");
                alert.setHeaderText(null);
                alert.getButtonTypes().set(0, new ButtonType("OK", ButtonBar.ButtonData.LEFT));
                alert.setContentText("Поле не должно быть пустым!");
                alert.showAndWait();
                return;
            }
            searchTable.setItems(FXCollections.observableArrayList());
            searchTable.setItems(FXCollections.observableArrayList(
                    listsRepository.findByNameContainsIgnoreCase(searchTextField.getText())));
        }
    }

    @FXML
    private void addList() {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/newlist.fxml"));
        try {
            Parent root = loader.load();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Добавить лист");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(searchTable.getScene().getWindow());
        NewListController controller = loader.getController();
        controller.initData(listsRepository);
        stage.setOnHidden(t -> update());
        stage.show();
    }

    public void update() {
        if (!FieldParser.isBlankString(searchTextField.getText()) || searchTypeComboBox.getValue().equals("Все")) {
            clickedSearchButton();
        }
    }

}
