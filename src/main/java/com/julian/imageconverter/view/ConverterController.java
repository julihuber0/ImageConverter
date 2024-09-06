package com.julian.imageconverter.view;

import com.julian.imageconverter.exceptions.ImageConverterException;
import com.julian.imageconverter.service.ConverterService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class ConverterController implements Initializable {

    @FXML
    private Label compressionLabel;

    @FXML
    private Slider compressionSlider;

    @FXML
    private ChoiceBox<String> outputFormatChoiceBox;

    @FXML
    private ChoiceBox<String> inputFormatChoiceBox;

    @FXML
    private TextField inputPathField;

    @FXML
    private TextField outputPathField;

    @FXML
    private Button convertButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button inputPathButton;

    @FXML
    private Button outputPathButton;

    @FXML
    private Label processingLabel;

    @FXML
    private ProgressIndicator progressIndicator;

    private final String[] outputFormats = {"jpg", "jxl", "avif", "webp"};
    private final String[] inputFormats = {"jpg", "jpeg", "JPG", "png", "gif", "bmp", "tiff", "webp", "avif", "heic"};

    String outputFormat = "jpg";
    String inputFormat = "jpg";
    int compressionFactor = 80;
    ThreadGroup threadGroup = new ThreadGroup("ConverterThreads");
    ConverterService converterService = new ConverterService();
    ImageConverterException exception = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        outputFormatChoiceBox.getItems().addAll(outputFormats);
        outputFormatChoiceBox.setValue(outputFormats[0]);
        outputFormatChoiceBox.setOnAction(this::getOutputFormat);
        inputFormatChoiceBox.getItems().addAll(inputFormats);
        inputFormatChoiceBox.setValue(inputFormats[0]);
        inputFormatChoiceBox.setOnAction(this::getInputFormat);

        processingLabel.setText("0");
        compressionSlider.valueProperty().addListener((observableValue, number, t1) -> {
            compressionFactor = (int) compressionSlider.getValue();
            compressionLabel.setText(String.valueOf(compressionFactor));
        });
        progressIndicator.setVisible(false);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            processingLabel.setText(String.valueOf(threadGroup.activeCount()));
            progressIndicator.setVisible(threadGroup.activeCount() > 0);

            if (exception != null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("An error occurred");
                alert.setContentText(exception.getMessage());
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image(Objects.requireNonNull(ConverterApplication.class.getResource("img/icon.png")).toString()));
                alert.show();
                exception = null;
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void getOutputFormat(ActionEvent event) {
        outputFormat = outputFormatChoiceBox.getValue();
    }

    public void getInputFormat(ActionEvent event) {
        inputFormat = inputFormatChoiceBox.getValue();
    }

    public void convert(ActionEvent event) {
        Thread thread = new Thread(threadGroup, () -> {
            try {
                converterService.convert(inputPathField.getText(), outputPathField.getText(), inputFormat, outputFormat, compressionFactor);
            } catch (ImageConverterException e) {
                exception = e;
            }
        });
        thread.start();
    }

    public void cancel(ActionEvent event) {
        converterService.stopAllProcesses();
        threadGroup.interrupt();
    }

    public void chooseInputPath(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose input directory");
        inputPathField.setText(directoryChooser.showDialog(null).getAbsolutePath());
    }

    public void chooseOutputPath(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose output directory");
        outputPathField.setText(directoryChooser.showDialog(null).getAbsolutePath());
    }
}
