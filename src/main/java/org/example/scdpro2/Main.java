package org.example.scdpro2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.scdpro2.business.services.DiagramService;
import org.example.scdpro2.ui.controllers.MainController;
import org.example.scdpro2.ui.views.StartPageView;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {

        StartPageView startPageView = new StartPageView(primaryStage); // Pass controller here
        primaryStage.setScene(new Scene(startPageView, 800, 600));
        primaryStage.setTitle("SCDPro2 - Software Construction & Design");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
