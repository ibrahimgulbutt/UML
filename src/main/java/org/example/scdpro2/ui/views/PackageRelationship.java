package org.example.scdpro2.ui.views;

import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

public class PackageRelationship<T extends javafx.scene.layout.BorderPane> extends javafx.scene.Node {
    private final Line horizontalLine;
    private final Line verticalLine;
    private final Polygon arrow;
    private final Label relationshipLabel;

    private final T startPackage;
    private final T endPackage;

    private final Pane diagramPane;

    private final Circle startAnchor;
    private final Circle endAnchor;
    private final Circle intersectionAnchor;  // New anchor for the intersection point

    public Point2D startAnchorPosition;
    public Point2D endAnchorPosition;
    public Point2D intersectionAnchorPosition;
    private boolean isLineSelected = false;
    private Color originalColor = Color.BLACK;

    private static final int SNAP_INCREMENT = 5;

    public PackageRelationship(Pane diagramPane, T startPackage, T endPackage) {
        this.diagramPane = diagramPane;
        this.startPackage = startPackage;
        this.endPackage = endPackage;

        // Create lines
        horizontalLine = new Line();
        verticalLine = new Line();
        horizontalLine.setStroke(Color.BLACK);
        verticalLine.setStroke(Color.BLACK);
        horizontalLine.getStrokeDashArray().addAll(5d, 5d);  // Make it dotted
        verticalLine.getStrokeDashArray().addAll(5d, 5d);    // Make it dotted

        // Create arrow
        arrow = new Polygon();
        arrow.getPoints().addAll(0.0, 0.0, -10.0, 5.0, -10.0, -5.0);
        arrow.setFill(Color.BLACK);

        // Create relationship label
        relationshipLabel = new Label("Relation");
        relationshipLabel.setStyle("-fx-font-size: 12px; -fx-background-color: white;");

        // Create draggable anchors
        startAnchor = new Circle(10, Color.TRANSPARENT);
        endAnchor = new Circle(10, Color.TRANSPARENT);
        intersectionAnchor = new Circle(5, Color.BLACK); // Intersection anchor

        // Add to diagram pane
        diagramPane.getChildren().addAll(horizontalLine, verticalLine, arrow, relationshipLabel, startAnchor, endAnchor, intersectionAnchor);

        // Initialize positions
        startAnchorPosition = new Point2D(startPackage.getLayoutX(), startPackage.getLayoutY());
        endAnchorPosition = new Point2D(endPackage.getLayoutX() + endPackage.getWidth(), endPackage.getLayoutY());
        intersectionAnchorPosition = new Point2D((startAnchorPosition.getX() + endAnchorPosition.getX()) / 2,
                (startAnchorPosition.getY() + endAnchorPosition.getY()) / 2);


        // Set initial anchor positions
        updateAnchorsToPackageBoundaries();

        // Update line positions
        updateLines();

        // Listeners to adjust dynamically when packages are moved
        startPackage.layoutXProperty().addListener((obs, oldVal, newVal) -> updateAnchorsToPackageBoundaries());
        startPackage.layoutYProperty().addListener((obs, oldVal, newVal) -> updateAnchorsToPackageBoundaries());
        endPackage.layoutXProperty().addListener((obs, oldVal, newVal) -> updateAnchorsToPackageBoundaries());
        endPackage.layoutYProperty().addListener((obs, oldVal, newVal) -> updateAnchorsToPackageBoundaries());

        addPackageMovementListeners();
        // Add drag listeners for anchors
        addAnchorDragListeners();
        addIntersectionAnchorDragListener();  // Add drag listener for intersection anchor

        // Add mouse event listeners for lines
        addClickListeners();
        addRightClickListeners();
    }

    // Method to add click event listeners for toggling line color
    private void addClickListeners() {
        horizontalLine.setOnMouseClicked(event -> handleLineClick(event, horizontalLine));
        verticalLine.setOnMouseClicked(event -> handleLineClick(event, verticalLine));
    }

    // Handle line click to toggle color
    private void handleLineClick(MouseEvent event, Line line) {
        if (event.getClickCount() == 1) {  // Single click
            if (isLineSelected) {
                // Toggle color to revert
                line.setStroke(originalColor);
                horizontalLine.setStroke(originalColor);
                verticalLine.setStroke(originalColor);
                isLineSelected = false;
            } else {
                // Change color
                originalColor = (Color) line.getStroke();  // Save current color
                line.setStroke(Color.RED);  // Change color to red
                horizontalLine.setStroke(Color.RED);
                verticalLine.setStroke(Color.RED);
                isLineSelected = true;
            }
        }
    }

