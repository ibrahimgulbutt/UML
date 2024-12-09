package org.example.scdpro2.ui.views.ClassDiagram;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.example.scdpro2.ui.controllers.MainController;
import org.example.scdpro2.business.models.BClassDiagarm.BClassBox;
import org.example.scdpro2.business.services.DiagramService;
import org.example.scdpro2.ui.views.MainView;
import org.example.scdpro2.ui.views.ClassDiagram.RelationshipLine.RelationshipType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * ClassDiagramPane is a UI component that represents a class diagram in the application.
 * It allows for the manipulation of class boxes, zooming, dragging, and managing relationships between them.
 */
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

    /**
     * Constructs a ClassDiagramPane object with the specified parameters.
     * Initializes zoom and drag functionality for the pane.
     *
     * @param mainView The main view of the application.
     * @param controller The main controller managing the application's logic.
     * @param diagramService The service handling diagram-related logic.
     */
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
    /**
     * Enables zoom functionality using the scroll wheel, with control held down.
     * Zooms in or out based on the direction of the scroll.
     */
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
    /**
     * Enables dragging functionality with the Ctrl key pressed.
     * Allows the user to drag the entire diagram pane.
     */
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
    /**
     * Zooms in the class diagram by increasing the zoom factor.
     * Updates the zoom level for the pane.
     */
    private void zoomIn() {
        zoomFactor += 0.1; // Increase zoom factor
        updateZoom(); // Apply zoom to the pane
    }
    /**
     * Zooms out the class diagram by decreasing the zoom factor.
     * Ensures the zoom factor does not go below a minimum threshold.
     */
    private void zoomOut() {
        zoomFactor -= 0.1; // Decrease zoom factor
        if (zoomFactor < 0.1) zoomFactor = 0.1; // Prevent zooming out too much
        updateZoom(); // Apply zoom to the pane
    }
    /**
     * Applies the current zoom factor to the diagram pane.
     */
    private void updateZoom() {
        setScaleX(zoomFactor); // Apply zoom to the X axis
        setScaleY(zoomFactor); // Apply zoom to the Y axis
    }
    /**
     * Creates zoom controls (buttons) for the user to manually zoom in and out of the diagram.
     * The controls are placed at the top-left corner of the pane.
     */
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
    /**
     * Highlights the class box with the given name by applying a specific style.
     *
     * @param className The name of the class box to highlight.
     */
    public void highlightClassBox(String className) {
        ClassBox classBox = getClassBoxByTitle(className);
        if (classBox != null) {
            classBox.setStyle("-fx-border-color: blue; -fx-padding: 5; -fx-background-color: #e0e0e0;");
        }
    }
    /**
     * Removes the highlighting from all class boxes.
     */
    public void unhighlightAllClassBoxes() {
        for (Node node : getChildren()) {
            if (node instanceof ClassBox) {
                ((ClassBox) node).setStyle("-fx-border-color: black; -fx-padding: 5; -fx-background-color: #e0e0e0;");
            }
        }
    }
    /**
     * Handles the click event on a class box. If in relationship mode, it triggers the
     * appropriate logic to create relationships between class boxes.
     *
     * @param clickedClassBox The class box that was clicked.
     * @param event The mouse event triggering the action.
     */
    public void handleClassBoxClick(ClassBox clickedClassBox, MouseEvent event) {
        mainView.handleClassBoxClick(clickedClassBox);
    }
    /**
     * Enables or disables the relationship mode. In this mode, users can select
     * class boxes to create relationships between them.
     *
     * @param enabled Indicates whether relationship mode is enabled.
     */
    public void setRelationshipModeEnabled(boolean enabled) {
        this.relationshipModeEnabled = enabled;
        if (!enabled && selectedClassBox != null) {
            selectedClassBox.setStyle("-fx-border-color: black; -fx-padding: 5; -fx-background-color: #e0e0e0;");
            selectedClassBox = null;
        }
    }
    /**
     * Clears the currently selected class box, if any, by removing the highlight.
     */
    public void clearSelectedClass() {
        if (selectedClassBox != null) {
            selectedClassBox.setStyle("-fx-border-color: black; -fx-padding: 5; -fx-background-color: #e0e0e0;");
            selectedClassBox = null;
        }
    }
    /**
     * Adds a class box to the diagram if it does not already exist in the UI.
     * Registers the class box and updates the UI map.
     *
     * @param classBox The class box to add.
     */
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
    /**
     * Retrieves the class box in the diagram with the specified title.
     *
     * @param className The name of the class box to retrieve.
     * @return The class box with the specified name, or null if not found.
     */
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
    /**
     * Sets the type of relationship for connecting class boxes (e.g., inheritance, association).
     *
     * @param type The relationship type.
     */
    public void setCurrentRelationshipType(RelationshipType type) {
        this.currentRelationshipType = type;
    }
    /**
     * Registers a class box for handling click events. If in relationship mode, it enables
     * the selection of class boxes to create relationships.
     *
     * @param classBox The class box to register.
     */
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
                mainView.handleSelection(classBox);
                System.out.println("Relationship mode is not enabled.");
            }
        });
    }
    // Retrieve all relationship lines connected to a given ClassBox or InterfaceBox
    /**
     * Retrieves all relationship lines connected to a given class box.
     *
     * @param box The class box to check for connected relationship lines.
     * @return A list of relationship lines connected to the specified class box.
     */
    public List<RelationshipLine> getRelationshipLinesConnectedTo(Object box) {
        List<RelationshipLine> connectedLines = new ArrayList<>();
        for (RelationshipLine line : relationships) {
            if (box instanceof ClassBox && line.isConnectedTo((ClassBox) box)) {
                connectedLines.add(line);
            }
        }
        return connectedLines;
    }

    /**
     * Removes a specific relationship line from the UI and the relationships list.
     *
     * @param line The relationship line to remove.
     */
    public void removeRelationshipLine(RelationshipLine line) {
        getChildren().remove(line.getLine()); // Remove the line
        if (line.getEndIndicator() != null) {
            getChildren().remove(line.getEndIndicator()); // Remove arrowheads if any
        }
        relationships.remove(line); // Remove from tracked relationships
        System.out.println("RelationshipLine removed from ClassDiagramPane.");
    }
    /**
     * Clears all diagrams and removes all UI elements from the pane.
     */
    public void clearDiagrams() {
        getChildren().clear();
    }



    // setter getters
    /**
     * Retrieves the list of relationships currently present in the class diagram pane.
     *
     * @return A list of relationship lines.
     */
    public List<RelationshipLine> getRelationships() {
        return relationships;
    }
    /**
     * Retrieves the class box for a specific diagram model.
     *
     * @param diagram The class diagram to search for.
     * @return The class box corresponding to the specified diagram, or null if not found.
     */
    public ClassBox getClassBoxForDiagram(BClassBox diagram) {
        for (Node node : getChildren()) {
            if (node instanceof ClassBox classBox && classBox.getClassDiagram().equals(diagram)) {
                return classBox;
            }
        }
        return null;
    }
    /**
     * Retrieves the main view associated with the diagram pane.
     *
     * @return The main view of the application.
     */
    public MainView getMainView() {
        return mainView;
    }
}
