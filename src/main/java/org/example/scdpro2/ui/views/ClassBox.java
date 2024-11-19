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
import org.example.scdpro2.ui.controllers.MainController;

public class ClassBox extends VBox {
    private final MainController controller;
    private final ClassDiagram classDiagram;
    private double offsetX, offsetY;
    private final Rectangle resizeHandle = new Rectangle(10, 10); // Resize handle
    private final VBox attributesBox = new VBox(); // Container for attributes
    private final VBox operationsBox = new VBox(); // Container for operations
    private final Button updateClassNameButton = new Button("✔"); // Update class name button

    public ClassBox(ClassDiagram classDiagram, MainController controller, ClassDiagramPane diagramPane) {
        this.classDiagram = classDiagram;
        this.controller = controller;

        setStyle("-fx-border-color: black; -fx-padding: 5; -fx-background-color: #e0e0e0;");
        setMinWidth(150);
        setMinHeight(100);

        resizeHandle.setStyle("-fx-fill: grey;");
        resizeHandle.setCursor(Cursor.SE_RESIZE);
        resizeHandle.setOnMouseDragged(this::resize);

        // Context Menu for Deleting Class
        ContextMenu contextMenu = createContextMenu(diagramPane);
        this.setOnContextMenuRequested(event -> contextMenu.show(this, event.getScreenX(), event.getScreenY()));

        // Class name section
        HBox nameBox = createNameBox(diagramPane);

        // Attributes section
        Label attributesLabel = new Label("Attributes:");
        Button addAttributeButton = new Button("+");
        addAttributeButton.setOnAction(e -> addAttribute(attributesBox));
        attributesBox.getChildren().addAll(attributesLabel, addAttributeButton);
        loadAttributes();

        // Operations section
        Label operationsLabel = new Label("Operations:");
        Button addOperationButton = new Button("+");
        addOperationButton.setOnAction(e -> addOperation(operationsBox));
        operationsBox.getChildren().addAll(operationsLabel, addOperationButton);
        loadOperations();

        // Add components to the ClassBox
        getChildren().addAll(nameBox, attributesBox, operationsBox, resizeHandle);
        setAlignment(Pos.TOP_LEFT);

        // Enable dragging for repositioning
        setOnMousePressed(this::handleMousePressed);
        setOnMouseDragged(this::handleMouseDragged);
    }

