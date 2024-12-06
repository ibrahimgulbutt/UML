package org.example.scdpro2.ui.views;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.Cursor;
import javafx.geometry.Pos;
import org.example.scdpro2.business.models.AttributeComponent;
import org.example.scdpro2.business.models.BClassBox;
import org.example.scdpro2.business.models.OperationComponent;
import org.example.scdpro2.ui.controllers.MainController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassBox extends VBox {
    private final MainController controller;
    public final BClassBox BClassBox;

    private double offsetX, offsetY;
    private final Rectangle resizeHandle = new Rectangle(10, 10); // Resize handle

    private final VBox attributesBox = new VBox(); // Container for attributes
    private final VBox operationsBox = new VBox(); // Container for operations

    private final Button updateClassNameButton = new Button("✔"); // Update class name button

    private final Map<String, List<RelationshipLine>> linesBySide = new HashMap<>();
    private List<RelationshipLine> connectedRelationships = new ArrayList<>();
    private static double k=2;


    public ClassBox(BClassBox BClassBox, MainController controller, ClassDiagramPane diagramPane) {
        this.BClassBox = BClassBox;
        this.controller = controller;

        this.getStylesheets().add(getClass().getResource("/org/example/scdpro2/styles/classbox.css").toExternalForm());

        // Add CSS class for the entire ClassBox
        getStyleClass().add("vbox");

        linesBySide.put("top", new ArrayList<>());
        linesBySide.put("bottom", new ArrayList<>());
        linesBySide.put("left", new ArrayList<>());
        linesBySide.put("right", new ArrayList<>());

        setPadding(new Insets(10)); // Add padding inside the ClassBox
        setSpacing(8); // Set spacing between child elements (class name, attributes, and operations)
        setMinWidth(150);
        setMinHeight(100);

        // Set margin for the ClassBox itself to give some space outside
        setMargin(resizeHandle, new Insets(5)); // Space around the resize handle
        setMargin(attributesBox, new Insets(5)); // Space around the attributes section
        setMargin(operationsBox, new Insets(5)); // Space around the operations section

        // Resize handle styling
        resizeHandle.setStyle("-fx-fill: grey;");
        resizeHandle.setCursor(Cursor.SE_RESIZE);
        resizeHandle.setOnMouseDragged(this::resize);

        // Context Menu for Deleting Class
        ContextMenu contextMenu = createContextMenu(diagramPane);
        this.setOnContextMenuRequested(event -> contextMenu.show(this, event.getScreenX(), event.getScreenY()));

        // Class name section
        HBox nameBox = createNameBox(diagramPane);
        nameBox.getStyleClass().add("hbox");

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

        // Attributes and Operations sections
        attributesBox.getStyleClass().add("vbox");
        operationsBox.getStyleClass().add("vbox");

        // Add the update button
        updateClassNameButton.getStyleClass().add("button");

        // Add components to the ClassBox
        getChildren().addAll(nameBox, attributesBox, operationsBox, resizeHandle);
        setAlignment(Pos.TOP_LEFT);

        // Enable dragging for repositioning
        setOnMousePressed(this::handleMousePressed);
        setOnMouseDragged(this::handleMouseDragged);
    }

    public List<AttributeComponent> getAttributes() {
        return BClassBox.getAttributes();
    }
    public List<OperationComponent> getOperations() {
        return BClassBox.getOperations();
    }

    public void setAttributes(List<AttributeComponent> attributes) {
        BClassBox.setAttributes(attributes);
    }
    public void setOperations(List<OperationComponent> operations) {
        BClassBox.setOperations(operations);
    }


    // creational functions

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
        HBox nameBox = new HBox(10); // Spacing between class name and button
        nameBox.setAlignment(Pos.CENTER); // Center the class name

        TextField classNameField = new TextField(BClassBox.getTitle());
        classNameField.getStyleClass().add("text-field");

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

            // Update the model
            String oldClassName = BClassBox.getTitle();
            BClassBox.setTitle(newClassName);
            controller.getDiagramService().getCurrentProject().getDiagrams().stream()
                    .filter(d -> d.equals(BClassBox))
                    .findFirst()
                    .ifPresent(d -> ((BClassBox) d).setTitle(newClassName));

            // Update the classListView
            int index = diagramPane.getMainView().classListView.getItems().indexOf(oldClassName);
            if (index != -1) {
                diagramPane.getMainView().classListView.getItems().set(index, newClassName);
            }

            updateClassNameButton.setVisible(false);
            System.out.println("Class name updated to: " + newClassName);
        });

        nameBox.getChildren().addAll(classNameField, updateClassNameButton);
        return nameBox;
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
        returnTypeComboBox.getSelectionModel().select(operation.getReturnType());

        // Add listeners for updates
        visibilityComboBox.valueProperty().addListener((obs, oldVal, newVal) -> operation.setVisibility(newVal));
        operationNameField.textProperty().addListener((obs, oldVal, newVal) -> operation.setName(newVal));
        returnTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> operation.setReturnType(newVal));

        Button deleteButton = new Button("❌");
        deleteButton.setOnAction(e -> {
            operationsBox.getChildren().remove(operationBox); // Remove from UI
            BClassBox.getOperations().remove(operation);     // Remove from model
        });

        operationBox.getChildren().addAll(visibilityComboBox, operationNameField, returnTypeComboBox, deleteButton);
        operationsBox.getChildren().add(operationBox);
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
        dataTypeComboBox.getSelectionModel().select(attribute.getDataType());

        // Add listeners for updates
        visibilityComboBox.valueProperty().addListener((obs, oldVal, newVal) -> attribute.setVisibility(newVal));
        attributeNameField.textProperty().addListener((obs, oldVal, newVal) -> attribute.setName(newVal));
        dataTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> attribute.setDataType(newVal));

        Button deleteButton = new Button("❌");
        deleteButton.setOnAction(e -> {
            attributesBox.getChildren().remove(attributeBox); // Remove from UI
            BClassBox.removeAttribute(attribute);            // Remove from model
        });

        attributeBox.getChildren().addAll(visibilityComboBox, attributeNameField, dataTypeComboBox, deleteButton);
        attributesBox.getChildren().add(attributeBox);
    }


    private void addAttribute(VBox attributesBox) {
        AttributeComponent attribute = new AttributeComponent("attribute", "+","int");
        BClassBox.addAttribute(attribute); // Add to the business model
        addAttributeToUI(attribute);
    }

    private void addOperation(VBox operationsBox) {
        OperationComponent operation = new OperationComponent("operation", "+","void");
        BClassBox.addOperation(operation); // Add to the business model
        addOperationToUI(operation);
    }

    // non creational functions

    public void deleteConnectedRelationships(Pane parentPane) {
        for (RelationshipLine relationship : new ArrayList<>(connectedRelationships)) {
            parentPane.getChildren().remove(relationship); // Remove the relationship from the UI
            if (parentPane instanceof ClassDiagramPane) {
                ((ClassDiagramPane) parentPane).removeRelationshipLine(relationship);
            }
        }
        connectedRelationships.clear();
    }

    public void addRelationship(RelationshipLine relationship) {
        connectedRelationships.add(relationship);
    }

    public void removeRelationship(RelationshipLine relationship) {
        connectedRelationships.remove(relationship);
    }

    public List<RelationshipLine> getConnectedRelationships() {
        return connectedRelationships;
    }

    // Load attributes from the business model
    private void loadAttributes() {
        for (AttributeComponent attribute : BClassBox.getAttributes()) {
            addAttributeToUI(attribute);
        }
    }


    // Load operations from the business model
    private void loadOperations() {
        for (OperationComponent operation : BClassBox.getOperations()) {
            addOperationToUI(operation);
        }
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

    public BClassBox getClassDiagram() {
        return BClassBox;
    }

    public String getClassName() {
        return BClassBox.getTitle();
    }

    public String getTitle() {
        return BClassBox.getTitle();
    }

    public Object getAttributesBox() {
        return BClassBox.getAttributes().toString();
    }

    public Object getOperationsBox() {
        return BClassBox.getOperations().toString();
    }

    public Object getDiagram() {
        return BClassBox;
    }
}
