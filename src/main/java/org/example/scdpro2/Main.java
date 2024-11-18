package org.example.scdpro2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.scdpro2.ui.views.MainView;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        MainView mainView = new MainView();
        Scene scene = new Scene(mainView, 1200, 800);
        primaryStage.setTitle("UML Editor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
