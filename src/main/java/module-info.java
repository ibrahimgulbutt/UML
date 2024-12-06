module org.example.scdpro2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.fasterxml.jackson.databind;
    requires javafx.swing; // Add this line
    requires java.desktop; // Add this line

    opens org.example.scdpro2 to javafx.fxml;
    exports org.example.scdpro2;
}