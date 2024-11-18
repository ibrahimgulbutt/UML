package org.example.scdpro2.ui.views;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import org.example.scdpro2.business.models.ClassDiagram;

public class RelationshipLine extends javafx.scene.Node {
    public enum RelationshipType {
        ASSOCIATION, AGGREGATION, COMPOSITION, INHERITANCE
    }

    private Line line;
    private Shape endIndicator;
    private RelationshipType type;

    private final ClassDiagram sourceDiagram; // Reference to the source ClassDiagram
    private final ClassDiagram targetDiagram; // Reference to the target ClassDiagram

    public RelationshipLine(ClassDiagram sourceDiagram, ClassDiagram targetDiagram,
                            double startX, double startY, double endX, double endY,
                            RelationshipType type) {
        this.sourceDiagram = sourceDiagram;
        this.targetDiagram = targetDiagram;

        this.type = type;
        this.line = new Line(startX, startY, endX, endY);
        this.line.setStrokeWidth(2);
        this.line.setStroke(Color.BLACK);
        this.endIndicator = createEndIndicator(type, endX, endY);

        updatePosition(startX, startY, endX, endY);
    }

    public void updatePosition(double startX, double startY, double endX, double endY) {
        line.setStartX(startX);
        line.setStartY(startY);
        line.setEndX(endX);
        line.setEndY(endY);

        if (endIndicator instanceof Polygon polygon) {
            polygon.setLayoutX(endX);
            polygon.setLayoutY(endY);
        }
    }

    private Shape createEndIndicator(RelationshipType type, double x, double y) {
        switch (type) {
            case INHERITANCE: // Triangle arrowhead
                Polygon triangle = new Polygon();
                triangle.getPoints().addAll(
                        x, y,
                        x - 10, y - 5,
                        x - 10, y + 5
                );
                triangle.setFill(Color.WHITE);
                triangle.setStroke(Color.BLACK);
                return triangle;

            case AGGREGATION: // Diamond
                Polygon diamond = new Polygon();
                diamond.getPoints().addAll(
                        x, y,
                        x - 10, y - 5,
                        x - 20, y,
                        x - 10, y + 5
                );
                diamond.setFill(Color.WHITE);
                diamond.setStroke(Color.BLACK);
                return diamond;

            case COMPOSITION: // Filled diamond
                Polygon filledDiamond = new Polygon();
                filledDiamond.getPoints().addAll(
                        x, y,
                        x - 10, y - 5,
                        x - 20, y,
                        x - 10, y + 5
                );
                filledDiamond.setFill(Color.BLACK);
                return filledDiamond;

            case ASSOCIATION: // Plain line (no additional indicator)
            default:
                return null;
        }
    }

    public Line getLine() {
        return line;
    }

    public Shape getEndIndicator() {
        return endIndicator;
    }

    // Utility methods to get source and target diagrams
    public ClassDiagram getSourceDiagram() {
        return sourceDiagram;
    }

    public ClassDiagram getTargetDiagram() {
        return targetDiagram;
    }

    public boolean isConnectedTo(ClassBox classBox) {
        ClassDiagram diagram = classBox.getClassDiagram();
        return diagram.equals(sourceDiagram) || diagram.equals(targetDiagram);
    }
}
