package org.example.scdpro2.ui.views;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.Cursor;
import javafx.geometry.Pos;
import org.example.scdpro2.business.models.AttributeComponent;
import org.example.scdpro2.business.models.ClassDiagram;
import org.example.scdpro2.business.models.OperationComponent;

public class ClassBox extends VBox {
    private double offsetX, offsetY;
    private final Rectangle resizeHandle = new Rectangle(10, 10); // Resize handle
    private ClassDiagram classDiagram;

    public ClassBox(ClassDiagram classDiagram) {
        this.classDiagram = classDiagram;
        setStyle("-fx-border-color: black; -fx-padding: 5; -fx-background-color: #e0e0e0;");
        setMinWidth(150);
        setMinHeight(100);

        resizeHandle.setStyle("-fx-fill: grey;");
        resizeHandle.setCursor(Cursor.SE_RESIZE);
        resizeHandle.setOnMouseDragged(this::resize);

        HBox nameBox = new HBox();
        TextField classNameField = new TextField(classDiagram.getTitle());
        classNameField.textProperty().addListener((obs, oldName, newName) -> classDiagram.setTitle(newName));
        nameBox.getChildren().addAll(new Label(" "), classNameField);

        VBox attributesBox = new VBox();
        Label attributesLabel = new Label("Attributes:");
        Button addAttributeButton = new Button("+");
        addAttributeButton.setOnAction(e -> addAttribute(attributesBox));
        attributesBox.getChildren().addAll(attributesLabel, addAttributeButton);

        VBox operationsBox = new VBox();
        Label operationsLabel = new Label("Operations:");
        Button addOperationButton = new Button("+");
        addOperationButton.setOnAction(e -> addOperation(operationsBox));
        operationsBox.getChildren().addAll(operationsLabel, addOperationButton);

        getChildren().addAll(nameBox, attributesBox, operationsBox);
        setOnMousePressed(this::handleMousePressed);
        setOnMouseDragged(this::handleMouseDragged);
        setAlignment(Pos.TOP_LEFT);
        getChildren().add(resizeHandle);
    }

    private void addAttribute(VBox attributesBox) {
        HBox attributeBox = new HBox();
        ComboBox<String> visibilityComboBox = new ComboBox<>();
        visibilityComboBox.getItems().addAll("+", "-", "#");
        visibilityComboBox.getSelectionModel().select("+");
        TextField attributeField = new TextField("attribute");
        attributeBox.getChildren().addAll(visibilityComboBox, attributeField);
        attributesBox.getChildren().add(attributeBox);

        // Update model component in the business layer
        AttributeComponent attribute = new AttributeComponent(attributeField.getText(), visibilityComboBox.getValue());
        classDiagram.addAttribute(attribute);
    }

    private void addOperation(VBox operationsBox) {
        HBox operationBox = new HBox();
        ComboBox<String> visibilityComboBox = new ComboBox<>();
        visibilityComboBox.getItems().addAll("+", "-", "#");
        visibilityComboBox.getSelectionModel().select("+");
        TextField operationField = new TextField("operation()");
        operationBox.getChildren().addAll(visibilityComboBox, operationField);
        operationsBox.getChildren().add(operationBox);

        // Update model component in the business layer
        OperationComponent operation = new OperationComponent(operationField.getText(), visibilityComboBox.getValue());
        classDiagram.addOperation(operation);
    }

    private void handleMousePressed(MouseEvent event) {
        offsetX = event.getSceneX() - getLayoutX();
        offsetY = event.getSceneY() - getLayoutY();
    }

    private void handleMouseDragged(MouseEvent event) {
        setLayoutX(event.getSceneX() - offsetX);
        setLayoutY(event.getSceneY() - offsetY);
    }

    private void resize(MouseEvent event) {
        double newWidth = event.getX();
        double newHeight = event.getY();
        if (newWidth > getMinWidth()) {
            setPrefWidth(newWidth);
        }
        if (newHeight > getMinHeight()) {
            setPrefHeight(newHeight);
        }
    }
    public ClassDiagram getClassDiagram() {
        return classDiagram;
    }
}
