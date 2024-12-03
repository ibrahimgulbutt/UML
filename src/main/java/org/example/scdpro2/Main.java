package org.example.scdpro2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.scdpro2.ui.views.StartPageView;
import org.kordamp.bootstrapfx.BootstrapFX; // Import BootstrapFX

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Create the StartPageView
        StartPageView startPageView = new StartPageView(primaryStage);

        // Create the scene and apply BootstrapFX styles
        Scene scene = new Scene(startPageView, 800, 600);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet()); // Add BootstrapFX stylesheet

        // Configure the primary stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("SCDPro2 - Software Construction & Design");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}