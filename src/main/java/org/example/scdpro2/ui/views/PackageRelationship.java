package org.example.scdpro2.ui.views;

import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

public class PackageRelationship extends javafx.scene.Node {
    private final Line horizontalLine;
    private final Line verticalLine;
    private final Polygon arrow;
    private final Label relationshipLabel;
    private final PackageBox startPackage;
    private final PackageBox endPackage;
    private final Pane diagramPane;

    private boolean isSelected = false;

    public PackageRelationship(Pane diagramPane, PackageBox startPackage, PackageBox endPackage) {
        this.diagramPane = diagramPane;
        this.startPackage = startPackage;
        this.endPackage = endPackage;

        // Create lines
        horizontalLine = new Line();
        verticalLine = new Line();
        horizontalLine.setStroke(Color.BLACK);
        verticalLine.setStroke(Color.BLACK);

        // Create arrow
        arrow = new Polygon();
        arrow.getPoints().addAll(0.0, 0.0, -10.0, 5.0, -10.0, -5.0);
        arrow.setFill(Color.BLACK);

        // Create relationship label
        relationshipLabel = new Label("Relation");
        relationshipLabel.setStyle("-fx-font-size: 12px; -fx-background-color: white;");

        // Add to diagram pane
        diagramPane.getChildren().addAll(horizontalLine, verticalLine, arrow, relationshipLabel);

        // Update line positions
        updateLines();

        // Listeners to adjust dynamically when packages are moved
        startPackage.layoutXProperty().addListener((obs, oldVal, newVal) -> updateLines());
        startPackage.layoutYProperty().addListener((obs, oldVal, newVal) -> updateLines());
        endPackage.layoutXProperty().addListener((obs, oldVal, newVal) -> updateLines());
        endPackage.layoutYProperty().addListener((obs, oldVal, newVal) -> updateLines());

        // Add selection and context menu handling
        addInteractionListeners();
    }

    private void addInteractionListeners() {
        // Select line on click
        horizontalLine.setOnMouseClicked(this::handleLineClick);
        verticalLine.setOnMouseClicked(this::handleLineClick);

        // Context menu on right-click
        horizontalLine.setOnMousePressed(event -> handleRightClick(event, this));
        verticalLine.setOnMousePressed(event -> handleRightClick(event, this));
    }

    private void handleLineClick(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            isSelected = !isSelected;
            Color color = isSelected ? Color.BLUE : Color.BLACK;
            horizontalLine.setStroke(color);
            verticalLine.setStroke(color);
            arrow.setFill(color);
        }
    }

    private void handleRightClick(MouseEvent event, PackageRelationship relationship) {
        if (event.getButton() == MouseButton.SECONDARY) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete this relationship?", ButtonType.YES, ButtonType.NO);
            alert.setTitle("Delete Relationship");
            alert.setHeaderText(null);

            if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                diagramPane.getChildren().removeAll(horizontalLine, verticalLine, arrow, relationshipLabel);
            }
        }
    }
    private void updateLines() {
        // Calculate the nearest boundary points
        double startX = getBoundaryPoint(startPackage, endPackage).getX();
        double startY = getBoundaryPoint(startPackage, endPackage).getY();
        double endX = getBoundaryPoint(endPackage, startPackage).getX();
        double endY = getBoundaryPoint(endPackage, startPackage).getY();

        // Horizontal line position
        horizontalLine.setStartX(startX);
        horizontalLine.setEndX(endX);
        horizontalLine.setStartY(startY);
        horizontalLine.setEndY(startY);

        // Vertical line position
        verticalLine.setStartX(endX);
        verticalLine.setEndX(endX);
        verticalLine.setStartY(startY);
        verticalLine.setEndY(endY);

        // Arrow position
        arrow.setLayoutX(endX);
        arrow.setLayoutY(endY);
        arrow.setRotate(calculateArrowAngle(startX, startY, endX, endY));

        // Label position
        relationshipLabel.setLayoutX((startX + endX) / 2 + 10);
        relationshipLabel.setLayoutY((startY + endY) / 2 - 10);
    }

    private Point2D getBoundaryPoint(PackageBox source, PackageBox target) {
        double sourceX = source.getLayoutX() + source.getWidth() / 2;
        double sourceY = source.getLayoutY() + source.getHeight() / 2;
        double targetX = target.getLayoutX() + target.getWidth() / 2;
        double targetY = target.getLayoutY() + target.getHeight() / 2;

        // Determine the closest boundary point
        double dx = targetX - sourceX;
        double dy = targetY - sourceY;
        double absDx = Math.abs(dx);
        double absDy = Math.abs(dy);

        if (absDx > absDy) {
            return new Point2D(
                    dx > 0 ? source.getLayoutX() + source.getWidth() : source.getLayoutX(),
                    sourceY
            );
        } else {
            return new Point2D(
                    sourceX,
                    dy > 0 ? source.getLayoutY() + source.getHeight() : source.getLayoutY()
            );
        }
    }

    private double calculateArrowAngle(double startX, double startY, double endX, double endY) {
        return Math.toDegrees(Math.atan2(endY - startY, endX - startX));
    }

    public PackageBox getStartPackage() {
        return startPackage;
    }

    public PackageBox getEndPackage() {
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
