package org.example.scdpro2.ui.views.ClassDiagram;

import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;
import org.example.scdpro2.business.models.BClassDiagarm.BClassBox;
import org.example.scdpro2.ui.views.MainView;

public class RelationshipLine extends Group {

    public enum RelationshipType {
        ASSOCIATION, AGGREGATION, COMPOSITION, INHERITANCE
    }
    private ClassBox target;
    private ClassBox source;
    private boolean isSelected;
    private static int linenumber=1;

    private Label relationshipLabel; // For the title
    private Label sourceMultiplicity; // Multiplicity for source end
    private Label targetMultiplicity; // Multiplicity for target end
    private MainView mainView;

    private Line line;
    private Shape endIndicator;
    private RelationshipType type;

    private BClassBox sourceDiagram; // Source class diagram
    private BClassBox targetDiagram; // Target class diagram


    private Polyline polyline;
    private int relationshipIndex;


    private Polyline clickOverlay; // Change to Polyline

    // Example corrected constructor
    public RelationshipLine(ClassBox source, String sourceSide, ClassBox target, String targetSide, RelationshipType type, int sourceOffsetIndex, int targetOffsetIndex, int relationshipIndex) {
        this.source = source;
        this.target = target;
        this.type = type;
        this.relationshipIndex = relationshipIndex;

        this.line = new Line();
        this.polyline = new Polyline();
        this.endIndicator = createEndIndicator(type); // Create the relationship indicator

        // Style the line
        this.line.setStrokeWidth(2);
        this.line.setStroke(Color.BLACK);

        // Add an overlay for mouse interactions
        this.clickOverlay = new Polyline();
        this.clickOverlay.setStrokeWidth(15);
        this.clickOverlay.setStroke(Color.TRANSPARENT);
        this.clickOverlay.setMouseTransparent(false);

        // Add listeners to update position dynamically
        source.addRelationship(this);
        target.addRelationship(this);
        source.layoutXProperty().addListener((obs, oldVal, newVal) -> updateRightAnglePath());
        source.layoutYProperty().addListener((obs, oldVal, newVal) -> updateRightAnglePath());
        target.layoutXProperty().addListener((obs, oldVal, newVal) -> updateRightAnglePath());
        target.layoutYProperty().addListener((obs, oldVal, newVal) -> updateRightAnglePath());

        this.relationshipLabel = new Label(" ");
        linenumber++;
        // Initialize multiplicity labels
        this.sourceMultiplicity = new Label(" ");
        this.targetMultiplicity = new Label(" ");

        // Add multiplicity labels and components to the group
        this.getChildren().addAll(clickOverlay, polyline, line, sourceMultiplicity, targetMultiplicity,relationshipLabel);
        if (endIndicator != null) {
            this.getChildren().add(endIndicator);
        }

        // Enable mouse events for selection and deletion
        setupMouseEvents();

        // Calculate initial path and position
        updateRightAnglePath();
    }

    public String getRelationshipLabel() {
        return relationshipLabel.getText();
    }

    public ClassBox getSource() {
        return source;
    }

