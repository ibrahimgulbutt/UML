package org.example.scdpro2.ui.views;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.example.scdpro2.ui.controllers.MainController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class StartPageViewTest extends ApplicationTest {

    private StartPageView startPageView;
    private MainController mockMainController;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        // Mock MainController
        mockMainController = mock(MainController.class);

        // Create StartPageView
        Platform.runLater(() -> {
            startPageView = new StartPageView(primaryStage);
            Scene scene = new Scene(startPageView, 800, 600);
            stage.setScene(scene);
            stage.show();
        });
    }

    @BeforeEach
    void setup() {
        reset(mockMainController);
    }

    @Test
    void testUIInitialization() {
        assertNotNull(lookup(".container").query(), "Main container should be initialized");
    }

    @Test
    void testOpenMainView() {
        // Arrange
        String projectType = "Class Diagram";

        // Act & Assert
        Platform.runLater(() -> {
            startPageView.openMainView(projectType);
            assertNotNull(primaryStage.getScene(), "Stage scene should not be null");
        });
    }

}