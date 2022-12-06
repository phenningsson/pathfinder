module com.example.pathfinder {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;



    opens com.example.pathfinder to javafx.fxml;
    exports com.example.pathfinder;
}