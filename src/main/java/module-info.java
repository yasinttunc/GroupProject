module com.project.coursework2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;

    opens com.project.coursework2 to javafx.fxml;
    opens com.project.coursework2.model to javafx.fxml;
    opens com.project.coursework2.gui to javafx.fxml;
    exports com.project.coursework2;
    exports com.project.coursework2.model;
    exports com.project.coursework2.data;
    exports com.project.coursework2.gui;
}