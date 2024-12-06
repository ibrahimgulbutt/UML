package org.example.scdpro2.ui.views;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.example.scdpro2.business.models.BClassBox;
import org.example.scdpro2.business.models.OperationComponent;
import org.example.scdpro2.ui.controllers.MainController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterfaceBox extends StackPane {
    private final Circle backgroundCircle; // Circular background
    public final BClassBox interfaceDiagram; // Interface business model
    private final VBox operationsBox = new VBox(); // Container for operations
    private double offsetX, offsetY; // Dragging offsets
    private final MainController controller;
    private final Button addOperationButton = new Button("+"); // Add operation button
    private List<OperationComponent> operations = new ArrayList<>();
    private final Map<String, List<RelationshipLine>> linesBySide = new HashMap<>();
    private List<RelationshipLine> connectedRelationships = new ArrayList<>();

    public InterfaceBox(BClassBox interfaceDiagram, MainController controller, ClassDiagramPane pane) {
        this.interfaceDiagram = interfaceDiagram;
        this.controller = controller;

        // Create a circular background for the interface
        backgroundCircle = new Circle(75); // Radius of 75
        backgroundCircle.setFill(Color.LIGHTYELLOW); // Light yellow background
        backgroundCircle.setStroke(Color.BLACK); // Black border
        backgroundCircle.setStrokeWidth(2);

        // VBox for interface label, name, and operations
        VBox content = new VBox(5);
        content.setAlignment(Pos.CENTER);

        // Interface label
        Label interfaceLabel = new Label("<<Interface>>");

        // Interface name
        TextField interfaceNameField = new TextField(interfaceDiagram.getTitle());
        interfaceNameField.setMaxWidth(120); // Limit width
        interfaceNameField.setPromptText("Interface Name");
        interfaceNameField.textProperty().addListener((obs, oldText, newText) -> {
            interfaceDiagram.setTitle(newText.trim());
        });

        // Load operations from the business model
        loadOperations();

        // Operations section
        Label operationsLabel = new Label("Operations:");
        addOperationButton.setOnAction(e -> addOperation());

        operationsBox.getChildren().addAll(operationsLabel, addOperationButton);

        // Context menu for delete action
        ContextMenu contextMenu = createContextMenu(pane);

        setOnContextMenuRequested(event -> contextMenu.show(this, event.getScreenX(), event.getScreenY()));

        // Add all components to the content VBox
        content.getChildren().addAll(interfaceLabel, interfaceNameField, operationsBox);

        // Add to the root StackPane
        getChildren().addAll(backgroundCircle, content);

        // Enable dragging
        setOnMousePressed(this::handleMousePressed);
        setOnMouseDragged(this::handleMouseDragged);
    }

    public void deleteConnectedRelationships(Pane parentPane) {
        for (RelationshipLine relationship : new ArrayList<>(connectedRelationships)) {
            parentPane.getChildren().remove(relationship); // Remove the relationship from the UI
            if (parentPane instanceof ClassDiagramPane) {
                ((ClassDiagramPane) parentPane).removeRelationshipLine(relationship);
            }
        }
        connectedRelationships.clear();
    }

    // Creates the context menu for deleting the interface
    private ContextMenu createContextMenu(ClassDiagramPane pane) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Delete Interface");
        deleteItem.setOnAction(event -> {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to delete this interface?", ButtonType.YES, ButtonType.NO);
            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    controller.deleteInterfaceBox(pane, this); // Delete the interface
                }
            });
        });
        contextMenu.getItems().add(deleteItem);
        return contextMenu;
    }

    // Load operations from the model into the UI
    private void loadOperations() {
        for (OperationComponent operation : interfaceDiagram.getOperations()) {
            addOperationToUI(operation);
        }
    }

    // Add a new operation
    private void addOperation() {
        OperationComponent operation = new OperationComponent("operation", "+",
                "void");
        interfaceDiagram.addOperation(operation); // Add to the business model
        addOperationToUI(operation);
    }

    // Add an operation to the UI
    private void addOperationToUI(OperationComponent operation) {
        HBox operationBox = new HBox(5);
        ComboBox<String> visibilityComboBox = new ComboBox<>();
        visibilityComboBox.getItems().addAll("+", "-", "#");
        visibilityComboBox.getSelectionModel().select(operation.getVisibility());

        TextField operationNameField = new TextField(operation.getName());
        ComboBox<String> returnTypeComboBox = new ComboBox<>();
        returnTypeComboBox.setEditable(true);
        returnTypeComboBox.getItems().addAll("void", "int", "String", "boolean", "double", "float", "char", "long", "short");
        returnTypeComboBox.setPromptText("Return Type");

        // Bind changes to the operation model
        visibilityComboBox.valueProperty().addListener((obs, oldVal, newVal) -> operation.setVisibility(newVal));
        operationNameField.textProperty().addListener((obs, oldVal, newVal) -> operation.setName(newVal));
        returnTypeComboBox.getEditor().textProperty().addListener((obs, oldVal, newVal) ->
                operation.setName(operationNameField.getText() + "(): " + newVal)
        );

        // Delete button for operation
        Button deleteButton = new Button("❌");
        deleteButton.setOnAction(e -> {
            operationsBox.getChildren().remove(operationBox); // Remove from UI
            interfaceDiagram.getOperations().remove(operation); // Remove from model
        });

        // Add all components to the operationBox
        operationBox.getChildren().addAll(visibilityComboBox, operationNameField, returnTypeComboBox, deleteButton);

        // Add to operationsBox
        operationsBox.getChildren().add(operationBox);
    }

    // Handles mouse press for dragging
    private void handleMousePressed(MouseEvent event) {
        offsetX = event.getSceneX() - getLayoutX();
        offsetY = event.getSceneY() - getLayoutY();
    }

    // Handles mouse drag for repositioning
    private void handleMouseDragged(MouseEvent event) {
        setLayoutX(event.getSceneX() - offsetX);
        setLayoutY(event.getSceneY() - offsetY);
    }

    // Get the associated business model
    public BClassBox getInterfaceDiagram() {
        return interfaceDiagram;
    }

    public String getClassName() {
        return interfaceDiagram.getTitle();
    }
    public void addRelationship(RelationshipLine relationship) {
        connectedRelationships.add(relationship);
    }

    public void setOperations(List<OperationComponent> operations) {
        this.operations=operations;
    }

    public BClassBox getClassDiagram() {
        return interfaceDiagram;
    }

    public Object getOperationsBox() {
        return interfaceDiagram.getOperations().toString();
    }

    public List<OperationComponent> getOperations() {
        return interfaceDiagram.getOperations();
    }
}
