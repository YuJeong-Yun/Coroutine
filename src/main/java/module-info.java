module com.example.coroutine {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;


    opens com.example.coroutine to javafx.fxml;
    exports com.example.coroutine;
}