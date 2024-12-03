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
    private InterfaceBox targetinterface;
    private InterfaceBox sourceinterface;
    private boolean isSelected;
    private static int linenumber=1;

    private Label relationshipLabel; // For the title
    private Label sourceMultiplicity; // Multiplicity for source end
    private Label targetMultiplicity; // Multiplicity for target end
    private MainView mainView;

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

    public enum RelationshipType {
        ASSOCIATION, AGGREGATION, COMPOSITION, INHERITANCE
    }

    private Line line;
    private Shape endIndicator;
    private RelationshipType type;

    private ClassDiagram sourceDiagram; // Source class diagram
    private ClassDiagram targetDiagram; // Target class diagram

    private PackageComponent sourcePackage;
    private PackageComponent targetPackage;

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

        this.relationshipLabel = new Label("Line "+linenumber);
        linenumber++;
        // Initialize multiplicity labels
        this.sourceMultiplicity = new Label("1");
        this.targetMultiplicity = new Label("2");

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


    public RelationshipLine(InterfaceBox source, String sourceSide, ClassBox target, String targetSide, RelationshipType type, int sourceOffsetIndex, int targetOffsetIndex, int relationshipIndex) {
        this.sourceinterface = source;
        this.target = target;
        this.type = type;
        this.relationshipIndex = relationshipIndex;

        this.line = new Line();
        this.polyline = new Polyline();
        this.endIndicator = createEndIndicator(type); // Create the relationship indicator

        // Style the line (visible part)
        this.line.setStrokeWidth(2); // Keep visible line width consistent
        this.line.setStroke(Color.BLACK);

        // Add an overlay for mouse interactions
        this.clickOverlay = new Polyline();
        this.clickOverlay.setStrokeWidth(15); // Larger hit area for easier selection
        this.clickOverlay.setStroke(Color.TRANSPARENT); // Invisible but interactive
        this.clickOverlay.setMouseTransparent(false); // Enable mouse events

        // Add listeners to update position dynamically
        source.addRelationship(this);
        target.addRelationship(this);
        source.layoutXProperty().addListener((obs, oldVal, newVal) -> updateRightAnglePath());
        source.layoutYProperty().addListener((obs, oldVal, newVal) -> updateRightAnglePath());
        target.layoutXProperty().addListener((obs, oldVal, newVal) -> updateRightAnglePath());
        target.layoutYProperty().addListener((obs, oldVal, newVal) -> updateRightAnglePath());

        // Calculate initial path and position
        updateRightAnglePath();

        // Add components to the Group
        this.getChildren().addAll(clickOverlay, polyline, line);
        if (endIndicator != null) {
            this.getChildren().add(endIndicator); // Add the shape to the UI
        }

        // Enable mouse events for selection and deletion
        setupMouseEvents();
    }

    public RelationshipLine(ClassBox source, String sourceSide, InterfaceBox target, String targetSide, RelationshipType type, int sourceOffsetIndex, int targetOffsetIndex, int relationshipIndex) {
        this.source = source;
        this.targetinterface = target;
        this.type = type;
        this.relationshipIndex = relationshipIndex;

        this.line = new Line();
        this.polyline = new Polyline();
        this.endIndicator = createEndIndicator(type); // Create the relationship indicator

        // Style the line (visible part)
        this.line.setStrokeWidth(2); // Keep visible line width consistent
        this.line.setStroke(Color.BLACK);

        // Add an overlay for mouse interactions
        this.clickOverlay = new Polyline();
        this.clickOverlay.setStrokeWidth(15); // Larger hit area for easier selection
        this.clickOverlay.setStroke(Color.TRANSPARENT); // Invisible but interactive
        this.clickOverlay.setMouseTransparent(false); // Enable mouse events

        // Add listeners to update position dynamically
        source.addRelationship(this);
        target.addRelationship(this);
        source.layoutXProperty().addListener((obs, oldVal, newVal) -> updateRightAnglePath());
        source.layoutYProperty().addListener((obs, oldVal, newVal) -> updateRightAnglePath());
        target.layoutXProperty().addListener((obs, oldVal, newVal) -> updateRightAnglePath());
        target.layoutYProperty().addListener((obs, oldVal, newVal) -> updateRightAnglePath());

        // Calculate initial path and position
        updateRightAnglePath();

        // Add components to the Group
        this.getChildren().addAll(clickOverlay, polyline, line);
        if (endIndicator != null) {
            this.getChildren().add(endIndicator); // Add the shape to the UI
        }

        // Enable mouse events for selection and deletion
        setupMouseEvents();
    }

    public void setMainView(MainView mainView) {
        this.mainView = mainView;
    }

    private void updateRightAnglePath() {
        if ((source == null && sourceinterface == null) || (target == null && targetinterface == null) || polyline == null)
            return;

        double offset = 20 * relationshipIndex; // Offset for multiple relationships

        // Determine source and target coordinates based on type
        double sourceX, sourceY, sourceWidth, sourceHeight;
        double targetX, targetY, targetWidth, targetHeight;

        double startX, startY, endX, endY;

        if (source != null) {
            sourceX = source.getLayoutX();
            sourceY = source.getLayoutY();
            sourceWidth = source.getWidth();
            sourceHeight = source.getHeight();
        } else {
            sourceX = sourceinterface.getLayoutX();
            sourceY = sourceinterface.getLayoutY();
            sourceWidth = sourceinterface.getWidth();
            sourceHeight = sourceinterface.getHeight();
        }

        if (target != null) {
            targetX = target.getLayoutX();
            targetY = target.getLayoutY();
            targetWidth = target.getWidth();
            targetHeight = target.getHeight();
        } else {
            targetX = targetinterface.getLayoutX();
            targetY = targetinterface.getLayoutY();
            targetWidth = targetinterface.getWidth();
            targetHeight = targetinterface.getHeight();
        }


        if (Math.abs(targetX - sourceX) > Math.abs(targetY - sourceY)) {
            // Horizontal connection
            startX = sourceX + (targetX > sourceX ? sourceWidth : 0); // Right or Left edge
            startY = sourceY + sourceHeight / 2 + offset; // Vertically centered with offset
            endX = targetX + (targetX > sourceX ? 0 : targetWidth); // Left or Right edge
            endY = targetY + targetHeight / 2 + offset; // Vertically centered with offset
        } else {
            // Vertical connection
            startX = sourceX + sourceWidth / 2 + offset; // Horizontally centered with offset
            startY = sourceY + (targetY > sourceY ? sourceHeight : 0); // Bottom or Top edge
            endX = targetX + targetWidth / 2 + offset; // Horizontally centered with offset
            endY = targetY + (targetY > sourceY ? 0 : targetHeight); // Top or Bottom edge
        }

        // Update polyline path
        polyline.getPoints().clear();
        polyline.getPoints().addAll(startX, startY, startX, endY, endX, endY);

        // Update the end indicator position and rotation
        if (endIndicator instanceof Polygon polygon) {
            double[] centeredPosition = centerEndIndicator(endX, endY, startX, startY);
            polygon.setLayoutX(centeredPosition[0]);
            polygon.setLayoutY(centeredPosition[1]);
            polygon.setRotate(calculateRotationAngle(startX, startY, endX, endY));
        }

        // Update visible line
        line.setStartX(startX);
        line.setStartY(startY);
        line.setEndX(endX);
        line.setEndY(endY);


        double midX = startX;
        double midY = endY;

        // Update the main line (or polyline)
        polyline.getPoints().setAll(startX, startY, midX, midY, endX, endY);

        // Update the clickOverlay to match the main line path
        clickOverlay.getPoints().setAll(startX, startY, midX, midY, endX, endY);
        clickOverlay.setStroke(Color.TRANSPARENT);
        line.setStroke(Color.TRANSPARENT);

        // Update the position of multiplicity labels
        sourceMultiplicity.setLayoutX(startX - 15); // Adjust X offset for better placement
        sourceMultiplicity.setLayoutY(startY - 10); // Adjust Y offset for better placement

        targetMultiplicity.setLayoutX(endX + 5); // Adjust X offset for better placement
        targetMultiplicity.setLayoutY(endY - 10); // Adjust Y offset for better placement
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

}
