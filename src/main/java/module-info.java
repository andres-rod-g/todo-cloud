module com.andresrodr.todocloud {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.andresrodr.todocloud to javafx.fxml;
    exports com.andresrodr.todocloud;
}