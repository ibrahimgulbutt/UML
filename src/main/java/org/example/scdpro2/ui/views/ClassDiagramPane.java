package org.example.scdpro2.ui.views;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.example.scdpro2.business.models.Diagram;
import org.example.scdpro2.business.models.Project;
import org.example.scdpro2.ui.controllers.MainController;
import org.example.scdpro2.business.models.BClassBox;
import org.example.scdpro2.business.services.DiagramService;
import org.example.scdpro2.ui.views.RelationshipLine.RelationshipType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassDiagramPane extends Pane {
    private final MainController controller;
    private final MainView mainView;
    private DiagramService diagramService;
    ;
    private boolean relationshipModeEnabled = false;
    private RelationshipType currentRelationshipType; // Added variable for relationship type
    private final List<RelationshipLine> relationships = new ArrayList<>();

    private ClassBox selectedClassBox;
    private final Map<BClassBox, Node> diagramToUIMap = new HashMap<>();

    public double zoomFactor = 1.0; // Default zoom level
    private double dragStartX;
    private double dragStartY;

    public ClassDiagramPane(MainView mainView, MainController controller, DiagramService diagramService) {
        System.out.println("Class diagaram pane is called ");
        this.mainView = mainView; // Store MainView reference
        this.controller = controller;
        this.diagramService = diagramService;

        // Create zoom buttons
        //createZoomControls();
        enableZoomWithScroll();
        enableDragWithCtrl();
    }

    // grabbing and zooming functions
    private void enableZoomWithScroll() {
        this.setOnScroll(event -> {
            if (event.isControlDown()) { // Check if the Ctrl key is pressed
                double deltaY = event.getDeltaY(); // Get the scroll delta
                if (deltaY > 0) {
                    zoomIn(); // Zoom in when scrolling up
                } else {
                    zoomOut(); // Zoom out when scrolling down
                }
            }
        });
    }

    private void enableDragWithCtrl() {
        this.setOnMousePressed(event -> {
            if (event.isControlDown()) { // Only enable dragging when Ctrl is held
                dragStartX = event.getSceneX();
                dragStartY = event.getSceneY();
            }
        });

        this.setOnMouseDragged(event -> {
            if (event.isControlDown()) { // Ensure Ctrl is held down for dragging
                double deltaX = event.getSceneX() - dragStartX;
                double deltaY = event.getSceneY() - dragStartY;

                // Translate the pane (move it based on drag distance)
                this.setTranslateX(this.getTranslateX() + deltaX);
                this.setTranslateY(this.getTranslateY() + deltaY);

                // Update the starting point for the next drag event
                dragStartX = event.getSceneX();
                dragStartY = event.getSceneY();
            }
        });

        this.setOnMouseReleased(event -> {
            // Optionally reset translation to avoid drift or additional logic when mouse is released
            // this.setTranslateX(0);
            // this.setTranslateY(0);
        });
    }

    private void zoomIn() {
        zoomFactor += 0.1; // Increase zoom factor
        updateZoom(); // Apply zoom to the pane
    }

    private void zoomOut() {
        zoomFactor -= 0.1; // Decrease zoom factor
        if (zoomFactor < 0.1) zoomFactor = 0.1; // Prevent zooming out too much
        updateZoom(); // Apply zoom to the pane
    }

    private void updateZoom() {
        setScaleX(zoomFactor); // Apply zoom to the X axis
        setScaleY(zoomFactor); // Apply zoom to the Y axis
    }

    private void createZoomControls() {
        Button zoomInButton = new Button("+");
        Button zoomOutButton = new Button("-");

        // Styling the buttons (optional)
        zoomInButton.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
        zoomOutButton.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");

        // Add event handlers for zooming in and out
        zoomInButton.setOnAction(event -> zoomIn());
        zoomOutButton.setOnAction(event -> zoomOut());

        // Layout for zoom controls
        VBox zoomControls = new VBox(10, zoomInButton, zoomOutButton);
        zoomControls.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-padding: 10px;");
        zoomControls.setTranslateX(10); // Positioning near the top-left corner
        zoomControls.setTranslateY(10);

        // Add zoom controls directly to this pane
        getChildren().add(zoomControls);
    }


    // UI functions
    public void highlightClassBox(String className) {
        ClassBox classBox = getClassBoxByTitle(className);
        if (classBox != null) {
            classBox.setStyle("-fx-border-color: blue; -fx-padding: 5; -fx-background-color: #e0e0e0;");
        }
    }

    public void unhighlightAllClassBoxes() {
        for (Node node : getChildren()) {
            if (node instanceof ClassBox) {
                ((ClassBox) node).setStyle("-fx-border-color: black; -fx-padding: 5; -fx-background-color: #e0e0e0;");
            }
        }
    }

    public void handleClassBoxClick(ClassBox clickedClassBox, MouseEvent event) {
        mainView.handleClassBoxClick(clickedClassBox);
    }

    public void setRelationshipModeEnabled(boolean enabled) {
        this.relationshipModeEnabled = enabled;
        if (!enabled && selectedClassBox != null) {
            selectedClassBox.setStyle("-fx-border-color: black; -fx-padding: 5; -fx-background-color: #e0e0e0;");
            selectedClassBox = null;
        }
    }

    public void clearSelectedClass() {
        if (selectedClassBox != null) {
            selectedClassBox.setStyle("-fx-border-color: black; -fx-padding: 5; -fx-background-color: #e0e0e0;");
            selectedClassBox = null;
        }
    }

    public void addClassBox(ClassBox classBox) {
        if (!getChildren().contains(classBox)) { // Prevent duplicate addition
            registerClassBox(classBox);
            getChildren().add(classBox);
            diagramToUIMap.put(classBox.getClassDiagram(), classBox);
            // Notify MainView to update classListView
            if (mainView != null) {
                mainView.addClassToList(classBox.getClassName());
            }
        } else {
            System.out.println("Warning: ClassBox already exists in diagramPane.");
        }
    }

    // UI helper functions
    public ClassBox getClassBoxByTitle(String className) {
        for (Node node : getChildren()) {
            if (node instanceof ClassBox) {
                ClassBox classBox = (ClassBox) node;
                if (classBox.getClassName().equals(className)) {
                    return classBox;
                }
            }
        }
        System.out.println("Error: No ClassBox found with the name \"" + className + "\".");
        return null;
    }
    public void setCurrentRelationshipType(RelationshipType type) {
        this.currentRelationshipType = type;
    }

    public void registerClassBox(ClassBox classBox) {
        classBox.setOnMouseClicked(event -> {
            System.out.println("ClassBox clicked: " + classBox.getClassDiagram().getTitle());
            if (relationshipModeEnabled) {
                if (mainView != null) {
                    mainView.handleClassBoxClick(classBox);
                } else {
                    System.out.println("Error: MainView reference is null.");
                }
            } else {
                System.out.println("Relationship mode is not enabled.");
            }
        });
    }
    // Retrieve all relationship lines connected to a given ClassBox or InterfaceBox
    public List<RelationshipLine> getRelationshipLinesConnectedTo(Object box) {
        List<RelationshipLine> connectedLines = new ArrayList<>();
        for (RelationshipLine line : relationships) {
            if (box instanceof ClassBox && line.isConnectedTo((ClassBox) box)) {
                connectedLines.add(line);
            }
        }
        return connectedLines;
    }

    // Remove a specific relationship line from the UI
    public void removeRelationshipLine(RelationshipLine line) {
        getChildren().remove(line.getLine()); // Remove the line
        if (line.getEndIndicator() != null) {
            getChildren().remove(line.getEndIndicator()); // Remove arrowheads if any
        }
        relationships.remove(line); // Remove from tracked relationships
        System.out.println("RelationshipLine removed from ClassDiagramPane.");
    }

    public void clearDiagrams() {
        getChildren().clear();
    }



    // setter getters
    public List<RelationshipLine> getRelationships() {
        return relationships;
    }

    public ClassBox getClassBoxForDiagram(BClassBox diagram) {
        for (Node node : getChildren()) {
            if (node instanceof ClassBox classBox && classBox.getClassDiagram().equals(diagram)) {
                return classBox;
            }
        }
        return null;
    }

    public MainView getMainView() {
        return mainView;
    }
}
