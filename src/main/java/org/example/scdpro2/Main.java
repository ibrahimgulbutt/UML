package org.example.scdpro2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.scdpro2.ui.views.StartPageView;
import org.kordamp.bootstrapfx.BootstrapFX; // Import BootstrapFX
/**
 * The Main class serves as the entry point for the SCDPro2 application.
 * It extends the JavaFX {@link Application} class to initialize and display the user interface.
 *
 * <p>The application starts with a {@link StartPageView}, styled with BootstrapFX,
 * and configured to run within a primary {@link Stage}.</p>
 *
 * <p>This class follows a three-layered architecture:
 * <ul>
 *   <li>UI Layer: Manages the user interface and views.</li>
 *   <li>Business Layer: Contains the business logic (not implemented here).</li>
 *   <li>Data Layer: Handles data management (not implemented here).</li>
 * </ul>
 * </p>
 *
 * <p>To run the application, execute the main method.</p>
 *
 * @see Application
 * @see StartPageView
 * @see Stage
 * @see Scene
 */
public class Main extends Application {
    /**
     * The main entry point for the JavaFX application.
     * This method is invoked when the application is launched.
     *
     * @param primaryStage The primary stage for the JavaFX application.
     *                     The primary stage serves as the main window where the user interface is displayed.
     */
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
    /**
     * The main method serves as the entry point to the application.
     * This method is responsible for launching the JavaFX application.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}