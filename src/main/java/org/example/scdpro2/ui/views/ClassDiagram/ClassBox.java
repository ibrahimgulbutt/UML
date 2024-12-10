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
/**
 * The ClassBox class represents a UI component that displays a class box in a class diagram.
 * It supports drag-and-drop functionality, resizing, and context menu options for deleting the class box.
 * The class box contains sections for attributes and operations, with the ability to add, update, and remove them.
 */
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

    /**
     * Constructs a new ClassBox with the provided class box model, controller, diagram pane, and type.
     *
     * @param BClassBox The model object for this class box.
     * @param controller The controller for the application.
     * @param diagramPane The pane containing the class diagram.
     * @param Type The type of the class box ("Class Box" or "Interface Box").
     */
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

    /**
     * Creates a context menu for the class box, with an option to delete the class.
     *
     * @param diagramPane The pane containing the class diagram.
     * @return The context menu with the delete option.
     */
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
    /**
     * Creates a name box for an interface, allowing the user to edit the interface name.
     *
     * @param diagramPane The pane containing the class diagram.
     * @return The HBox containing the name field and update button.
     */
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
    /**
     * Creates a name box for a class, allowing the user to edit the class name.
     *
     * @param diagramPane The pane containing the class diagram.
     * @return The HBox containing the name field and update button.
     */
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
    /**
     * Adds a new operation to the UI and updates the model.
     *
     * @param operation The operation to be added.
     */
    private void addOperationToUI(OperationComponent operation) {
        HBox operationBox = new HBox();

        ComboBox<String> visibilityComboBox = new ComboBox<>();
        visibilityComboBox.getItems().addAll("+", "-", "#");
        visibilityComboBox.getSelectionModel().select(operation.getVisibility());

        visibilityComboBox.prefWidthProperty().bind(operationBox.widthProperty().multiply(0.1));

        TextField operationNameField = new TextField(operation.getName());

        operationNameField.prefWidthProperty().bind(operationBox.widthProperty().multiply(0.3));

        ComboBox<String> returnTypeComboBox = new ComboBox<>();
        returnTypeComboBox.setEditable(true);
        returnTypeComboBox.getItems().addAll("void", "int", "String", "boolean", "double", "float", "char", "long", "short");
        returnTypeComboBox.setPromptText("Return Type");
        returnTypeComboBox.getSelectionModel().select(operation.getReturnType());

        returnTypeComboBox.prefWidthProperty().bind(operationBox.widthProperty().multiply(0.3));

        // Add listeners for updates
        visibilityComboBox.valueProperty().addListener((obs, oldVal, newVal) -> operation.setVisibility(newVal));
        operationNameField.textProperty().addListener((obs, oldVal, newVal) -> operation.setName(newVal));
        returnTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> operation.setReturnType(newVal));

        Button deleteButton = new Button("❌");
        deleteButton.prefWidthProperty().bind(operationBox.widthProperty().multiply(0.1));
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

    /**
     * Adds a new attribute to the UI and updates the model.
     *
     * @param attribute The attribute to be added.
     */
    private void addAttributeToUI(AttributeComponent attribute) {
        HBox attributeBox = new HBox();

        ComboBox<String> visibilityComboBox = new ComboBox<>();
        visibilityComboBox.getItems().addAll("+", "-", "#");
        visibilityComboBox.getSelectionModel().select(attribute.getVisibility());

        visibilityComboBox.prefWidthProperty().bind(attributeBox.widthProperty().multiply(0.1));


        TextField attributeNameField = new TextField(attribute.getName());

        attributeNameField.prefWidthProperty().bind(attributeBox.widthProperty().multiply(0.3));

        ComboBox<String> dataTypeComboBox = new ComboBox<>();
        dataTypeComboBox.setEditable(true);
        dataTypeComboBox.getItems().addAll("int", "String", "boolean", "double", "float", "char", "long", "short");
        dataTypeComboBox.setPromptText("Data Type");
        dataTypeComboBox.getSelectionModel().select(attribute.getDataType());
        dataTypeComboBox.prefWidthProperty().bind(attributeBox.widthProperty().multiply(0.3));


        // Add listeners for updates
        visibilityComboBox.valueProperty().addListener((obs, oldVal, newVal) -> attribute.setVisibility(newVal));
        attributeNameField.textProperty().addListener((obs, oldVal, newVal) -> attribute.setName(newVal));
        dataTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> attribute.setDataType(newVal));

        Button deleteButton = new Button("❌");
        deleteButton.prefWidthProperty().bind(attributeBox.widthProperty().multiply(0.1));
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

    /**
     * Adds a new attribute to the class box and updates both the UI and model.
     */
    private void addAttribute(VBox attributesBox) {
        AttributeComponent attribute = new AttributeComponent("attribute", "+","int");
        BClassBox.addAttribute(attribute); // Add to the business model
        addAttributeToUI(attribute);
    }
    /**
     * Adds a new operation to the class box and updates both the UI and model.
     */
    private void addOperation(VBox operationsBox) {
        OperationComponent operation = new OperationComponent("operation", "+","void");
        BClassBox.addOperation(operation); // Add to the business model
        addOperationToUI(operation);
    }
    /**
     * Handles mouse press event for dragging functionality.
     *
     * @param event The mouse event triggered on press.
     */
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
    /**
     * Handles mouse drag event for dragging functionality.
     *
     * @param event The mouse event triggered on drag.
     */
    private void handleMouseReleased(MouseEvent event) {
        isResizing = false; // End resizing
    }

    /**
     * Handles mouse release event for resizing functionality.
     *
     * @param event The mouse event triggered on release.
     */
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

    /**
     * Updates the cursor when hovering over the class box to show resizing options.
     *
     * @param event The mouse event triggered on mouse movement.
     */
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
    /**
     * Determines if the mouse is currently on the edge of the diagram component for resizing.
     * Sets the resizing direction accordingly.
     *
     * @param event The MouseEvent triggered by a mouse movement.
     * @return true if the mouse is on an edge, false otherwise.
     */
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
    /**
     * Deletes the connected relationships from the parent pane and clears the list of connected relationships.
     *
     * @param parentPane The parent pane containing the diagram component.
     */
    public void deleteConnectedRelationships(Pane parentPane) {
        for (RelationshipLine relationship : new ArrayList<>(connectedRelationships)) {
            parentPane.getChildren().remove(relationship); // Remove the relationship from the UI
            if (parentPane instanceof ClassDiagramPane) {
                ((ClassDiagramPane) parentPane).removeRelationshipLine(relationship);
            }
        }
        connectedRelationships.clear();
    }
    /**
     * Adds a relationship line to the list of connected relationships.
     *
     * @param relationship The RelationshipLine to be added.
     */
    public void addRelationship(RelationshipLine relationship) {
        connectedRelationships.add(relationship);
    }
    /**
     * Removes a relationship line from the list of connected relationships.
     *
     * @param relationship The RelationshipLine to be removed.
     */
    public void removeRelationship(RelationshipLine relationship) {
        connectedRelationships.remove(relationship);
    }
    /**
     * Loads and displays the attributes of the class in the diagram from the business layer.
     */
    private void loadAttributes() {
        for (AttributeComponent attribute : BClassBox.getAttributes()) {
            addAttributeToUI(attribute);
        }
    }
    /**
     * Loads and displays the operations of the class in the diagram from the business layer.
     */
    private void loadOperations() {
        for (OperationComponent operation : BClassBox.getOperations()) {
            addOperationToUI(operation);
        }
    }

    // getter setters
    /**
     * Gets the list of connected relationship lines.
     *
     * @return A list of RelationshipLine objects representing the connected relationships.
     */
    public List<RelationshipLine> getConnectedRelationships() {
        return connectedRelationships;
    }
    /**
     * Gets the list of attributes for the class diagram.
     *
     * @return A list of AttributeComponent objects representing the class attributes.
     */
    public List<AttributeComponent> getAttributes() {
        return BClassBox.getAttributes();
    }
    /**
     * Gets the list of operations for the class diagram.
     *
     * @return A list of OperationComponent objects representing the class operations.
     */
    public List<OperationComponent> getOperations() {
        return BClassBox.getOperations();
    }
    /**
     * Sets the attributes for the class diagram.
     *
     * @param attributes A list of AttributeComponent objects to set as the attributes.
     */
    public void setAttributes(List<AttributeComponent> attributes) {
        BClassBox.setAttributes(attributes);
    }
    /**
     * Sets the operations for the class diagram.
     *
     * @param operations A list of OperationComponent objects to set as the operations.
     */
    public void setOperations(List<OperationComponent> operations) {
        BClassBox.setOperations(operations);
    }
    /**
     * Gets the BClassBox associated with this class diagram component.
     *
     * @return The BClassBox representing the class diagram.
     */
    public BClassBox getClassDiagram() {
        return BClassBox;
    }
    /**
     * Gets the class name of the diagram.
     *
     * @return The title of the class, which represents the class name in the diagram.
     */
    public String getClassName() {
        return BClassBox.getTitle();
    }
    /**
     * Gets the title of the class diagram.
     *
     * @return The title of the class diagram.
     */

    public String getTitle() {
        return BClassBox.getTitle();
    }
    /**
     * Gets a string representation of the attributes box of the class diagram.
     *
     * @return A string representing the attributes box.
     */
    public Object getAttributesBox() {
        return BClassBox.getAttributes().toString();
    }
    /**
     * Gets a string representation of the operations box of the class diagram.
     *
     * @return A string representing the operations box.
     */
    public Object getOperationsBox() {
        return BClassBox.getOperations().toString();
    }
    /**
     * Gets the BClassBox diagram component.
     *
     * @return The BClassBox representing the class diagram.
     */
    public Object getDiagram() {
        return BClassBox;
    }
}
