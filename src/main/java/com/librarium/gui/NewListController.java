package com.librarium.gui;

import com.librarium.entity.Lists;
import com.librarium.repository.ListsRepository;
import com.librarium.utils.FieldParser;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class NewListController {
    @FXML
    private TextField textField;
    @FXML
    private Button okBtn;
    private ListsRepository lr;
    public void initData(ListsRepository lr){
        this.lr = lr;
    }

    public void clickedOk() {
        String name = textField.getText();
        if (FieldParser.isBlankString(name)){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.getButtonTypes().set(0, new ButtonType("OK", ButtonBar.ButtonData.LEFT));
            alert.setContentText("Поле не должно быть пустым!");
            alert.show();
        } else if (lr.findByName(name).size()>0){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.getButtonTypes().set(0, new ButtonType("OK", ButtonBar.ButtonData.LEFT));
            alert.setContentText("Такой лист уже существует!");
            alert.show();
        } else {
            lr.saveAndFlush(new Lists(name));
            lr.flush();
            okBtn.getScene().getWindow().hide();
        }
    }
}
