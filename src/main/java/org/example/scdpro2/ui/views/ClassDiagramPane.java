package org.example.scdpro2.ui.views;

import javafx.scene.layout.Pane;
import org.example.scdpro2.ui.controllers.MainController;
import org.example.scdpro2.business.models.ClassDiagram;
import org.example.scdpro2.business.services.DiagramService;
import org.example.scdpro2.ui.views.RelationshipLine.RelationshipType;

public class ClassDiagramPane extends Pane {
    private final MainController controller;
    private ClassBox selectedClassBox;
    private boolean relationshipModeEnabled = false;
    private DiagramService diagramService;
    private RelationshipType currentRelationshipType; // Added variable for relationship type

    public ClassDiagramPane(MainController controller, DiagramService diagramService) {
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
            if (relationshipModeEnabled) {
                if (selectedClassBox == null) {
                    selectedClassBox = classBox;
                    classBox.setStyle("-fx-border-color: blue;");
                } else {
                    controller.connectClasses(this, selectedClassBox, classBox, currentRelationshipType);
                    selectedClassBox.setStyle("-fx-border-color: black;");
                    selectedClassBox = null;
                }
            }
        });
    }
}
