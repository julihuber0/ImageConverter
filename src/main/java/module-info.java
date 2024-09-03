module com.julian.imageconverter.view {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.julian.imageconverter.view to javafx.fxml;
    exports com.julian.imageconverter.view;
}