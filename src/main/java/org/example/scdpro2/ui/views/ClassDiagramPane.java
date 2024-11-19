package org.example.scdpro2.ui.views;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.example.scdpro2.business.models.Diagram;
import org.example.scdpro2.business.models.Project;
import org.example.scdpro2.ui.controllers.MainController;
import org.example.scdpro2.business.models.ClassDiagram;
import org.example.scdpro2.business.services.DiagramService;
import org.example.scdpro2.ui.views.RelationshipLine.RelationshipType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassDiagramPane extends Pane {
    private final MainController controller;
    private ClassBox selectedClassBox;
    private boolean relationshipModeEnabled = false;
    private DiagramService diagramService;
    private RelationshipType currentRelationshipType; // Added variable for relationship type
    private final List<RelationshipLine> relationships = new ArrayList<>();
    private final Map<ClassDiagram, Node> diagramToUIMap = new HashMap<>();

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

    public void addClassBox(ClassBox classBox) {
        if (!getChildren().contains(classBox)) { // Prevent duplicate addition
            registerClassBox(classBox);
            getChildren().add(classBox);
            diagramToUIMap.put(classBox.getClassDiagram(), classBox);
        } else {
            System.out.println("Warning: ClassBox already exists in diagramPane.");
        }
    }

    public void removeClassBox(ClassBox classBox) {
        List<RelationshipLine> linesToRemove = getRelationshipLinesConnectedTo(classBox);
        for (RelationshipLine line : linesToRemove) {
            removeRelationshipLine(line);
        }
        getChildren().remove(classBox); // Remove from UI
        diagramService.removeDiagram(classBox.getClassDiagram()); // Remove from business layer
        System.out.println("Removed class from diagram pane: " + classBox.getClassDiagram().getTitle());
    }

    // Retrieve all relationship lines connected to a given ClassBox or InterfaceBox
    public List<RelationshipLine> getRelationshipLinesConnectedTo(Object box) {
        List<RelationshipLine> connectedLines = new ArrayList<>();
        for (RelationshipLine line : relationships) {
            if (box instanceof ClassBox && line.isConnectedTo((ClassBox) box)) {
                connectedLines.add(line);
            } else if (box instanceof InterfaceBox && line.isConnectedTo((InterfaceBox) box)) {
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
        relationships.remove(line);
        System.out.println("Removed RelationshipLine from UI");
    }


    public void removeInterfaceBox(InterfaceBox interfaceBox) {
        List<RelationshipLine> linesToRemove = getRelationshipLinesConnectedTo(interfaceBox);
        for (RelationshipLine line : linesToRemove) {
            removeRelationshipLine(line);
        }
        getChildren().remove(interfaceBox);
        diagramService.removeDiagram(interfaceBox.getInterfaceDiagram());
        System.out.println("Removed interface: " + interfaceBox.getInterfaceDiagram().getTitle());
    }

    public void addRelationship(ClassBox source, ClassBox target, RelationshipLine.RelationshipType type) {
        System.out.println("Creating relationship: Source = " + source.getClassDiagram().getTitle() +
                ", Target = " + target.getClassDiagram().getTitle() + ", Type = " + type);

        addRelationshipCommon(source.getLayoutX(), source.getLayoutY(), source.getWidth(), source.getHeight(),
                target.getLayoutX(), target.getLayoutY(), target.getWidth(), target.getHeight(),
                source.getClassDiagram(), target.getClassDiagram(), type);
    }


    public void addRelationship(ClassBox source, InterfaceBox target, RelationshipType type) {
        addRelationshipCommon(source.getLayoutX(), source.getLayoutY(), source.getWidth(), source.getHeight(),
                target.getLayoutX(), target.getLayoutY(), target.getWidth(), target.getHeight(),
                source.getClassDiagram(), target.getInterfaceDiagram(), type);
    }

    public void addRelationship(InterfaceBox source, ClassBox target, RelationshipType type) {
        addRelationshipCommon(source.getLayoutX(), source.getLayoutY(), source.getWidth(), source.getHeight(),
                target.getLayoutX(), target.getLayoutY(), target.getWidth(), target.getHeight(),
                source.getInterfaceDiagram(), target.getClassDiagram(), type);
    }
    public void addRelationship(InterfaceBox source, InterfaceBox target, RelationshipType type) {
        addRelationshipCommon(source.getLayoutX(), source.getLayoutY(), source.getWidth(), source.getHeight(),
                target.getLayoutX(), target.getLayoutY(), target.getWidth(), target.getHeight(),
                source.getInterfaceDiagram(), target.getInterfaceDiagram(), type);
    }

    // Common logic for creating a relationship
    private void addRelationshipCommon(double startX, double startY, double sourceWidth, double sourceHeight,
                                       double endX, double endY, double targetWidth, double targetHeight,
                                       ClassDiagram sourceDiagram, ClassDiagram targetDiagram,
                                       RelationshipType type) {
        Node sourceNode = diagramToUIMap.get(sourceDiagram);
        Node targetNode = diagramToUIMap.get(targetDiagram);

        if (sourceNode == null || targetNode == null) {
            System.out.println("Error: Unable to find UI component for source or target diagram.");
            return;
        }

        double lineStartX = startX + sourceWidth / 2;
        double lineStartY = startY + sourceHeight / 2;
        double lineEndX = endX + targetWidth / 2;
        double lineEndY = endY + targetHeight / 2;

        RelationshipLine relationship = new RelationshipLine(sourceDiagram, targetDiagram, type, lineStartX, lineStartY, lineEndX, lineEndY);
        relationship.enableSelectionAndDeletion(this);
        relationships.add(relationship);
        getChildren().addAll(relationship.getLine());
        if (relationship.getEndIndicator() != null) {
            getChildren().add(relationship.getEndIndicator());
        }

        // Attach listeners for dynamic updates
        attachDynamicListeners(sourceNode, targetNode, relationship, sourceWidth, sourceHeight, targetWidth, targetHeight);
    }

    private void attachDynamicListeners(Node sourceNode, Node targetNode, RelationshipLine relationship,
                                        double sourceWidth, double sourceHeight, double targetWidth, double targetHeight) {
        if (sourceNode instanceof ClassBox || sourceNode instanceof InterfaceBox) {
            sourceNode.layoutXProperty().addListener((obs, oldX, newX) ->
                    relationship.updatePosition(newX.doubleValue() + sourceWidth / 2, sourceNode.getLayoutY() + sourceHeight / 2,
                            relationship.getLine().getEndX(), relationship.getLine().getEndY()));
            sourceNode.layoutYProperty().addListener((obs, oldY, newY) ->
                    relationship.updatePosition(sourceNode.getLayoutX() + sourceWidth / 2, newY.doubleValue() + sourceHeight / 2,
                            relationship.getLine().getEndX(), relationship.getLine().getEndY()));
        }

        if (targetNode instanceof ClassBox || targetNode instanceof InterfaceBox) {
            targetNode.layoutXProperty().addListener((obs, oldX, newX) ->
                    relationship.updatePosition(relationship.getLine().getStartX(), relationship.getLine().getStartY(),
                            newX.doubleValue() + targetWidth / 2, targetNode.getLayoutY() + targetHeight / 2));
            targetNode.layoutYProperty().addListener((obs, oldY, newY) ->
                    relationship.updatePosition(relationship.getLine().getStartX(), relationship.getLine().getStartY(),
                            targetNode.getLayoutX() + targetWidth / 2, newY.doubleValue() + targetHeight / 2));
        }
    }



    public void handleInterfaceBoxClick(InterfaceBox interfaceBox) {
        if (!relationshipModeEnabled || currentRelationshipType == null) {
            System.out.println("Relationship mode not active or no type selected.");
            return;
        }

        if (selectedClassBox == null && interfaceBox != null) {
            interfaceBox.setStyle("-fx-border-color: blue;");
            selectedClassBox = null; // Clear any selected class box
            System.out.println("Source interface selected: " + interfaceBox.getInterfaceDiagram().getTitle());
        } else if (selectedClassBox != null) {
            // Handle relationship creation between ClassBox and InterfaceBox
            createRelationship(selectedClassBox, interfaceBox);
            selectedClassBox.setStyle("-fx-border-color: black;");
            selectedClassBox = null;
            System.out.println("Target interface selected: " + interfaceBox.getInterfaceDiagram().getTitle());
        }
    }

    public void createRelationship(ClassBox source, InterfaceBox target) {
        addRelationship(source, target, currentRelationshipType);
    }

    public void createRelationship(InterfaceBox source, ClassBox target) {
        addRelationship(source, target, currentRelationshipType);
    }

    public boolean isRelationshipModeEnabled() {
        return relationshipModeEnabled;
    }
    public void registerInterfaceBox(InterfaceBox interfaceBox) {
        interfaceBox.setOnMouseClicked(event -> {
            System.out.println("InterfaceBox clicked: " + interfaceBox.getInterfaceDiagram().getTitle());
            if (relationshipModeEnabled) {
                if (mainView != null) {
                    mainView.handleInterfaceBoxClick(interfaceBox);
                } else {
                    System.out.println("Error: MainView reference is null.");
                }
            } else {
                System.out.println("Relationship mode is not enabled.");
            }
        });
    }


    public void addInterfaceBox(InterfaceBox interfaceBox) {
        if (!getChildren().contains(interfaceBox)) { // Prevent duplicate addition
            registerInterfaceBox(interfaceBox);
            getChildren().add(interfaceBox);
            diagramToUIMap.put(interfaceBox.getInterfaceDiagram(), interfaceBox); // Map ClassDiagram to InterfaceBox
        } else {
            System.out.println("Warning: InterfaceBox already exists in diagramPane.");
        }
    }

    public void clearDiagrams() {
        getChildren().clear();
    }

    public void loadDiagramsFromProject(Project project) {
        clearDiagrams();
        for (Diagram diagram : project.getDiagrams()) {
            if (diagram instanceof ClassDiagram classDiagram) {
                ClassBox classBox = new ClassBox(classDiagram, controller, this);
                addClassBox(classBox);
                getChildren().add(classBox);
            }
        }
    }

    public ClassBox getClassBoxForDiagram(ClassDiagram diagram) {
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