    // Method to add right-click event listeners for delete confirmation
    private void addRightClickListeners() {
        horizontalLine.setOnMousePressed(event -> {
            if (event.isSecondaryButtonDown() && event.getClickCount() == 2) {  // Double right-click
                showDeleteConfirmation();
            }
        });

        verticalLine.setOnMousePressed(event -> {
            if (event.isSecondaryButtonDown() && event.getClickCount() == 2) {  // Double right-click
                showDeleteConfirmation();
            }
        });
    }

    // Show confirmation dialog for deletion
    private void showDeleteConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Lines");
        alert.setHeaderText("Are you sure you want to delete both lines and anchors?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                deleteLinesAndAnchors();
            }
        });
    }

    // Method to delete lines and anchors
    private void deleteLinesAndAnchors() {
        diagramPane.getChildren().remove(horizontalLine);
        diagramPane.getChildren().remove(verticalLine);
        diagramPane.getChildren().remove(arrow);
        diagramPane.getChildren().remove(startAnchor);
        diagramPane.getChildren().remove(endAnchor);
        diagramPane.getChildren().remove(intersectionAnchor);
        diagramPane.getChildren().remove(relationshipLabel);
    }


    public Circle getStartAnchor() {
        return startAnchor;
    }

    public Circle getEndAnchor() {
        return endAnchor;
    }

    public Circle getIntersectionAnchor() {
        return intersectionAnchor;
    }


    private void addAnchorDragListener(Circle anchor, T packageBox) {
        anchor.setOnMouseDragged(event -> {
            double anchorX = event.getX();
            double anchorY = event.getY();

            // Get package boundaries
            double boxLeft = packageBox.getLayoutX();
            double boxRight = boxLeft + packageBox.getWidth();
            double boxTop = packageBox.getLayoutY();
            double boxBottom = boxTop + packageBox.getHeight();

            // Determine the closest edge and constrain the anchor to that edge
            double distanceToLeft = Math.abs(anchorX - boxLeft);
            double distanceToRight = Math.abs(anchorX - boxRight);
            double distanceToTop = Math.abs(anchorY - boxTop);
            double distanceToBottom = Math.abs(anchorY - boxBottom);

            if (distanceToLeft <= distanceToRight && distanceToLeft <= distanceToTop && distanceToLeft <= distanceToBottom) {
                // Closest to the left edge
                anchorX = boxLeft;
                anchorY = Math.min(Math.max(anchorY, boxTop), boxBottom);
            } else if (distanceToRight <= distanceToLeft && distanceToRight <= distanceToTop && distanceToRight <= distanceToBottom) {
                // Closest to the right edge
                anchorX = boxRight;
                anchorY = Math.min(Math.max(anchorY, boxTop), boxBottom);
            } else if (distanceToTop <= distanceToLeft && distanceToTop <= distanceToRight && distanceToTop <= distanceToBottom) {
                // Closest to the top edge
                anchorY = boxTop;
                anchorX = Math.min(Math.max(anchorX, boxLeft), boxRight);
            } else {
                // Closest to the bottom edge
                anchorY = boxBottom;
                anchorX = Math.min(Math.max(anchorX, boxLeft), boxRight);
            }

            // Update anchor position
            if (anchor == startAnchor) {
                startAnchorPosition = new Point2D(anchorX, anchorY);
            } else if (anchor == endAnchor) {
                endAnchorPosition = new Point2D(anchorX, anchorY);
            }

            anchor.setCenterX(anchorX);
            anchor.setCenterY(anchorY);

            updateLines();
        });
    }

    private Point2D getClosestEdgePoint(Circle anchor, T packageBox) {
        double boxLeft = packageBox.getLayoutX();
        double boxRight = boxLeft + packageBox.getWidth();
        double boxTop = packageBox.getLayoutY();
        double boxBottom = boxTop + packageBox.getHeight();

        double anchorX = anchor.getCenterX();
        double anchorY = anchor.getCenterY();

        // Find the closest edge point
        double distanceToLeft = Math.abs(anchorX - boxLeft);
        double distanceToRight = Math.abs(anchorX - boxRight);
        double distanceToTop = Math.abs(anchorY - boxTop);
        double distanceToBottom = Math.abs(anchorY - boxBottom);

        if (distanceToLeft <= distanceToRight && distanceToLeft <= distanceToTop && distanceToLeft <= distanceToBottom) {
            // Closest to the left edge
            return new Point2D(boxLeft, Math.min(Math.max(anchorY, boxTop), boxBottom));
        } else if (distanceToRight <= distanceToLeft && distanceToRight <= distanceToTop && distanceToRight <= distanceToBottom) {
            // Closest to the right edge
            return new Point2D(boxRight, Math.min(Math.max(anchorY, boxTop), boxBottom));
        } else if (distanceToTop <= distanceToLeft && distanceToTop <= distanceToRight && distanceToTop <= distanceToBottom) {
            // Closest to the top edge
            return new Point2D(Math.min(Math.max(anchorX, boxLeft), boxRight), boxTop);
        } else {
            // Closest to the bottom edge
            return new Point2D(Math.min(Math.max(anchorX, boxLeft), boxRight), boxBottom);
        }
    }

    private void updateAnchorVisuals() {
        startAnchor.setCenterX(startAnchorPosition.getX());
        startAnchor.setCenterY(startAnchorPosition.getY());
        endAnchor.setCenterX(endAnchorPosition.getX());
        endAnchor.setCenterY(endAnchorPosition.getY());
        intersectionAnchor.setCenterX(intersectionAnchorPosition.getX());
        intersectionAnchor.setCenterY(intersectionAnchorPosition.getY());
    }

    private void addIntersectionAnchorDragListener() {
        intersectionAnchor.setOnMouseDragged(event -> {
            double newX = snapToIncrement(event.getX());
            double newY = snapToIncrement(event.getY());

            intersectionAnchorPosition = new Point2D(newX, newY);
            intersectionAnchor.setCenterX(newX);
            intersectionAnchor.setCenterY(newY);

            updateLines();
        });
    }

    private double snapToIncrement(double value) {
        return Math.round(value / SNAP_INCREMENT) * SNAP_INCREMENT;
    }

    private void updateAnchorsToPackageBoundaries() {
        // Update start anchor
        startAnchorPosition = getClosestEdgePoint(startAnchor, startPackage);

        // Update end anchor
        endAnchorPosition = getClosestEdgePoint(endAnchor, endPackage);

        // Calculate intersection anchor position dynamically
        intersectionAnchorPosition = calculateIntersectionAnchorPosition();

        // Update anchor visuals
        updateAnchorVisuals();

        // Update lines and arrow
        updateLines();
    }

    private void addAnchorDragListeners() {
        addAnchorDragListener(startAnchor, startPackage);
        addAnchorDragListener(endAnchor, endPackage);
    }

    private void addPackageMovementListeners() {
        startPackage.layoutXProperty().addListener((obs, oldVal, newVal) -> updateAnchorsToPackageBoundaries());
        startPackage.layoutYProperty().addListener((obs, oldVal, newVal) -> updateAnchorsToPackageBoundaries());
        endPackage.layoutXProperty().addListener((obs, oldVal, newVal) -> updateAnchorsToPackageBoundaries());
        endPackage.layoutYProperty().addListener((obs, oldVal, newVal) -> updateAnchorsToPackageBoundaries());
    }

    private void updateLines() {
        // Update horizontal line
        horizontalLine.setStartX(startAnchorPosition.getX());
        horizontalLine.setStartY(startAnchorPosition.getY());
        horizontalLine.setEndX(endAnchorPosition.getX());
        horizontalLine.setEndY(startAnchorPosition.getY());

        // Update vertical line
        verticalLine.setStartX(endAnchorPosition.getX());
        verticalLine.setStartY(startAnchorPosition.getY());
        verticalLine.setEndX(endAnchorPosition.getX());
        verticalLine.setEndY(endAnchorPosition.getY());

        // Calculate intersection point
        double intersectionX = endAnchorPosition.getX();
        double intersectionY = startAnchorPosition.getY();
        intersectionAnchorPosition = new Point2D(intersectionX, intersectionY);

        // Update intersection circle
        intersectionAnchor.setCenterX(intersectionX);
        intersectionAnchor.setCenterY(intersectionY);

        // Update arrow position relative to intersection anchor
        arrow.setLayoutX(endAnchorPosition.getX());
        arrow.setLayoutY(endAnchorPosition.getY());
        arrow.setRotate(calculateArrowAngle(intersectionAnchorPosition.getX(), intersectionAnchorPosition.getY(),
                endAnchorPosition.getX(), endAnchorPosition.getY()));


        // Update label position
        relationshipLabel.setLayoutX(intersectionX + 10); // Offset slightly for readability
        relationshipLabel.setLayoutY(intersectionY - 10); // Offset slightly for readability
    }

    private Point2D calculateIntersectionAnchorPosition() {
        // Midpoint between start and end anchors
        double midX = (startAnchorPosition.getX() + endAnchorPosition.getX()) / 2;
        double midY = (startAnchorPosition.getY() + endAnchorPosition.getY()) / 2;
        return new Point2D(midX, midY);
    }

    private double calculateArrowAngle(double startX, double startY, double endX, double endY) {
        return Math.toDegrees(Math.atan2(endY - startY, endX - startX));
    }

    public T getStartPackage() {
        return startPackage;
    }

    public T getEndPackage() {
        return  endPackage;
    }

    public Line getHorizontalLine() {
        return horizontalLine;
    }

    public Line getVerticalLine() {
        return verticalLine;
    }

    public Polygon getArrow() {
        return arrow;
    }

    public Label getRelationshipLabel() {
        return relationshipLabel;
    }
}