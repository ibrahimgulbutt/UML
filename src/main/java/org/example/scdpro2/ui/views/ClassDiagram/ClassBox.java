package org.example.scdpro2.ui.views.ClassDiagram;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.Cursor;
import javafx.geometry.Pos;
import org.example.scdpro2.business.models.BClassDiagarm.AttributeComponent;
import org.example.scdpro2.business.models.BClassDiagarm.BClassBox;
import org.example.scdpro2.business.models.BClassDiagarm.OperationComponent;
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

    private double offsetX, offsetY; // For dragging
    private double initialWidth, initialHeight; // For resizing
    private double initialMouseX, initialMouseY; // For resizing
    private boolean isResizing = false; // Tracks resizing state
    private String resizeDirection = ""; // Tracks the direction of resizing
    private String Type;

    public ClassBox(BClassBox BClassBox, MainController controller, ClassDiagramPane diagramPane, String Type) {
        this.BClassBox = BClassBox;
        this.controller = controller;
        this.Type=Type;

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

        HBox nameBox= new HBox();
        // Class name section
        if(Type.equals("Class Box"))
        {
            nameBox = createNameBox(diagramPane);
        } else if (Type.equals("Interface Box"))
        {
            nameBox = createInterfaceNameBox(diagramPane);
        }

        Button addAttributeButton = new Button();
        if(Type.equals("Class Box"))
        {
            // Attributes section
//        Label attributesLabel = new Label("Attributes:");
            addAttributeButton = new Button("+");
            addAttributeButton.setOnAction(e -> addAttribute(attributesBox));
            attributesBox.getChildren().addAll( addAttributeButton);
            loadAttributes();
        }

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

    private HBox createInterfaceNameBox(ClassDiagramPane diagramPane) {
        VBox titleBox = new VBox(5); // Container for the title and name box
        titleBox.setAlignment(Pos.CENTER); // Center align

        // Add the permanent title
        Label interfaceTitle = new Label("<<Interface>>");
        interfaceTitle.getStyleClass().add("interface-title"); // Add a CSS class for custom styling

        HBox nameBox = new HBox(10); // Spacing between class name and button
        nameBox.setAlignment(Pos.CENTER); // Center the class name

        TextField classNameField = new TextField(BClassBox.getTitle());
        classNameField.getStyleClass().add("classbox-input");

        classNameField.setPromptText("Interface Name");
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
        titleBox.getChildren().addAll(interfaceTitle, nameBox);
        return new HBox(titleBox); // Return as HBox for BorderPane integration
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

        visibilityComboBox.setPrefWidth(10);
        visibilityComboBox.setPrefHeight(20);
        visibilityComboBox.setMinWidth(50);

        TextField operationNameField = new TextField(operation.getName());

        operationNameField.setPrefWidth(70);
        operationNameField.setPrefHeight(20);

        ComboBox<String> returnTypeComboBox = new ComboBox<>();
        returnTypeComboBox.setEditable(true);
        returnTypeComboBox.getItems().addAll("void", "int", "String", "boolean", "double", "float", "char", "long", "short");
        returnTypeComboBox.setPromptText("Return Type");
        returnTypeComboBox.getSelectionModel().select(operation.getReturnType());

        returnTypeComboBox.setPrefWidth(50);
        returnTypeComboBox.setPrefHeight(20);

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

        visibilityComboBox.setPrefWidth(10);  // Example width
        visibilityComboBox.setPrefHeight(20);  // Example height
        visibilityComboBox.setMinWidth(50);
        visibilityComboBox.setMaxWidth(100);

        TextField attributeNameField = new TextField(attribute.getName());

        // Set preferred width and height of attributeNameField
        attributeNameField.setPrefWidth(70);  // Example width
        attributeNameField.setPrefHeight(20);  // Example height
        attributeNameField.setMaxWidth(200);

        ComboBox<String> dataTypeComboBox = new ComboBox<>();
        dataTypeComboBox.setEditable(true);
        dataTypeComboBox.getItems().addAll("int", "String", "boolean", "double", "float", "char", "long", "short");
        dataTypeComboBox.setPromptText("Data Type");
        dataTypeComboBox.getSelectionModel().select(attribute.getDataType());

        dataTypeComboBox.setMinWidth(50);
        dataTypeComboBox.setMaxWidth(200);
        dataTypeComboBox.setPrefWidth(50);
        dataTypeComboBox.setPrefHeight(20);

        // Add listeners for updates
        visibilityComboBox.valueProperty().addListener((obs, oldVal, newVal) -> attribute.setVisibility(newVal));
        attributeNameField.textProperty().addListener((obs, oldVal, newVal) -> attribute.setName(newVal));
        dataTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> attribute.setDataType(newVal));

        Button deleteButton = new Button("❌");
        deleteButton.setOnAction(e -> {
            attributesBox.getChildren().remove(attributeBox); // Remove from UI
            BClassBox.removeAttribute(attribute);            // Remove from model
        });
        deleteButton.setMaxWidth(200);

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
            isResizing = true;
            initialMouseX = event.getSceneX();
            initialMouseY = event.getSceneY();
            initialWidth = getWidth();
            initialHeight = getHeight();
            event.consume();
        } else {
            isResizing = false; // Reset resizing state if not on edge
            offsetX = event.getSceneX() - getLayoutX();
            offsetY = event.getSceneY() - getLayoutY();
        }
    }

    private void handleMouseReleased(MouseEvent event) {
        isResizing = false; // End resizing
    }


    private void handleMouseDragged(MouseEvent event) {
        if (isResizing) {
            double deltaX = event.getSceneX() - initialMouseX;
            double deltaY = event.getSceneY() - initialMouseY;

            if (resizeDirection.contains("right")) setPrefWidth(Math.max(initialWidth + deltaX, getMinWidth()));
            if (resizeDirection.contains("left")) setPrefWidth(Math.max(initialWidth - deltaX, getMinWidth()));
            if (resizeDirection.contains("bottom")) setPrefHeight(Math.max(initialHeight + deltaY, getMinHeight()));
            if (resizeDirection.contains("top")) setPrefHeight(Math.max(initialHeight - deltaY, getMinHeight()));

            event.consume();
        } else {
            // Handle dragging
            double newX = event.getSceneX() - offsetX;
            double newY = event.getSceneY() - offsetY;
            setLayoutX(newX);
            setLayoutY(newY);
        }
    }


    private void updateCursor(MouseEvent event) {
        if (isOnEdge(event)) {
            switch (resizeDirection) {
                case "top-left":
                case "bottom-right":
                    setCursor(Cursor.NW_RESIZE);
                    break;
                case "top-right":
                case "bottom-left":
                    setCursor(Cursor.NE_RESIZE);
                    break;
                case "top":
                case "bottom":
                    setCursor(Cursor.V_RESIZE);
                    break;
                case "left":
                case "right":
                    setCursor(Cursor.H_RESIZE);
                    break;
            }
        } else {
            setCursor(Cursor.DEFAULT);
        }
    }

    private boolean isOnEdge(MouseEvent event) {
        double edgeThreshold = 5.0; // Buffer zone for detecting edges
        double mouseX = event.getX();
        double mouseY = event.getY();

        boolean onLeftEdge = mouseX >= 0 && mouseX <= edgeThreshold;
        boolean onRightEdge = mouseX >= getWidth() - edgeThreshold && mouseX <= getWidth();
        boolean onTopEdge = mouseY >= 0 && mouseY <= edgeThreshold;
        boolean onBottomEdge = mouseY >= getHeight() - edgeThreshold && mouseY <= getHeight();

        // Set resize direction
        if (onLeftEdge && onTopEdge) resizeDirection = "top-left";
        else if (onLeftEdge && onBottomEdge) resizeDirection = "bottom-left";
        else if (onRightEdge && onTopEdge) resizeDirection = "top-right";
        else if (onRightEdge && onBottomEdge) resizeDirection = "bottom-right";
        else if (onLeftEdge) resizeDirection = "left";
        else if (onRightEdge) resizeDirection = "right";
        else if (onTopEdge) resizeDirection = "top";
        else if (onBottomEdge) resizeDirection = "bottom";
        else resizeDirection = "";

        return !resizeDirection.isEmpty();
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
