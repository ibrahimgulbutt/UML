package org.example.scdpro2.ui.views;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
    public double zoomFactor = 1.0; // Default zoom level

    public ClassDiagramPane(MainView mainView, MainController controller, DiagramService diagramService) {
        System.out.println("Class diagaram pane is called ");
        this.mainView = mainView; // Store MainView reference
        this.controller = controller;
        this.diagramService = diagramService;

        // Create zoom buttons
        //createZoomControls();
    }



    private void createZoomControls() {
        Button zoomInButton = new Button("+");
        Button zoomOutButton = new Button("-");

        // Styling the buttons (optional)
        zoomInButton.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
        zoomOutButton.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");

        // Add event handlers for zooming in and out
        zoomInButton.setOnAction(event -> {
            zoomFactor += 0.1;
            setScaleX(zoomFactor);
            setScaleY(zoomFactor);
        });

        zoomOutButton.setOnAction(event -> {
            zoomFactor -= 0.1;
            if (zoomFactor < 0.1) zoomFactor = 0.1; // Prevent zooming out too much
            setScaleX(zoomFactor);
            setScaleY(zoomFactor);
        });

        // Layout for zoom controls
        VBox zoomControls = new VBox(10, zoomInButton, zoomOutButton);
        zoomControls.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-padding: 10px;");
        zoomControls.setTranslateX(10); // Positioning near the top-left corner
        zoomControls.setTranslateY(10);

        // Add zoom controls directly to this pane
        getChildren().add(zoomControls);
    }


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
            // Notify MainView to update classListView
            if (mainView != null) {
                mainView.addClassToList(classBox.getClassName());
            }
        } else {
            System.out.println("Warning: ClassBox already exists in diagramPane.");
        }
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
        getChildren().remove(line.getLine()); // Remove the line
        if (line.getEndIndicator() != null) {
            getChildren().remove(line.getEndIndicator()); // Remove arrowheads if any
        }
        relationships.remove(line); // Remove from tracked relationships
        System.out.println("RelationshipLine removed from ClassDiagramPane.");
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
                                        double sourceWidth, double sourceHeight,
                                        double targetWidth, double targetHeight) {
        sourceNode.layoutXProperty().addListener((observable, oldValue, newValue) -> {
            relationship.updateStartCoordinates(
                    sourceNode.getLayoutX() + sourceWidth / 2,
                    sourceNode.getLayoutY() + sourceHeight / 2);
        });
        sourceNode.layoutYProperty().addListener((observable, oldValue, newValue) -> {
            relationship.updateStartCoordinates(
                    sourceNode.getLayoutX() + sourceWidth / 2,
                    sourceNode.getLayoutY() + sourceHeight / 2);
        });
        targetNode.layoutXProperty().addListener((observable, oldValue, newValue) -> {
            relationship.updateEndCoordinates(
                    targetNode.getLayoutX() + targetWidth / 2,
                    targetNode.getLayoutY() + targetHeight / 2);
        });
        targetNode.layoutYProperty().addListener((observable, oldValue, newValue) -> {
            relationship.updateEndCoordinates(
                    targetNode.getLayoutX() + targetWidth / 2,
                    targetNode.getLayoutY() + targetHeight / 2);
        });
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
