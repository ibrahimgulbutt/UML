package org.example.scdpro2.ui.views;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.MouseEvent;

public class PackageClassBox extends BorderPane {
    private double offsetX, offsetY;
    private double initialWidth, initialHeight;
    private double initialMouseX, initialMouseY;
    private boolean isResizing = false;
    private String resizeDirection = "";

    private final Rectangle topRectangle = new Rectangle();
    private final Label nameLabel = new Label();

    public PackageClassBox() {
        // Set up the top rectangle
        topRectangle.setHeight(20);
        topRectangle.setFill(Color.LIGHTGRAY);
        setTop(topRectangle);

        // Set up the label (title)
        nameLabel.setText("Class");
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        BorderPane.setAlignment(nameLabel, Pos.CENTER);
        setCenter(nameLabel);

        // Initial size
        setPrefWidth(100);
        setPrefHeight(80);

        // Event handlers for resizing and moving
        setOnMousePressed(this::handleMousePressed);
        setOnMouseDragged(this::handleMouseDragged);
        setOnMouseReleased(this::handleMouseReleased);
        setOnMouseMoved(this::updateCursor);
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
            // Dragging logic
            setLayoutX(event.getSceneX() - offsetX);
            setLayoutY(event.getSceneY() - offsetY);
        }
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
            if (newWidth >= 100) {
                setPrefWidth(newWidth);
                topRectangle.setWidth(newWidth); // Update rectangle width
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
                topRectangle.setWidth(newWidth); // Update rectangle width
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
}