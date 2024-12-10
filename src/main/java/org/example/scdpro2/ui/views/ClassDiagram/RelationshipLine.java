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
/**
 * Represents a relationship line between two class boxes in a class diagram.
 * The line may be one of several types, such as association, aggregation, composition, or inheritance.
 */
public class RelationshipLine extends Group {
    /**
     * Enum for different types of relationships between class boxes.
     */
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

    /**
     * Constructs a RelationshipLine with the given source and target class boxes,
     * relationship type, and other associated properties.
     *
     * @param source          The source class box
     * @param sourceSide      The side of the source class box
     * @param target          The target class box
     * @param targetSide      The side of the target class box
     * @param type            The type of the relationship
     * @param sourceOffsetIndex   The offset index for the source
     * @param targetOffsetIndex   The offset index for the target
     * @param relationshipIndex    The index of the relationship (for multiple relationships)
     */
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
    /**
     * Returns the label of the relationship.
     *
     * @return The relationship label
     */
    public String getRelationshipLabel() {
        return relationshipLabel.getText();
    }
    /**
     * Returns the source class box of the relationship.
     *
     * @return The source class box
     */
    public ClassBox getSource() {
        return source;
    }
    /**
     * Returns the target class box of the relationship.
     *
     * @return The target class box
     */
    public ClassBox getTarget() {
        return target;
    }
    /**
     * Returns the polyline representing the path of the relationship.
     *
     * @return The polyline
     */
    public Polyline getPolyline() {
        return polyline;
    }
    /**
     * Returns the multiplicity text for the start (source) end of the relationship.
     *
     * @return The multiplicity for the start end
     */
    public String getMultiplicityStart() {
        return sourceMultiplicity.getText();
    }
    /**
     * Sets the multiplicity text for the start (source) end of the relationship.
     *
     * @param text The multiplicity text for the start end
     */
    public void setMultiplicityStart(String text) {
        sourceMultiplicity.setText(text);
    }
    /**
     * Returns the multiplicity text for the end (target) of the relationship.
     *
     * @return The multiplicity for the end
     */
    public String getMultiplicityEnd() {
        return targetMultiplicity.getText();
    }
    /**
     * Sets the multiplicity text for the end (target) of the relationship.
     *
     * @param text The multiplicity text for the end
     */
    public void setMultiplicityEnd(String text) {
        targetMultiplicity.setText(text);
    }
    /**
     * Sets the title text for the relationship.
     *
     * @param text The title text
     */
    public void setTitle(String text) {
        relationshipLabel.setText(text);
    }
    /**
     * Returns the title text of the relationship.
     *
     * @return The relationship title
     */
    public String getTitle() {
        return relationshipLabel.getText();
    }
    /**
     * Sets the main view of the relationship line for mouse interaction handling.
     *
     * @param mainView The main view
     */
    public void setMainView(MainView mainView) {
        this.mainView = mainView;
    }
    /**
     * Updates the path of the relationship line, including the polyline, line, and position of labels.
     */
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

    /**
     * Centers the end indicator based on the start and end coordinates.
     *
     * @param endX  The end X coordinate
     * @param endY  The end Y coordinate
     * @param startX The start X coordinate
     * @param startY The start Y coordinate
     * @return The centered position of the end indicator as an array [X, Y]
     */
    private double[] centerEndIndicator(double endX, double endY, double startX, double startY) {
        double angle = Math.atan2(endY - startY, endX - startX);
        double distance = 10; // Adjust based on the size of your diamond or triangle
        return new double[]{
                endX - distance * Math.cos(angle), // Center X position
                endY - distance * Math.sin(angle)  // Center Y position
        };
    }
    /**
     * Sets up the mouse events for selection and deletion of the relationship line.
     */
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

    /**
     * Deletes the relationship line from the parent pane and updates the class boxes.
     */
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
    /**
     * Updates the selection style of the relationship line, highlighting it when selected.
     */
    private void updateSelectionStyle() {
        if (isSelected) {
            polyline.setStroke(Color.RED); // Highlight in red when selected
        } else {
            polyline.setStroke(Color.BLACK); // Default to black
        }
    }
    /**
     * Creates the end indicator shape for the relationship line based on the relationship type.
     *
     * @param type The type of relationship (e.g., inheritance, aggregation, etc.)
     * @return The shape used as the end indicator
     */
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
    /**
     * Calculates the rotation angle for the end indicator based on the start and end points.
     *
     * @param startX The start X coordinate
     * @param startY The start Y coordinate
     * @param endX   The end X coordinate
     * @param endY   The end Y coordinate
     * @return The rotation angle in degrees
     */
    private double calculateRotationAngle(double startX, double startY, double endX, double endY) {
        return Math.toDegrees(Math.atan2(endY - startY, endX - startX));
    }
    /**
     * Returns the line representing the relationship path.
     *
     * @return The relationship line
     */
    public Line getLine() {
        return line;
    }
    /**
     * Returns the end indicator shape of the relationship.
     *
     * @return The end indicator shape
     */
    public Shape getEndIndicator() {
        return endIndicator;
    }
    /**
     * Returns the source class diagram of the relationship.
     *
     * @return The source class diagram
     */
    public BClassBox getSourceDiagram() {
        return sourceDiagram;
    }
    /**
     * Returns the target class diagram of the relationship.
     *
     * @return The target class diagram
     */
    public BClassBox getTargetDiagram() {
        return targetDiagram;
    }
    /**
     * Returns the type of the relationship.
     *
     * @return The relationship type
     */
    public RelationshipType getType() {
        return type;
    }
    /**
     * Checks if the relationship is connected to a given class box.
     *
     * @param classBox The class box to check
     * @return True if connected to the class box, false otherwise
     */
    public boolean isConnectedTo(ClassBox classBox) {
        return sourceDiagram.equals(classBox.getClassDiagram()) || targetDiagram.equals(classBox.getClassDiagram());
    }



}
