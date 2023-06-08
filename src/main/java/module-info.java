module com.example.discordgui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens com.example.discordgui to javafx.fxml;
    exports com.example.discordgui;

    opens com.example.discordgui.controller to javafx.fxml;
    exports com.example.discordgui.controller;
}