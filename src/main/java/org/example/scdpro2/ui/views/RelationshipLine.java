package org.example.scdpro2.ui.views;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import org.example.scdpro2.business.models.ClassDiagram;
import org.example.scdpro2.ui.views.RelationshipLine.RelationshipType;

public class RelationshipLine {
    public enum RelationshipType {
        ASSOCIATION, AGGREGATION, COMPOSITION, INHERITANCE
    }

    private final Line line;
    private final Shape endIndicator;
    private final RelationshipType type;

    private final ClassDiagram sourceDiagram; // Source class diagram
    private final ClassDiagram targetDiagram; // Target class diagram

    public RelationshipLine(ClassDiagram sourceDiagram, ClassDiagram targetDiagram,
                            RelationshipType type, double startX, double startY,
                            double endX, double endY) {
        this.sourceDiagram = sourceDiagram;
        this.targetDiagram = targetDiagram;
        this.type = type;

        this.line = new Line(startX, startY, endX, endY);
        this.line.setStrokeWidth(2);
        this.line.setStroke(Color.BLACK);
        this.endIndicator = createEndIndicator(type);

        updatePosition(startX, startY, endX, endY);
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
}
