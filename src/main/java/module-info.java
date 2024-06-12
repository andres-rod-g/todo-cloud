module com.andresrodr.todocloud {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;
    requires java.desktop;
    requires org.apache.pdfbox;
    requires commons.logging;

    opens com.andresrodr.todocloud to javafx.fxml;
    exports com.andresrodr.todocloud;
}