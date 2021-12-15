
//module com.example.ex1 {
//    requires javafx.controls;
//    requires javafx.fxml;
//    requires org.controlsfx.controls;
//    requires com.dlsc.formsfx;
//    requires org.kordamp.bootstrapfx.core;
//    requires socialnetwork;
//    requires java.sql;
//    opens com.example.ex1 to javafx.fxml;
//    exports com.example.ex1;


module com.example.ex2 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    requires socialnetwork;
    requires java.sql;

    opens com.example.ex2 to javafx.fxml;
    exports com.example.ex2;

    opens com.example.ex2.utils to javafx.base;
    exports com.example.ex2.utils;
}