    public ClassBox getTarget() {
        return target;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public String getMultiplicityStart() {
        return sourceMultiplicity.getText();
    }

    public void setMultiplicityStart(String text) {
        sourceMultiplicity.setText(text);
    }

    public String getMultiplicityEnd() {
        return targetMultiplicity.getText();
    }

    public void setMultiplicityEnd(String text) {
        targetMultiplicity.setText(text);
    }

    public void setTitle(String text) {
        relationshipLabel.setText(text);
    }

    public String getTitle() {
        return relationshipLabel.getText();
    }

    public void setMainView(MainView mainView) {
        this.mainView = mainView;
    }

    private void updateRightAnglePath() {
        // Ensure neither source nor target are null, and polyline is initialized
        if ((source == null) || (target == null) || polyline == null)
            return;

        double offset = 40 * relationshipIndex; // Offset for multiple relationships

        // Initialize coordinates
        double sourceX, sourceY, sourceWidth, sourceHeight;
        double targetX, targetY, targetWidth, targetHeight;

        // Use source or sourceinterface depending on availability
        sourceX = source.getLayoutX();
        sourceY = source.getLayoutY();
        sourceWidth = source.getWidth();
        sourceHeight = source.getHeight();

        // Use target or targetinterface depending on availability
        targetX = target.getLayoutX();
        targetY = target.getLayoutY();
        targetWidth = target.getWidth();
        targetHeight = target.getHeight();

        // Determine connection direction (horizontal or vertical)
        double startX, startY, endX, endY, bendX, bendY;

        if (Math.abs(targetX - sourceX) > Math.abs(targetY - sourceY)) {
            // Horizontal connection
            startX = sourceX + (targetX > sourceX ? sourceWidth : 0); // Right or Left edge
            startY = sourceY + sourceHeight / 2 + offset; // Vertically centered with offset
            bendX = targetX + (targetX > sourceX ? 0 : targetWidth); // Left or Right edge
            bendY = startY; // Y remains the same for a horizontal line
            endX = bendX; // End X is the same as bend
            endY = targetY + targetHeight / 2 + offset; // Vertically centered with offset
        } else {
            // Vertical connection
            startX = sourceX + sourceWidth / 2 + offset; // Horizontally centered with offset
            startY = sourceY + (targetY > sourceY ? sourceHeight : 0); // Bottom or Top edge
            bendX = startX; // X remains the same for vertical connection
            bendY = targetY + (targetY > sourceY ? 0 : targetHeight); // Top or Bottom edge
            endX = targetX + targetWidth / 2 + offset; // Horizontally centered with offset
            endY = bendY; // End Y is the same as bend
        }

        // Update polyline path (with bend)
        polyline.getPoints().clear();
        polyline.getPoints().addAll(startX, startY, bendX, bendY, endX, endY);

        // Update the end indicator position and rotation
        if (endIndicator instanceof Polygon polygon) {
            double[] centeredPosition = centerEndIndicator(endX, endY, startX, startY);
            polygon.setLayoutX(centeredPosition[0]);
            polygon.setLayoutY(centeredPosition[1]);
            polygon.setRotate(calculateRotationAngle(startX, startY, endX, endY));
        }

        // Update visible line (this is the same path as polyline)
        line.setStartX(startX);
        line.setStartY(startY);
        line.setEndX(endX);
        line.setEndY(endY);

        // Update the clickOverlay to match the main line path
        clickOverlay.getPoints().setAll(startX, startY, bendX, bendY, endX, endY);
        clickOverlay.setStroke(Color.TRANSPARENT);
        line.setStroke(Color.TRANSPARENT);

        // Update relationship label position based on polyline
        if (relationshipLabel != null) {
            // Calculate the midpoints of the polyline segments
            double midX = (startX + bendX) / 2;
            double midY = (startY + bendY) / 2;

            // Calculate the label's position
            relationshipLabel.setLayoutX(midX - relationshipLabel.getWidth() / 2); // Center horizontally
            relationshipLabel.setLayoutY(midY - relationshipLabel.getHeight() / 2); // Center vertically

            // Adjust for if polyline is mostly horizontal or vertical
            if (Math.abs(startX - endX) > Math.abs(startY - endY)) {
                // If the polyline is more horizontal, position label near the middle segment
                relationshipLabel.setLayoutY(midY - 10); // Slightly above the middle of the polyline
                // Set rotation to 0 for horizontal
                relationshipLabel.setRotate(0);
            } else {
                // If polyline is vertical, position label slightly right of the line
                relationshipLabel.setLayoutX(midX + 10); // Slightly to the right
                // Set rotation to 180 for vertical (rotate 180 degrees)
                relationshipLabel.setRotate(90);
            }
        }

        // Update the position of multiplicity labels
        if (sourceMultiplicity != null) {
            sourceMultiplicity.setLayoutX(startX - 15);  // Adjust X offset for better placement
            sourceMultiplicity.setLayoutY(startY - 0);  // Adjust Y offset for better placement
        }

        if (targetMultiplicity != null) {
            targetMultiplicity.setLayoutX(endX + 5);  // Adjust X offset for better placement
            targetMultiplicity.setLayoutY(endY - 20);  // Adjust Y offset for better placement
        }
    }


    private double[] centerEndIndicator(double endX, double endY, double startX, double startY) {
        double angle = Math.atan2(endY - startY, endX - startX);
        double distance = 10; // Adjust based on the size of your diamond or triangle
        return new double[]{
                endX - distance * Math.cos(angle), // Center X position
                endY - distance * Math.sin(angle)  // Center Y position
        };
    }

    private void setupMouseEvents() {
        // Clickable overlay for selection
        clickOverlay.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                isSelected = !isSelected;
                updateSelectionStyle();
                mainView.handleSelection(this);
            }
            event.consume();
        });

        // Right-click for deletion
        clickOverlay.setOnContextMenuRequested(event -> {
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
            polyline.setStroke(Color.RED); // Highlight in red when selected
        } else {
            polyline.setStroke(Color.BLACK); // Default to black
        }
    }

    private Shape createEndIndicator(RelationshipType type) {
        switch (type) {
            case INHERITANCE: // Triangle arrowhead
                Polygon triangle = new Polygon();
                triangle.getPoints().addAll(
                        -10.0, -5.0,  // Left base corner
                        -10.0, 5.0,   // Right base corner
                        0.0, 0.0      // Tip of the triangle
                );
                triangle.setFill(Color.WHITE);
                triangle.setStroke(Color.BLACK);
                return triangle;

            case AGGREGATION: // Diamond
                Polygon diamond = new Polygon();
                diamond.getPoints().addAll(
                        0.0, 0.0,    // Center
                        -10.0, -5.0, // Top-left
                        -20.0, 0.0,  // Middle-left
                        -10.0, 5.0   // Bottom-left
                );
                diamond.setFill(Color.WHITE);
                diamond.setStroke(Color.BLACK);
                return diamond;

            case COMPOSITION: // Filled diamond
                Polygon filledDiamond = new Polygon();
                filledDiamond.getPoints().addAll(
                        0.0, 0.0,    // Center
                        -10.0, -5.0, // Top-left
                        -20.0, 0.0,  // Middle-left
                        -10.0, 5.0   // Bottom-left
                );
                filledDiamond.setFill(Color.BLACK);
                return filledDiamond;

            default:
                return null;
        }
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

    public BClassBox getSourceDiagram() {
        return sourceDiagram;
    }

    public BClassBox getTargetDiagram() {
        return targetDiagram;
    }

    public RelationshipType getType() {
        return type;
    }

    public boolean isConnectedTo(ClassBox classBox) {
        return sourceDiagram.equals(classBox.getClassDiagram()) || targetDiagram.equals(classBox.getClassDiagram());
    }

}
