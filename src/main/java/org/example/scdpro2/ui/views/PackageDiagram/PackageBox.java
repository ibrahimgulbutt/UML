package org.example.scdpro2.ui.views.PackageDiagram;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.example.scdpro2.business.models.BPackageDiagarm.PackageComponent;
import org.example.scdpro2.ui.controllers.MainController;

import java.util.ArrayList;
import java.util.List;

public class PackageBox extends BorderPane  {
    private final PackageComponent packageComponent;
    private final MainController controller;
    private final PackageDiagramPane diagramPane;

    private double offsetX, offsetY; // For dragging
    private double initialWidth, initialHeight; // For resizing
    private double initialMouseX, initialMouseY; // For resizing

    private boolean isResizing = false; // Tracks resizing state
    private String resizeDirection = ""; // Tracks the direction of resizing

    private final VBox contentBox = new VBox(); // Holds the package's content
    private final Label nameLabel = new Label(); // Displays package name
    private final Rectangle topRectangle = new Rectangle(); // Small rectangle on top

    public PackageBox(PackageComponent packageComponent, MainController controller, PackageDiagramPane diagramPane) {
        this.packageComponent = packageComponent;
        this.controller = controller;
        this.diagramPane = diagramPane;

        // Set up the content area
        contentBox.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: #cce5ff;");
        contentBox.setAlignment(Pos.TOP_CENTER);

        nameLabel.setText(packageComponent.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        contentBox.getChildren().add(nameLabel);

        // Set initial size
        setPrefWidth(200);
        setPrefHeight(150);

        // Set up the top rectangle
        topRectangle.setHeight(20); // Small height for the rectangle
        topRectangle.setWidth(getPrefWidth() * 0.45); // Set width to 45% of the PackageBox width
        topRectangle.setStyle("-fx-padding: 10; -fx-background-color: #cce5ff;"); // Set a color for the rectangle
        setTop(topRectangle); // Add it to the top of the BorderPane

        // Add content to the center of the BorderPane
        setCenter(contentBox);

        // Add mouse event handlers
        setOnMousePressed(this::handleMousePressed);
        setOnMouseDragged(this::handleMouseDragged);
        setOnMouseReleased(this::handleMouseReleased);

        // Configure cursor changes for resizing regions
        setOnMouseMoved(this::updateCursor);

        // Enable name editing
        setupNameEditing();

        // Right-click context menu
        ContextMenu contextMenu = new ContextMenu();
        MenuItem addClassBoxMenuItem = new MenuItem("Add Class Box");
        addClassBoxMenuItem.setOnAction(e -> addClassBox());
        contextMenu.getItems().add(addClassBoxMenuItem);

        // Add options to the context menu
        MenuItem deletePackageMenuItem = new MenuItem("Delete Package Box");
        deletePackageMenuItem.setOnAction(e -> deletePackage());
        contextMenu.getItems().add(deletePackageMenuItem);

        // Show context menu on right-click
        setOnContextMenuRequested(event -> contextMenu.show(this, event.getScreenX(), event.getScreenY()));





    }


    private void setupNameEditing() {
        nameLabel.setOnMouseClicked(event -> {
            TextField nameField = new TextField(packageComponent.getName());
            nameField.setOnAction(e -> {
                String newName = nameField.getText().trim();
                if (!newName.isEmpty()) {
                    packageComponent.setName(newName);
                    nameLabel.setText(newName);
                    contentBox.getChildren().remove(nameField); // Remove TextField
                    contentBox.getChildren().add(0, nameLabel); // Re-add Label
                }
            });
            contentBox.getChildren().remove(nameLabel); // Remove Label
            contentBox.getChildren().add(0, nameField); // Add TextField
            nameField.requestFocus();
        });
    }

    private boolean confirmAction(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setTitle(title);
        alert.setHeaderText(null);
        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    public void moveClassBoxes(double deltaX, double deltaY) {
        for (var node : diagramPane.getChildren()) {
            if (node instanceof PackageClassBox classBox && classBox.getParentPackageBox() == this) {
                classBox.setLayoutX(classBox.getLayoutX() + deltaX);
                classBox.setLayoutY(classBox.getLayoutY() + deltaY);
            }
        }
    }

    private void addClassBox() {
        // Create a new instance of PackageClassBox
        PackageClassBox classBox = new PackageClassBox(this);

        // Set the default size of the class box
        classBox.setPrefWidth(100); // Set width to 100 pixels (default size)
        classBox.setPrefHeight(80); // Set height to 80 pixels (default size)

        // Calculate the new position of the class box, ensuring it stays within the bounds of the PackageBox
        double newLayoutX = getLayoutX() + 10; // Offset for visibility
        double newLayoutY = getLayoutY() + 10;

        // Ensure the new layoutX and layoutY are within the bounds of the PackageBox
        double maxX = getLayoutX() + getPrefWidth() - classBox.getPrefWidth();
        double maxY = getLayoutY() + getPrefHeight() - classBox.getPrefHeight();
        if (newLayoutX > maxX) {
            newLayoutX = maxX; // Restrict to the right boundary
        }
        if (newLayoutY > maxY) {
            newLayoutY = maxY; // Restrict to the bottom boundary
        }

        classBox.setLayoutX(newLayoutX);
        classBox.setLayoutY(newLayoutY);

        // Add the class box to the diagram pane
        diagramPane.getChildren().add(classBox);
    }

    private void deletePackage() {
        if (confirmAction("Delete Package Box", "Are you sure you want to delete this package box and all its relationships?")) {
            List<PackageRelationship> relationshipsToRemove = new ArrayList<>();

            // Collect relationships involving this package
            for (PackageRelationship relationship : diagramPane.getRelationships()) {
                if (relationship.getStartPackage() == this || relationship.getEndPackage() == this) {
                    relationshipsToRemove.add(relationship);
                }
            }

            // Remove the collected relationships
            for (PackageRelationship relationship : relationshipsToRemove) {
                diagramPane.removeRelationship(relationship);
            }

            // Remove the package box itself
            diagramPane.getChildren().remove(this);
        }
    }

    public void addComponent(String componentName) {
        Label componentLabel = new Label(componentName);
        componentLabel.setStyle("-fx-background-color: #e6e6e6; -fx-padding: 3; -fx-border-color: black; -fx-border-width: 1;");
        contentBox.getChildren().add(componentLabel);
    }


    // drag and resize functions
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
            double newX = event.getSceneX() - offsetX;
            double newY = event.getSceneY() - offsetY;

            double deltaX = newX - getLayoutX();
            double deltaY = newY - getLayoutY();

            setLayoutX(newX);
            setLayoutY(newY);

            // Move all child class boxes
            moveClassBoxes(deltaX, deltaY);
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

    private void resize(MouseEvent event) {
        double deltaX = event.getSceneX() - initialMouseX;
        double deltaY = event.getSceneY() - initialMouseY;

        if (resizeDirection.contains("RIGHT")) {
            double newWidth = initialWidth + deltaX;
            if (newWidth >= 100) {
                setPrefWidth(newWidth);
                topRectangle.setWidth(newWidth * 0.45); // Update top rectangle width as well
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
                topRectangle.setWidth(newWidth * 0.45); // Update top rectangle width
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

    private void handleMouseReleased(MouseEvent event) {
        isResizing = false;
        resizeDirection = "";
    }


    // setter and getters
    public PackageDiagramPane getDiagramPane() {
        return diagramPane;
    }

    public PackageComponent getPackageComponent() {
        return packageComponent;
    }


}