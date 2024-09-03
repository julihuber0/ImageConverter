package com.julian.imageconverter.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class ConverterApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ConverterApplication.class.getResource("converter.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("ImageConverter");
        Image icon = new Image(Objects.requireNonNull(ConverterApplication.class.getResource("img/icon.png")).toString());
        stage.getIcons().add(icon);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm quit");
            alert.setHeaderText("Quit ImageConverter?");
            alert.setContentText("Do you want to exit?");
            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            alertStage.getIcons().add(new Image(Objects.requireNonNull(ConverterApplication.class.getResource("img/icon.png")).toString()));
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == ButtonType.CANCEL) {
                    e.consume();
                }
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}
