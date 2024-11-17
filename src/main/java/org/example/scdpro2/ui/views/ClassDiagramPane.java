package org.example.scdpro2.ui.views;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.example.scdpro2.ui.controllers.MainController;
import org.example.scdpro2.business.models.ClassDiagram;
import org.example.scdpro2.business.services.DiagramService;
import org.example.scdpro2.ui.views.RelationshipLine.RelationshipType;

import java.util.ArrayList;
import java.util.List;

public class ClassDiagramPane extends Pane {
    private final MainController controller;
    private ClassBox selectedClassBox;
    private boolean relationshipModeEnabled = false;
    private DiagramService diagramService;
    private RelationshipType currentRelationshipType; // Added variable for relationship type
    private final List<RelationshipLine> relationships = new ArrayList<>();

    private final MainView mainView;

    public ClassDiagramPane(MainView mainView, MainController controller, DiagramService diagramService) {
        this.mainView = mainView; // Store MainView reference
        this.controller = controller;
        this.diagramService = diagramService;
    }

    public void setRelationshipModeEnabled(boolean enabled) {
        this.relationshipModeEnabled = enabled;
        if (!enabled && selectedClassBox != null) {
            selectedClassBox.setStyle("-fx-border-color: black;");
            selectedClassBox = null;
        }
    }

    public void clearSelectedClass() {
        if (selectedClassBox != null) {
            selectedClassBox.setStyle("-fx-border-color: black;");
            selectedClassBox = null;
        }
    }

    // Method to set the relationship type selected in the toolbar
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



    public void addRelationship(ClassBox source, ClassBox target, RelationshipType type) {
        double startX = source.getLayoutX() + source.getWidth() / 2;
        double startY = source.getLayoutY() + source.getHeight() / 2;
        double endX = target.getLayoutX() + target.getWidth() / 2;
        double endY = target.getLayoutY() + target.getHeight() / 2;

        RelationshipLine relationship = new RelationshipLine(
                source.getClassDiagram(), target.getClassDiagram(), // Pass ClassDiagram references
                startX, startY, endX, endY, type
        );
        relationships.add(relationship);

        getChildren().addAll(relationship.getLine());
        if (relationship.getEndIndicator() != null) {
            getChildren().add(relationship.getEndIndicator());
        }

        // Dynamic updates when source or target is moved
        source.layoutXProperty().addListener((obs, oldX, newX) ->
                relationship.updatePosition(newX.doubleValue() + source.getWidth() / 2, source.getLayoutY() + source.getHeight() / 2, target.getLayoutX() + target.getWidth() / 2, target.getLayoutY() + target.getHeight() / 2)
        );

        source.layoutYProperty().addListener((obs, oldY, newY) ->
                relationship.updatePosition(source.getLayoutX() + source.getWidth() / 2, newY.doubleValue() + source.getHeight() / 2, target.getLayoutX() + target.getWidth() / 2, target.getLayoutY() + target.getHeight() / 2)
        );

        target.layoutXProperty().addListener((obs, oldX, newX) ->
                relationship.updatePosition(source.getLayoutX() + source.getWidth() / 2, source.getLayoutY() + source.getHeight() / 2, newX.doubleValue() + target.getWidth() / 2, target.getLayoutY() + target.getHeight() / 2)
        );

        target.layoutYProperty().addListener((obs, oldY, newY) ->
                relationship.updatePosition(source.getLayoutX() + source.getWidth() / 2, source.getLayoutY() + source.getHeight() / 2, target.getLayoutX() + target.getWidth() / 2, newY.doubleValue() + target.getHeight() / 2)
        );
    }


    public void addClassBox(ClassBox classBox) {
        if (!getChildren().contains(classBox)) { // Prevent duplicate addition
            registerClassBox(classBox);
        } else {
            System.out.println("Warning: ClassBox already exists in diagramPane.");
        }
    }
    public void removeClassBox(ClassBox classBox) {
        getChildren().remove(classBox); // Remove from UI
        diagramService.removeDiagram(classBox.getClassDiagram()); // Remove from business layer
        System.out.println("Removed class from diagram pane: " + classBox.getClassDiagram().getTitle());
    }

    // Retrieve all relationship lines connected to a given ClassBox
    public List<RelationshipLine> getRelationshipLinesConnectedTo(ClassBox classBox) {
        List<RelationshipLine> connectedLines = new ArrayList<>();
        for (Node node : relationships) { // Use the relationships list, not all children
            if (node instanceof RelationshipLine line && line.isConnectedTo(classBox)) {
                connectedLines.add(line);
            }
        }
        return connectedLines;
    }


    // Remove a specific relationship line from the UI
    public void removeRelationshipLine(RelationshipLine line) {
        getChildren().remove(line.getLine());
        if (line.getEndIndicator() != null) {
            getChildren().remove(line.getEndIndicator());
        }
        System.out.println("Removed RelationshipLine from UI");
    }





}
