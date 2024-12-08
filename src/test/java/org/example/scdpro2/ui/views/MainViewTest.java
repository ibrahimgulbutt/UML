package org.example.scdpro2.ui.views;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.scdpro2.business.services.DiagramService;
import org.example.scdpro2.ui.controllers.MainController;
import org.example.scdpro2.ui.views.ClassDiagram.ClassDiagramPane;
import org.example.scdpro2.ui.views.PackageDiagram.PackageDiagramPane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MainViewTest extends ApplicationTest {

    private MainController mockController;
    private DiagramService mockDiagramService;
    private MainView mainView;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        // Mock dependencies
        mockController = mock(MainController.class);
        mockDiagramService = mock(DiagramService.class);
        when(mockController.getDiagramService()).thenReturn(mockDiagramService);

        // Initialize MainView for testing
        Platform.runLater(() -> {
            mainView = new MainView(mockController, "Class Diagram");
            Scene scene = new Scene(mainView, 800, 600);
            stage.setScene(scene);
            stage.show();
        });
    }

    @BeforeEach
    void setup() {
        reset(mockController);
    }

    @Test
    void testClassDiagramPaneInitialization() {
        // Verify the ClassDiagramPane is set in the center for "Class Diagram"
        Platform.runLater(() -> {
            assertNotNull(mainView.classDiagramPane, "ClassDiagramPane should be initialized");
            assertEquals(mainView.classDiagramPane, mainView.getCenter(), "ClassDiagramPane should be in the center");
        });
    }

    @Test
    void testPackageDiagramPaneInitialization() {
        Platform.runLater(() -> {
            // Initialize a MainView with "Package Diagram"
            MainView packageDiagramView = new MainView(mockController, "Package Diagram");
            assertNotNull(packageDiagramView.packageDiagramPane, "PackageDiagramPane should be initialized");
            assertEquals(packageDiagramView.packageDiagramPane, packageDiagramView.getCenter(),
                    "PackageDiagramPane should be in the center");
        });
    }

    @Test
    void testClassListViewInitialization() {
        Platform.runLater(() -> {
            assertNotNull(mainView.classListView, "ClassListView should be initialized");
            assertTrue(mainView.classListView.getItems().isEmpty(), "ClassListView should initially be empty");
        });
    }

    @Test
    void testAddClassButton() {
        Platform.runLater(() -> {
            // Locate the "Add Class" button
            Button addClassButton = lookup(".btn-primary").queryButton();
            assertNotNull(addClassButton, "Add Class button should be present");

            // Simulate a click on the button
            clickOn(addClassButton, MouseButton.PRIMARY);

            // Verify that the controller's `addClassBox` method is called
            verify(mockController, times(1)).addClassBox(mainView.classDiagramPane);
        });
    }

    @Test
    void testAddInterfaceButton() {
        Platform.runLater(() -> {
            // Locate the "Add Interface" button
            Button addInterfaceButton = lookup(".btn-secondary").queryButton();
            assertNotNull(addInterfaceButton, "Add Interface button should be present");

            // Simulate a click on the button
            clickOn(addInterfaceButton, MouseButton.PRIMARY);

            // Verify that the controller's `addInterfaceBox` method is called
            verify(mockController, times(1)).addInterfaceBox(mainView.classDiagramPane);
        });
    }

    @Test
    void testRelationshipModeToggle() {
        Platform.runLater(() -> {
            // Locate the relationship mode toggle button
            ToggleButton relationshipModeToggle = lookup(".btn-outline-warning").queryAs(ToggleButton.class);
            assertNotNull(relationshipModeToggle, "Relationship Mode toggle button should be present");

            // Simulate enabling relationship mode
            clickOn(relationshipModeToggle);
            assertTrue(relationshipModeToggle.isSelected(), "Relationship Mode should be enabled");

            // Verify the controller interaction
            verify(mockController).addClassDiagram();

            // Simulate disabling relationship mode
            clickOn(relationshipModeToggle);
            assertFalse(relationshipModeToggle.isSelected(), "Relationship Mode should be disabled");
        });
    }

    @Test
    void testZoomInButton() {
        Platform.runLater(() -> {
            // Locate the Zoom In button
            Button zoomInButton = lookup(".zoom-button").nth(0).queryButton();
            assertNotNull(zoomInButton, "Zoom In button should be present");

            double initialZoomFactor = mainView.classDiagramPane.zoomFactor;

            // Simulate a click on the Zoom In button
            clickOn(zoomInButton);

            // Verify that the zoom factor increased
            assertEquals(initialZoomFactor + 0.1, mainView.classDiagramPane.zoomFactor, 0.01,
                    "Zoom factor should increase by 0.1");
        });
    }

    @Test
    void testGenerateCodeButton() {
        Platform.runLater(() -> {
            // Locate the Generate Code button
            Button generateCodeButton = lookup(".btn-outline-secondary").queryButton();
            assertNotNull(generateCodeButton, "Generate Code button should be present");

            // Mock the controller's generateCode method
            when(mockController.generateCode()).thenReturn("Generated Code");

            // Simulate a click on the Generate Code button
            clickOn(generateCodeButton);

            // Verify that the controller's generateCode method is called
            verify(mockController, times(1)).generateCode();
        });
    }

    @Test
    void testUpdateRightSideToolbar() {
        Platform.runLater(() -> {
            VBox toolbar = mainView.createRightSideToolbar();
            assertNotNull(toolbar, "Right-side toolbar should be initialized");

            // Simulate selection of an item (e.g., ClassBox or RelationshipLine)
            mainView.updateRightSideToolbar(new Object());

            // Verify toolbar updates appropriately
            assertFalse(toolbar.getChildren().isEmpty(), "Right-side toolbar should update with content");
        });
    }

}

