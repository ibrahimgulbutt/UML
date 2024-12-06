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

public class ClassBox extends BorderPane {
    private final MainController controller;
    public final BClassBox BClassBox;

    private final VBox attributesBox = new VBox(); // Container for attributes
    private final VBox operationsBox = new VBox(); // Container for operations

    private final Button updateClassNameButton = new Button("✔"); // Update class name button

    private final Map<String, List<RelationshipLine>> linesBySide = new HashMap<>();
    private List<RelationshipLine> connectedRelationships = new ArrayList<>();
    private static double k=2;

    private double offsetX, offsetY; // For dragging
    private double initialWidth, initialHeight; // For resizing
    private double initialMouseX, initialMouseY; // For resizing
    private boolean isResizing = false; // Tracks resizing state
    private String resizeDirection = ""; // Tracks the direction of resizing

    public ClassBox(BClassBox BClassBox, MainController controller, ClassDiagramPane diagramPane) {
        this.BClassBox = BClassBox;
        this.controller = controller;

        this.getStylesheets().add(getClass().getResource("/org/example/scdpro2/styles/classbox.css").toExternalForm());

        // Add CSS class for the entire ClassBox
        getStyleClass().add("classbox");

        linesBySide.put("top", new ArrayList<>());
        linesBySide.put("bottom", new ArrayList<>());
        linesBySide.put("left", new ArrayList<>());
        linesBySide.put("right", new ArrayList<>());

        setPadding(new Insets(10)); // Padding for the entire BorderPane
        BorderPane.setMargin(attributesBox, new Insets(5)); // Margin around attributesBox

        setMinWidth(150);
        setMinHeight(100);

        // Set margin for the ClassBox itself to give some space outside
        setMargin(attributesBox, new Insets(5)); // Space around the attributes section
        setMargin(operationsBox, new Insets(5)); // Space around the operations section

        // Context Menu for Deleting Class
        ContextMenu contextMenu = createContextMenu(diagramPane);
        this.setOnContextMenuRequested(event -> contextMenu.show(this, event.getScreenX(), event.getScreenY()));

        // Class name section
        HBox nameBox = createNameBox(diagramPane);

        // Attributes section
//        Label attributesLabel = new Label("Attributes:");
        Button addAttributeButton = new Button("+");
        addAttributeButton.setOnAction(e -> addAttribute(attributesBox));
        attributesBox.getChildren().addAll( addAttributeButton);
        loadAttributes();

        // Operations section
//        Label operationsLabel = new Label("Operations:");
        Button addOperationButton = new Button("+");
        addOperationButton.setOnAction(e -> addOperation(operationsBox));
        operationsBox.getChildren().addAll( addOperationButton);
        loadOperations();

        // Attributes and Operations sections
//        attributesBox.getStyleClass().add("vbox");
//        operationsBox.getStyleClass().add("vbox");

        // Add the update button
        updateClassNameButton.getStyleClass().add("button");

        // Add CSS classes for buttons and text fields
        addAttributeButton.getStyleClass().add("classbox-add-button");
        addOperationButton.getStyleClass().add("classbox-add-button");
        updateClassNameButton.getStyleClass().add("classbox-button");

        // Add components to BorderPane regions
        setTop(nameBox);
        setCenter(attributesBox);
        setBottom(operationsBox);

        // Enable dragging for repositioning
        setOnMousePressed(this::handleMousePressed);
        setOnMouseDragged(this::handleMouseDragged);

        setOnMousePressed(this::handleMousePressed);
        setOnMouseDragged(this::handleMouseDragged);
        setOnMouseReleased(this::handleMouseReleased);

        // Configure cursor changes for resizing regions
        setOnMouseMoved(this::updateCursor);
    }

    // UI functions
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

