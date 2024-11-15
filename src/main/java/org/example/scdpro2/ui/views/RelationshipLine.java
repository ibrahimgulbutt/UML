package org.example.scdpro2.ui.views;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

public class RelationshipLine {
    public enum RelationshipType {
        ASSOCIATION, AGGREGATION, COMPOSITION, INHERITANCE
    }

    private Line line;
    private Shape endIndicator;
    private RelationshipType type;

    public RelationshipLine(double startX, double startY, double endX, double endY, RelationshipType type) {
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
}