    // Create the context menu for deleting the class
    private ContextMenu createContextMenu(ClassDiagramPane diagramPane) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Delete Class");
        deleteItem.setOnAction(event -> {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to delete this class?", ButtonType.YES, ButtonType.NO);
            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    controller.deleteClassBox(diagramPane, this); // Call deleteClassBox
                }
            });
        });
        contextMenu.getItems().add(deleteItem);
        return contextMenu;
    }

    // Create the name section
    private HBox createNameBox(ClassDiagramPane diagramPane) {
        HBox nameBox = new HBox();
        TextField classNameField = new TextField(classDiagram.getTitle());
        classNameField.setPromptText("Class Name");
        classNameField.textProperty().addListener((obs, oldName, newName) -> {
            if (!newName.equals(oldName)) {
                updateClassNameButton.setVisible(true);
            }
        });

        updateClassNameButton.setVisible(false);
        updateClassNameButton.setOnAction(e -> {
            String newClassName = classNameField.getText().trim();
            if (newClassName.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Class name cannot be empty.", ButtonType.OK);
                alert.showAndWait();
                return;
            }
            classDiagram.setTitle(newClassName);
            controller.getDiagramService().getCurrentProject().getDiagrams().stream()
                    .filter(d -> d.equals(classDiagram))
                    .findFirst()
                    .ifPresent(d -> ((ClassDiagram) d).setTitle(newClassName));

            updateClassNameButton.setVisible(false);
            diagramPane.getMainView().updateClassListView(); // Update the class list in the UI
            System.out.println("Class name updated to: " + newClassName);
        });

        nameBox.getChildren().addAll(classNameField, updateClassNameButton);
        return nameBox;
    }

    // Load attributes from the business model
    private void loadAttributes() {
        for (AttributeComponent attribute : classDiagram.getAttributes()) {
            addAttributeToUI(attribute);
        }
    }

    private void addAttribute(VBox attributesBox) {
        AttributeComponent attribute = new AttributeComponent("attribute", "+");
        classDiagram.addAttribute(attribute); // Add to the business model
        addAttributeToUI(attribute);
    }

    private void addAttributeToUI(AttributeComponent attribute) {
        HBox attributeBox = new HBox();
        ComboBox<String> visibilityComboBox = new ComboBox<>();
        visibilityComboBox.getItems().addAll("+", "-", "#");
        visibilityComboBox.getSelectionModel().select(attribute.getVisibility());

        TextField attributeNameField = new TextField(attribute.getName());
        ComboBox<String> dataTypeComboBox = new ComboBox<>();
        dataTypeComboBox.setEditable(true);
        dataTypeComboBox.getItems().addAll("int", "String", "boolean", "double", "float", "char", "long", "short");
        dataTypeComboBox.setPromptText("Data Type");

        visibilityComboBox.valueProperty().addListener((obs, oldVal, newVal) -> attribute.setVisibility(newVal));
        attributeNameField.textProperty().addListener((obs, oldVal, newVal) -> attribute.setName(newVal));
        dataTypeComboBox.getEditor().textProperty().addListener((obs, oldVal, newVal) -> attribute.setName(attributeNameField.getText() + ": " + newVal));

        Button deleteButton = new Button("❌");
        deleteButton.setOnAction(e -> {
            attributesBox.getChildren().remove(attributeBox); // Remove from UI
            classDiagram.getAttributes().remove(attribute);   // Remove from the model
        });

        attributeBox.getChildren().addAll(visibilityComboBox, attributeNameField, dataTypeComboBox, deleteButton);
        attributesBox.getChildren().add(attributeBox);
    }

    // Load operations from the business model
    private void loadOperations() {
        for (OperationComponent operation : classDiagram.getOperations()) {
            addOperationToUI(operation);
        }
    }

    private void addOperation(VBox operationsBox) {
        OperationComponent operation = new OperationComponent("operation", "+");
        classDiagram.addOperation(operation); // Add to the business model
        addOperationToUI(operation);
    }

    private void addOperationToUI(OperationComponent operation) {
        HBox operationBox = new HBox();
        ComboBox<String> visibilityComboBox = new ComboBox<>();
        visibilityComboBox.getItems().addAll("+", "-", "#");
        visibilityComboBox.getSelectionModel().select(operation.getVisibility());

        TextField operationNameField = new TextField(operation.getName());
        ComboBox<String> returnTypeComboBox = new ComboBox<>();
        returnTypeComboBox.setEditable(true);
        returnTypeComboBox.getItems().addAll("void", "int", "String", "boolean", "double", "float", "char", "long", "short");
        returnTypeComboBox.setPromptText("Return Type");

        visibilityComboBox.valueProperty().addListener((obs, oldVal, newVal) -> operation.setVisibility(newVal));
        operationNameField.textProperty().addListener((obs, oldVal, newVal) -> operation.setName(newVal));
        returnTypeComboBox.getEditor().textProperty().addListener((obs, oldVal, newVal) -> operation.setName(operationNameField.getText() + "(): " + newVal));

        Button deleteButton = new Button("❌");
        deleteButton.setOnAction(e -> {
            operationsBox.getChildren().remove(operationBox); // Remove from UI
            classDiagram.getOperations().remove(operation);   // Remove from the model
        });

        operationBox.getChildren().addAll(visibilityComboBox, operationNameField, returnTypeComboBox, deleteButton);
        operationsBox.getChildren().add(operationBox);
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
