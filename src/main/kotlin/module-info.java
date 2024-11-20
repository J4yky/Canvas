module org.linepainter.canvas {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;


    opens org.linepainter.canvas to javafx.fxml;
    exports org.linepainter.canvas;
}