package org.example.scdpro2.ui.views;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.example.scdpro2.business.models.ClassDiagram;
import org.example.scdpro2.ui.controllers.MainController;

public class InterfaceBox extends StackPane {
    private double offsetX, offsetY;
    private Circle backgroundCircle;
    private ClassDiagram interfaceDiagram;
    private Runnable onDelete; // Callback to notify deletion

    public InterfaceBox(ClassDiagram interfaceDiagram, Runnable onDelete) {
        this.interfaceDiagram = interfaceDiagram;
        this.onDelete = onDelete;

        // Create a circular background
        backgroundCircle = new Circle(75); // Set radius
        backgroundCircle.setFill(Color.LIGHTBLUE);
        backgroundCircle.setStroke(Color.BLUE);
        backgroundCircle.setStrokeWidth(2);

        // Interface label and name
        VBox content = new VBox();
        content.setAlignment(Pos.CENTER);
        Label interfaceLabel = new Label("<<Interface>>");
        TextField interfaceNameField = new TextField(interfaceDiagram.getTitle());
        interfaceNameField.setMaxWidth(120); // Constrain width
        interfaceNameField.textProperty().addListener((obs, oldText, newText) -> interfaceDiagram.setTitle(newText.trim()));

        // Buttons
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            if (onDelete != null) {
                onDelete.run();
            }
        });

        content.getChildren().addAll(interfaceLabel, interfaceNameField, deleteButton);

        // Add everything to the circular box
        getChildren().addAll(backgroundCircle, content);

        // Mouse listeners for dragging
        setOnMousePressed(this::handleMousePressed);
        setOnMouseDragged(this::handleMouseDragged);
    }

    private void handleMousePressed(MouseEvent event) {
        offsetX = event.getSceneX() - getLayoutX();
        offsetY = event.getSceneY() - getLayoutY();
    }

    private void handleMouseDragged(MouseEvent event) {
        setLayoutX(event.getSceneX() - offsetX);
        setLayoutY(event.getSceneY() - offsetY);
    }

    public ClassDiagram getInterfaceDiagram() {
        return interfaceDiagram;
    }

    public void enableSelection(ClassDiagramPane diagramPane) {
        setOnMouseClicked(event -> {
            if (diagramPane.isRelationshipModeEnabled()) {
                diagramPane.handleInterfaceBoxClick(this);
            }
        });
    }

}
