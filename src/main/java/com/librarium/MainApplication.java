package com.librarium;

import com.google.common.base.Throwables;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.FileNotFoundException;

import static java.lang.System.exit;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.librarium.repository")
public class MainApplication extends Application {
    private ConfigurableApplicationContext springContext;
    private FXMLLoader fxmlLoader;
    private boolean isDatabaseOk = true;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        try {
            springContext = SpringApplication.run(MainApplication.class);
            fxmlLoader = new FXMLLoader();
            fxmlLoader.setControllerFactory(springContext::getBean);
        } catch (Exception e) {
            if (Throwables.getRootCause(e) instanceof FileNotFoundException) {
                isDatabaseOk = false;
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText(null);
                    alert.getButtonTypes().set(0, new ButtonType("OK", ButtonBar.ButtonData.LEFT));
                    alert.setContentText("Связь с базой данных потеряна! Проверьте наличие базы данных.");
                    alert.setOnCloseRequest(EventHandler -> exit(1));
                    alert.show();
                });
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        if (isDatabaseOk) {
            fxmlLoader.setLocation(getClass().getResource("/fxml/mainwindow.fxml"));
            Parent rootNode = fxmlLoader.load();
            primaryStage.setTitle("Librarium");
            Scene scene = new Scene(rootNode);
            primaryStage.setScene(scene);
            primaryStage.show();
        }
    }

    @Override
    public void stop() {
        springContext.stop();
        exit(0);
    }
}
