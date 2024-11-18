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

import java.util.List;

public class ClassBox extends VBox {
    private double offsetX, offsetY;
    private final Rectangle resizeHandle = new Rectangle(10, 10); // Resize handle
    private ClassDiagram classDiagram;
    private Button updateClassNameButton; // Declare as a field
    private Runnable onClassNameUpdated; // Callback for class name updates

    public ClassBox(ClassDiagram classDiagram, MainController controller, ClassDiagramPane diagramPane) {
        this.classDiagram = classDiagram;
        setStyle("-fx-border-color: black; -fx-padding: 5; -fx-background-color: #e0e0e0;");
        setMinWidth(150);
        setMinHeight(100);

        resizeHandle.setStyle("-fx-fill: grey;");
        resizeHandle.setCursor(Cursor.SE_RESIZE);
        resizeHandle.setOnMouseDragged(this::resize);

        // Context Menu for Deleting Class
        // Add context menu for deleting the class
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
        this.setOnContextMenuRequested(event -> contextMenu.show(this, event.getScreenX(), event.getScreenY()));

        // Class name section
        HBox nameBox = new HBox();
        TextField classNameField = new TextField(classDiagram.getTitle());
        classNameField.textProperty().addListener((obs, oldName, newName) -> {
            if (!newName.equals(oldName)) {
                updateClassNameButton.setVisible(true); // Show tick button when text changes
            }
        });

        updateClassNameButton = new Button("✔");
        updateClassNameButton.setVisible(false);
        updateClassNameButton.setOnAction(e -> {
            String newClassName = classNameField.getText().trim();
            if (newClassName.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Class name cannot be empty.", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            classDiagram.setTitle(newClassName);
            updateClassNameButton.setVisible(false);
            System.out.println("Class name updated to: " + newClassName);
        });

        classNameField.setOnMouseClicked(event -> updateClassNameButton.setVisible(true));
        nameBox.getChildren().addAll(classNameField, updateClassNameButton);

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


    private void handleDeleteClass() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete this class?",
                ButtonType.YES, ButtonType.NO);
        confirmation.setTitle("Delete Class");
        confirmation.setHeaderText("Confirm Delete");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                // Notify the parent to remove this ClassBox from the UI and business layer
                if (getParent() instanceof ClassDiagramPane diagramPane) {
                    diagramPane.removeClassBox(this);
                }
                System.out.println("Class deleted: " + classDiagram.getTitle());
            }
        });
    }


    private void addAttribute(VBox attributesBox) {
        HBox attributeBox = new HBox();
        ComboBox<String> visibilityComboBox = new ComboBox<>();
        visibilityComboBox.getItems().addAll("+", "-", "#");
        visibilityComboBox.getSelectionModel().select("+");

        TextField attributeNameField = new TextField("attribute");
        attributeNameField.setPromptText("Name");

        ComboBox<String> dataTypeComboBox = new ComboBox<>();
        dataTypeComboBox.setEditable(true);
        dataTypeComboBox.getItems().addAll(
                "int", "String", "boolean", "double", "float", "char", "long", "short"
        );
        dataTypeComboBox.setPromptText("Data Type");

        Button saveButton = new Button("✔");
        Button deleteButton = new Button("❌"); // Add delete button
        deleteButton.setVisible(false); // Hidden until saved

        saveButton.setOnAction(e -> {
            String visibility = visibilityComboBox.getValue();
            String attributeName = attributeNameField.getText().trim();
            String dataType = dataTypeComboBox.getEditor().getText().trim();

            if (attributeName.isEmpty() || dataType.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Attribute name and data type are required.", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            // Update model component in the business layer
            AttributeComponent attribute = new AttributeComponent(attributeName + ": " + dataType, visibility);
            classDiagram.addAttribute(attribute);

            visibilityComboBox.valueProperty().addListener((obs, oldVal, newVal) -> attribute.setVisibility(newVal));

            saveButton.setVisible(false);
            deleteButton.setVisible(true); // Show delete button after saving

            System.out.println("Attribute added: " + attributeName + " : " + dataType);
        });

        deleteButton.setOnAction(e -> {
            attributesBox.getChildren().remove(attributeBox); // Remove from UI
            classDiagram.getAttributes().removeIf(attr -> attr.getName().equals(attributeNameField.getText())); // Remove from business layer
            System.out.println("Attribute deleted: " + attributeNameField.getText());
        });

        attributeBox.getChildren().addAll(visibilityComboBox, attributeNameField, dataTypeComboBox, saveButton, deleteButton);
        attributesBox.getChildren().add(attributeBox);
    }



    private void addOperation(VBox operationsBox) {
        HBox operationBox = new HBox();
        ComboBox<String> visibilityComboBox = new ComboBox<>();
        visibilityComboBox.getItems().addAll("+", "-", "#");
        visibilityComboBox.getSelectionModel().select("+");

        TextField operationField = new TextField("operation");
        operationField.setPromptText("Operation Name");

        ComboBox<String> returnTypeComboBox = new ComboBox<>();
        returnTypeComboBox.setEditable(true);
        returnTypeComboBox.getItems().addAll(
                "void", "int", "String", "boolean", "double", "float", "char", "long", "short"
        );
        returnTypeComboBox.setPromptText("Return Type");

        Button saveButton = new Button("✔");
        Button deleteButton = new Button("❌"); // Add delete button
        deleteButton.setVisible(false);

        saveButton.setOnAction(e -> {
            String visibility = visibilityComboBox.getValue();
            String operationName = operationField.getText().trim();
            String returnType = returnTypeComboBox.getEditor().getText().trim();

            if (operationName.isEmpty() || returnType.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Operation name and return type are required.", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            OperationComponent operation = new OperationComponent(operationName + "(): " + returnType, visibility);
            classDiagram.addOperation(operation);

            visibilityComboBox.valueProperty().addListener((obs, oldVal, newVal) -> operation.setVisibility(newVal));

            saveButton.setVisible(false);
            deleteButton.setVisible(true); // Show delete button after saving

            System.out.println("Operation added: " + operationName + " : " + returnType);
        });

        deleteButton.setOnAction(e -> {
            operationsBox.getChildren().remove(operationBox);
            classDiagram.getOperations().removeIf(op -> op.getName().equals(operationField.getText()));
            System.out.println("Operation deleted: " + operationField.getText());
        });

        operationBox.getChildren().addAll(visibilityComboBox, operationField, returnTypeComboBox, saveButton, deleteButton);
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
