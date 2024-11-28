package org.example.scdpro2.ui.views;

import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;
import org.example.scdpro2.business.models.ClassDiagram;
import org.example.scdpro2.business.models.PackageComponent;

public class RelationshipLine extends Group {
    private ClassBox target;
    private ClassBox source;
    private boolean isSelected;

    public ClassBox getSource() {
        return source;
    }

    public ClassBox getTarget() {
        return target;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public enum RelationshipType {
        ASSOCIATION, AGGREGATION, COMPOSITION, INHERITANCE
    }

    private Line line;
    private Shape endIndicator;
    private RelationshipType type;

    private Label relationshipLabel;

    private ClassDiagram sourceDiagram; // Source class diagram
    private ClassDiagram targetDiagram; // Target class diagram

    private PackageComponent sourcePackage;
    private PackageComponent targetPackage;

    private Polyline polyline;
    private int relationshipIndex;

    public RelationshipLine(ClassDiagram sourceDiagram, ClassDiagram targetDiagram,
                            RelationshipType type, double startX, double startY,
                            double endX, double endY) {
        this.sourceDiagram = sourceDiagram;
        this.targetDiagram = targetDiagram;
        this.type = type;


        this.polyline = new Polyline();

        this.line = new Line(startX, startY, endX, endY);
        this.line.setStrokeWidth(2);
        this.line.setStroke(Color.BLACK);
        this.endIndicator = createEndIndicator(type);


        updatePosition(startX, startY, endX, endY);

        this.getChildren().add(line); // Add the line to the Group
        System.out.println("Start: (" + startX + ", " + startY + "), End: (" + endX + ", " + endY + ")");

    }

    public RelationshipLine(PackageComponent sourcePackage, PackageComponent targetPackage,
                            double startX, double startY, double endX, double endY, String title) {
        this.sourcePackage = sourcePackage;
        this.targetPackage = targetPackage;

        // Create the line
        this.line = new Line(startX, startY, endX, endY);
        this.line.setStrokeWidth(2);
        this.line.setStroke(Color.BLACK);

        updateRightAnglePath();
        // Create the label for the title above the line
        this.relationshipLabel = new Label(title);
        this.relationshipLabel.setStyle("-fx-font-size: 12; -fx-background-color: white;");
        updatepackPosition(startX, startY, endX, endY);
    }

    public RelationshipLine(ClassBox source, String sourceSide, ClassBox target, String targetSide, RelationshipType type, int sourceOffsetIndex, int targetOffsetIndex, int relationshipIndex) {
        this.source = source;
        this.target = target;
        this.type = type;
        this.relationshipIndex = relationshipIndex; // Track relationship index

        this.line = new Line();
        this.polyline = new Polyline();

        // Make the line easier to click
        this.line.setStrokeWidth(100); // Increase the width
        this.line.setStroke(Color.BLACK); // Default color
        this.line.setPickOnBounds(true); // Allow picking the thin line

        source.addRelationship(this);
        target.addRelationship(this);

        source.layoutXProperty().addListener((obs, oldVal, newVal) -> updateRightAnglePath());
        source.layoutYProperty().addListener((obs, oldVal, newVal) -> updateRightAnglePath());
        target.layoutXProperty().addListener((obs, oldVal, newVal) -> updateRightAnglePath());
        target.layoutYProperty().addListener((obs, oldVal, newVal) -> updateRightAnglePath());

        updateRightAnglePath();

        this.getChildren().addAll(polyline, line);
        setupMouseEvents();
    }



    private void updateRightAnglePath() {
        if (source == null || target == null || polyline == null) return;

        // Calculate offsets for parallel lines (adjust spacing as needed)
        double offset = 20 * relationshipIndex; // 20 pixels per relationship

        // Get source and target class box boundaries
        double sourceX = source.getLayoutX();
        double sourceY = source.getLayoutY();
        double sourceWidth = source.getWidth();
        double sourceHeight = source.getHeight();

        double targetX = target.getLayoutX();
        double targetY = target.getLayoutY();
        double targetWidth = target.getWidth();
        double targetHeight = target.getHeight();

        // Determine the direction of the connection and adjust the starting/ending points
        double startX, startY, endX, endY;

        if (Math.abs(targetX - sourceX) > Math.abs(targetY - sourceY)) {
            // Horizontal connection
            startX = sourceX + (targetX > sourceX ? sourceWidth : 0); // Right or Left edge
            startY = sourceY + sourceHeight / 2 + offset; // Center vertically, add offset
            endX = targetX + (targetX > sourceX ? 0 : targetWidth); // Left or Right edge
            endY = targetY + targetHeight / 2 + offset; // Center vertically, add offset
        } else {
            // Vertical connection
            startX = sourceX + sourceWidth / 2 + offset; // Center horizontally, add offset
            startY = sourceY + (targetY > sourceY ? sourceHeight : 0); // Bottom or Top edge
            endX = targetX + targetWidth / 2 + offset; // Center horizontally, add offset
            endY = targetY + (targetY > sourceY ? 0 : targetHeight); // Top or Bottom edge
        }

        // Update the polyline for the right-angle path
        polyline.getPoints().clear();
        polyline.getPoints().addAll(
                startX, startY, // Start point
                startX, endY,   // Turn point
                endX, endY      // End point
        );
    }





    private void setupMouseEvents() {
        // Single click to toggle selection
        line.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                isSelected = !isSelected;
                updateSelectionStyle();
            }
            event.consume();
        });

        // Right-click to prompt deletion
        line.setOnContextMenuRequested(event -> {
            isSelected = true; // Highlight the line
            updateSelectionStyle();

            // Show confirmation dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to delete this relationship?",
                    ButtonType.YES, ButtonType.NO);
            alert.setHeaderText("Delete Relationship");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    deleteRelationship();
                } else {
                    // Cancel deletion, revert selection
                    isSelected = false;
                    updateSelectionStyle();
                }
            });

            event.consume();
        });
    }

    private void deleteRelationship() {
        if (getParent() instanceof Pane parentPane) {
            parentPane.getChildren().remove(this); // Remove from UI
            if (parentPane instanceof ClassDiagramPane) {
                ((ClassDiagramPane) parentPane).removeRelationshipLine(this);
            }
            source.removeRelationship(this);
            target.removeRelationship(this);
            System.out.println("Relationship deleted.");
        }
    }

    private void updateSelectionStyle() {
        if (isSelected) {
            line.setStroke(Color.RED); // Highlight in red when selected
        } else {
            line.setStroke(Color.BLACK); // Default to black
        }
    }





    public void updateStartCoordinates(double startX, double startY) {
        line.setStartX(startX);
        line.setStartY(startY);
    }

    public void updateEndCoordinates(double endX, double endY) {
        line.setEndX(endX);
        line.setEndY(endY);

        if (endIndicator instanceof Polygon polygon) {
            double[] offset = calculateOffset(line.getStartX(), line.getStartY(), endX, endY, 20);
            polygon.setLayoutX(offset[0]);
            polygon.setLayoutY(offset[1]);
            polygon.setRotate(calculateRotationAngle(line.getStartX(), line.getStartY(), endX, endY));
        }
    }






    public void updatepackPosition(double startX, double startY, double endX, double endY) {
        line.setStartX(startX);
        line.setStartY(startY);
        line.setEndX(endX);
        line.setEndY(endY);

        // Position the label at the midpoint of the line
        double midX = (startX + endX) / 2;
        double midY = (startY + endY) / 2 - 15; // Slightly above the line
        relationshipLabel.setLayoutX(midX - relationshipLabel.getWidth() / 2);
        relationshipLabel.setLayoutY(midY - relationshipLabel.getHeight() / 2);
    }

    public void updatePosition(double startX, double startY, double endX, double endY) {
        line.setStartX(startX);
        line.setStartY(startY);
        line.setEndX(endX);
        line.setEndY(endY);

        if (endIndicator instanceof Polygon polygon) {
            double[] offset = calculateOffset(startX, startY, endX, endY, 20); // Adjust as needed
            polygon.setLayoutX(offset[0]);
            polygon.setLayoutY(offset[1]);
            polygon.setRotate(calculateRotationAngle(startX, startY, endX, endY));
        }
    }

    public Label getRelationshipLabel() {
        return relationshipLabel;
    }

    public PackageComponent getSourcePackage() {
        return sourcePackage;
    }

    public PackageComponent getTargetPackage() {
        return targetPackage;
    }

    private Shape createEndIndicator(RelationshipType type) {
        switch (type) {
            case INHERITANCE: // Triangle arrowhead
                Polygon triangle = new Polygon();
                triangle.getPoints().addAll(0.0, 0.0, -10.0, -5.0, -10.0, 5.0);
                triangle.setFill(Color.WHITE);
                triangle.setStroke(Color.BLACK);
                return triangle;
            case AGGREGATION: // Diamond
                Polygon diamond = new Polygon();
                diamond.getPoints().addAll(0.0, 0.0, -10.0, -5.0, -20.0, 0.0, -10.0, 5.0);
                diamond.setFill(Color.WHITE);
                diamond.setStroke(Color.BLACK);
                return diamond;
            case COMPOSITION: // Filled diamond
                Polygon filledDiamond = new Polygon();
                filledDiamond.getPoints().addAll(0.0, 0.0, -10.0, -5.0, -20.0, 0.0, -10.0, 5.0);
                filledDiamond.setFill(Color.BLACK);
                return filledDiamond;
            default:
                return null;
        }
    }

    private double[] calculateOffset(double startX, double startY, double endX, double endY, double distance) {
        double angle = Math.atan2(endY - startY, endX - startX);
        return new double[]{endX - distance * Math.cos(angle), endY - distance * Math.sin(angle)};
    }

    private double calculateRotationAngle(double startX, double startY, double endX, double endY) {
        return Math.toDegrees(Math.atan2(endY - startY, endX - startX));
    }

    public Line getLine() {
        return line;
    }

    public Shape getEndIndicator() {
        return endIndicator;
    }

    public ClassDiagram getSourceDiagram() {
        return sourceDiagram;
    }

    public ClassDiagram getTargetDiagram() {
        return targetDiagram;
    }

    public RelationshipType getType() {
        return type;
    }

    public void enableSelectionAndDeletion(ClassDiagramPane parentPane) {
        line.setOnMouseClicked(event -> {
            highlightLine(true);
            showDeleteConfirmation(parentPane);
        });

        if (endIndicator != null) {
            endIndicator.setOnMouseClicked(event -> {
                highlightLine(true);
                showDeleteConfirmation(parentPane);
            });
        }
    }

    private void highlightLine(boolean highlight) {
        line.setStroke(highlight ? Color.RED : Color.BLACK);
    }

    private void showDeleteConfirmation(ClassDiagramPane parentPane) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete this relationship?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            parentPane.removeRelationshipLine(this);
        } else {
            highlightLine(false);
        }
    }

    public boolean isConnectedTo(ClassBox classBox) {
        return sourceDiagram.equals(classBox.getClassDiagram()) || targetDiagram.equals(classBox.getClassDiagram());
    }

    public boolean isConnectedTo(InterfaceBox interfaceBox) {
        return sourceDiagram.equals(interfaceBox.getInterfaceDiagram()) || targetDiagram.equals(interfaceBox.getInterfaceDiagram());
    }

    public void setType(RelationshipType type) {
        this.type = type;
    }

}
