package org.example.scdpro2.ui.views;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class RelationshipLine extends Line {

    public enum RelationshipType {
        ASSOCIATION, AGGREGATION, COMPOSITION, INHERITANCE
    }

    public RelationshipLine(double startX, double startY, double endX, double endY, RelationshipType type) {
        super(startX, startY, endX, endY);
        setStyle(type);
    }

    private void setStyle(RelationshipType type) {
        switch (type) {
            case ASSOCIATION:
                setStroke(Color.BLACK);
                getStrokeDashArray().addAll(10.0, 5.0);
                break;
            case AGGREGATION:
                setStroke(Color.BLUE);
                break;
            case COMPOSITION:
                setStroke(Color.GREEN);
                setStrokeWidth(2);
                break;
            case INHERITANCE:
                setStroke(Color.RED);
                setStrokeWidth(2);
                break;
        }
    }
}