    private HBox createNameBox(ClassDiagramPane diagramPane) {
        HBox nameBox = new HBox(10); // Spacing between class name and button
        nameBox.setAlignment(Pos.CENTER); // Center the class name

        TextField classNameField = new TextField(BClassBox.getTitle());
        classNameField.getStyleClass().add("classbox-input");

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

        visibilityComboBox.getStyleClass().add("classbox-combobox");
        operationNameField.getStyleClass().add("classbox-input");
        returnTypeComboBox.getStyleClass().add("classbox-combobox");
        deleteButton.getStyleClass().add("classbox-delete-button");

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

        visibilityComboBox.getStyleClass().add("classbox-combobox");
        attributeNameField.getStyleClass().add("classbox-input");
        dataTypeComboBox.getStyleClass().add("classbox-combobox");
        deleteButton.getStyleClass().add("classbox-delete-button");


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

    // grabbing and resizing functions
    private void handleMousePressed(MouseEvent event) {
        if (isOnEdge(event)) {
            // Start resizing
            isResizing = true;
            initialWidth = getPrefWidth();
            initialHeight = getPrefHeight();
            initialMouseX = event.getSceneX();
            initialMouseY = event.getSceneY();
        } else {
            // Start dragging
            isResizing = false;
            offsetX = event.getSceneX() - getLayoutX();
            offsetY = event.getSceneY() - getLayoutY();
        }
    }

    private void handleMouseDragged(MouseEvent event) {
        if (isResizing) {
            resize(event);
        } else {
            // Dragging logic
            double newX = event.getSceneX() - offsetX;
            double newY = event.getSceneY() - offsetY;
            setLayoutX(newX);
            setLayoutY(newY);
        }
    }

    private void handleMouseReleased(MouseEvent event) {
        isResizing = false;
        resizeDirection = "";
    }

    private void updateCursor(MouseEvent event) {
        if (isOnEdge(event)) {
            switch (resizeDirection) {
                case "RIGHT":
                    setCursor(Cursor.E_RESIZE);
                    break;
                case "BOTTOM":
                    setCursor(Cursor.S_RESIZE);
                    break;
                case "BOTTOM_RIGHT":
                    setCursor(Cursor.SE_RESIZE);
                    break;
                case "LEFT":
                    setCursor(Cursor.W_RESIZE);
                    break;
                case "TOP":
                    setCursor(Cursor.N_RESIZE);
                    break;
                case "TOP_LEFT":
                    setCursor(Cursor.NW_RESIZE);
                    break;
                default:
                    setCursor(Cursor.DEFAULT);
                    break;
            }
        } else {
            setCursor(Cursor.MOVE);
        }
    }

    private void resize(MouseEvent event) {
        double deltaX = event.getSceneX() - initialMouseX;
        double deltaY = event.getSceneY() - initialMouseY;

        if (resizeDirection.contains("RIGHT")) {
            double newWidth = initialWidth + deltaX;
            if (newWidth >= 100) {
                setPrefWidth(newWidth);
            }
        }

        if (resizeDirection.contains("BOTTOM")) {
            double newHeight = initialHeight + deltaY;
            if (newHeight >= 100) {
                setPrefHeight(newHeight);
            }
        }

        if (resizeDirection.contains("LEFT")) {
            double newWidth = initialWidth - deltaX;
            if (newWidth >= 100) {
                setPrefWidth(newWidth);
                setLayoutX(getLayoutX() + deltaX);
            }
        }

        if (resizeDirection.contains("TOP")) {
            double newHeight = initialHeight - deltaY;
            if (newHeight >= 100) {
                setPrefHeight(newHeight);
                setLayoutY(getLayoutY() + deltaY);
            }
        }
    }

    private boolean isOnEdge(MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();
        double width = getWidth();
        double height = getHeight();
        double edgeThreshold = 10;

        if (mouseX <= edgeThreshold && mouseY <= edgeThreshold) {
            resizeDirection = "TOP_LEFT";
            return true;
        } else if (mouseX >= width - edgeThreshold && mouseY >= height - edgeThreshold) {
            resizeDirection = "BOTTOM_RIGHT";
            return true;
        } else if (mouseX >= width - edgeThreshold) {
            resizeDirection = "RIGHT";
            return true;
        } else if (mouseY >= height - edgeThreshold) {
            resizeDirection = "BOTTOM";
            return true;
        } else if (mouseX <= edgeThreshold) {
            resizeDirection = "LEFT";
            return true;
        } else if (mouseY <= edgeThreshold) {
            resizeDirection = "TOP";
            return true;
        } else {
            resizeDirection = "";
            return false;
        }
    }

    // UI helper functions
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


    //loading from/to business layer functions
    private void loadAttributes() {
        for (AttributeComponent attribute : BClassBox.getAttributes()) {
            addAttributeToUI(attribute);
        }
    }

    private void loadOperations() {
        for (OperationComponent operation : BClassBox.getOperations()) {
            addOperationToUI(operation);
        }
    }

    // getter setters
    public List<RelationshipLine> getConnectedRelationships() {
        return connectedRelationships;
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
