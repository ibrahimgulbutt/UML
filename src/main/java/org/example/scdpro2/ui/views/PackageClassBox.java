package org.example.scdpro2.ui.views;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PackageClassBox extends BorderPane {
    private double offsetX, offsetY;
    private double initialWidth, initialHeight;
    private double initialMouseX, initialMouseY;
    private boolean isResizing = false;
    private String resizeDirection = "";

    private final VBox classNameBox = new VBox();
    private final TextField nameField = new TextField();
    private final ComboBox<String> visibilityDropdown = new ComboBox<>();
    private PackageBox parentPackageBox; // Reference to parent PackageBox

    public PackageClassBox(PackageBox parentPackageBox) {
        this.parentPackageBox = parentPackageBox;

        // Set up the VBox for the class name and visibility dropdown
        classNameBox.setAlignment(Pos.CENTER);
        classNameBox.setSpacing(5);
        classNameBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 5; -fx-background-color: lightgray;");
        setTop(classNameBox);

        // Set up the editable text field for the class name
        nameField.setText("Class");
        nameField.setPromptText("Enter class name");
        nameField.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        nameField.setPrefWidth(100);

        // Set up the visibility dropdown
        visibilityDropdown.getItems().addAll("+", "-", "#"); // Options for visibility
        visibilityDropdown.setValue("+"); // Default value
        visibilityDropdown.setPrefWidth(50);

        // Combine the name field and dropdown in an HBox
        HBox nameAndVisibilityBox = new HBox(5); // Spacing between elements
        nameAndVisibilityBox.setAlignment(Pos.CENTER_LEFT);
        nameAndVisibilityBox.getChildren().addAll(nameField, visibilityDropdown);

        // Add the HBox to the VBox
        classNameBox.getChildren().add(nameAndVisibilityBox);

        // Initial size
        setPrefWidth(150);
        setPrefHeight(80);

        // Event handlers for resizing and moving
        setOnMousePressed(this::handleMousePressed);
        setOnMouseDragged(this::handleMouseDragged);
        setOnMouseReleased(this::handleMouseReleased);
        setOnMouseMoved(this::updateCursor);

        // Add a context menu for deletion
        setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY && event.getClickCount() == 2) {
                // Show confirmation dialog
                if (confirmAction("Delete Class Box", "Are you sure you want to delete this class box?")) {
                    parentPackageBox.getDiagramPane().getChildren().remove(this); // Remove from the diagram
                }
            }
        });
    }

    // Utility method for showing a confirmation dialog
    private boolean confirmAction(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setTitle(title);
        alert.setHeaderText(null);
        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

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

            // Get parent boundaries
            double parentX = parentPackageBox.getLayoutX();
            double parentY = parentPackageBox.getLayoutY();
            double parentWidth = parentPackageBox.getPrefWidth();
            double parentHeight = parentPackageBox.getPrefHeight();

            // Calculate constrained positions
            double minX = parentX;
            double minY = parentY;
            double maxX = parentX + parentWidth - getPrefWidth();
            double maxY = parentY + parentHeight - getPrefHeight();

            // Constrain movement
            newX = Math.max(minX, Math.min(newX, maxX));
            newY = Math.max(minY, Math.min(newY, maxY));

            setLayoutX(newX);
            setLayoutY(newY);
        }
    }

    public PackageBox getParentPackageBox() {
        return parentPackageBox;
    }

    private void handleMouseReleased(MouseEvent event) {
        isResizing = false;
        resizeDirection = "";
    }

    private void resize(MouseEvent event) {
        double deltaX = event.getSceneX() - initialMouseX;
        double deltaY = event.getSceneY() - initialMouseY;

        if (resizeDirection.contains("RIGHT")) {
            double newWidth = initialWidth + deltaX;
            if (newWidth >= 50) setPrefWidth(newWidth);
        }
        if (resizeDirection.contains("BOTTOM")) {
            double newHeight = initialHeight + deltaY;
            if (newHeight >= 50) setPrefHeight(newHeight);
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
                default:
                    setCursor(Cursor.DEFAULT);
                    break;
            }
        } else {
            setCursor(Cursor.MOVE);
        }
    }

    private boolean isOnEdge(MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();
        double width = getWidth();
        double height = getHeight();
        double edgeThreshold = 10;

        if (mouseX >= width - edgeThreshold) {
            resizeDirection = "RIGHT";
            return true;
        } else if (mouseY >= height - edgeThreshold) {
            resizeDirection = "BOTTOM";
            return true;
        } else {
            resizeDirection = "";
            return false;
        }
    }
}
