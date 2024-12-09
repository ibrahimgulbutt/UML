package org.example.scdpro2.ui.views.PackageDiagram;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.example.scdpro2.business.models.BPackageDiagarm.PackageClassComponent;
import org.example.scdpro2.business.models.BPackageDiagarm.PackageComponent;
import org.example.scdpro2.ui.controllers.MainController;

import java.util.ArrayList;
import java.util.List;
/**
 * Represents a visual representation of a package in the package diagram.
 * The PackageBox contains a name label, a top rectangle, and class boxes.
 * It supports features such as dragging, resizing, renaming, and adding/removing class boxes.
 */
public class PackageBox extends BorderPane  {
    public PackageComponent packageComponent;
    public final MainController controller;
    private final PackageDiagramPane diagramPane;

    private double offsetX, offsetY; // For dragging
    private double initialWidth, initialHeight; // For resizing
    private double initialMouseX, initialMouseY; // For resizing

    private boolean isResizing = false; // Tracks resizing state
    private String resizeDirection = ""; // Tracks the direction of resizing

    private final VBox contentBox = new VBox(); // Holds the package's content
    private final Label nameLabel = new Label(); // Displays package name
    private final Rectangle topRectangle = new Rectangle(); // Small rectangle on top
    private ArrayList<PackageClassBox> packageClassBoxes;
    /**
     * Constructs a PackageBox.
     *
     * @param packageComponent The underlying package component.
     * @param controller        The main controller.
     * @param diagramPane       The diagram pane where this PackageBox will be displayed.
     */
    public PackageBox(PackageComponent packageComponent, MainController controller, PackageDiagramPane diagramPane) {
        this.packageComponent = packageComponent;
        this.controller = controller;
        this.diagramPane = diagramPane;

        packageClassBoxes=new ArrayList<>();
        // Set up the content area
        contentBox.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: #cce5ff;");
        contentBox.setAlignment(Pos.TOP_CENTER);

        nameLabel.setText(packageComponent.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        contentBox.getChildren().add(nameLabel);

        // Set initial size
        setPrefWidth(packageComponent.getWidth());
        setPrefHeight(packageComponent.getHeight());

        // Set up the top rectangle
        topRectangle.setHeight(20); // Small height for the rectangle
        topRectangle.setWidth(getPrefWidth() * 0.45); // Set width to 45% of the PackageBox width
        topRectangle.setStyle("-fx-padding: 10; -fx-background-color: #cce5ff;"); // Set a color for the rectangle
        setTop(topRectangle); // Add it to the top of the BorderPane

        // Add content to the center of the BorderPane
        setCenter(contentBox);

        // Add mouse event handlers
        setOnMousePressed(this::handleMousePressed);
        setOnMouseDragged(this::handleMouseDragged);
        setOnMouseReleased(this::handleMouseReleased);

        // Configure cursor changes for resizing regions
        setOnMouseMoved(this::updateCursor);

        // Enable name editing
        setupNameEditing();

        // Right-click context menu
        ContextMenu contextMenu = new ContextMenu();
        MenuItem addClassBoxMenuItem = new MenuItem("Add Class Box");
        addClassBoxMenuItem.setOnAction(e -> addClassBox());
        contextMenu.getItems().add(addClassBoxMenuItem);

        // Add options to the context menu
        MenuItem deletePackageMenuItem = new MenuItem("Delete Package Box");
        deletePackageMenuItem.setOnAction(e -> deletePackage());
        contextMenu.getItems().add(deletePackageMenuItem);

        // Show context menu on right-click
        setOnContextMenuRequested(event -> {
            System.out.println("Context menu requested");
            contextMenu.show(this, event.getScreenX(), event.getScreenY());
        });

    }
    /**
     * Sets the ID of the underlying PackageComponent.
     *
     * @param id The new ID for the package.
     */
    public void setPackageComponentid(String id)
    {
        this.packageComponent.setName(id);
    }
    /**
     * Enables editing of the package's name by clicking on the label.
     */
    private void setupNameEditing() {
        nameLabel.setOnMouseClicked(event -> {
            TextField nameField = new TextField(packageComponent.getName());
            nameField.setOnAction(e -> {
                String newName = nameField.getText().trim();
                if (!newName.isEmpty()) {
                    // Update model and UI
                    packageComponent.setName(newName);
                    nameLabel.setText(newName);
                    contentBox.getChildren().remove(nameField);
                    contentBox.getChildren().add(0, nameLabel);
                    controller.getmainview().addClassToList(newName);
                }
            });
            contentBox.getChildren().remove(nameLabel);
            contentBox.getChildren().add(0, nameField);
            nameField.requestFocus();
        });
    }


    private boolean confirmAction(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setTitle(title);
        alert.setHeaderText(null);
        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }
    /**
     * Moves all class boxes associated with this PackageBox.
     *
     * @param deltaX The change in X-coordinate.
     * @param deltaY The change in Y-coordinate.
     */
    public void moveClassBoxes(double deltaX, double deltaY) {
        for (var node : diagramPane.getChildren()) {
            if (node instanceof PackageClassBox classBox && classBox.getParentPackageBox() == this) {
                classBox.setLayoutX(classBox.getLayoutX() + deltaX);
                classBox.setLayoutY(classBox.getLayoutY() + deltaY);
            }
        }
    }
    /**
     * Adds a new class box to this package.
     */
    public void addClassBox() {
        // Create a new instance of PackageClassBox
        PackageClassBox classBox = controller.addPackageClassBox(diagramPane,this,packageComponent);

        // Set the default size of the class box
        classBox.setPrefWidth(100); // Set width to 100 pixels (default size)
        classBox.setPrefHeight(80); // Set height to 80 pixels (default size)

        // Calculate the new position of the class box, ensuring it stays within the bounds of the PackageBox
        double newLayoutX = getLayoutX() + 10; // Offset for visibility
        double newLayoutY = getLayoutY() + 10;

        // Ensure the new layoutX and layoutY are within the bounds of the PackageBox
        double maxX = getLayoutX() + getPrefWidth() - classBox.getPrefWidth();
        double maxY = getLayoutY() + getPrefHeight() - classBox.getPrefHeight();
        if (newLayoutX > maxX) {
            newLayoutX = maxX; // Restrict to the right boundary
        }
        if (newLayoutY > maxY) {
            newLayoutY = maxY; // Restrict to the bottom boundary
        }

        classBox.setLayoutX(newLayoutX);
        classBox.setLayoutY(newLayoutY);

        packageClassBoxes.add(classBox);
        // Add the class box to the diagram pane
        diagramPane.addClassBox(classBox,this);
    }
    /**
     * Loads an existing class box into this package.
     *
     * @param pcc The class component to load.
     */
    public void addClassBoxforload(PackageClassComponent pcc,double x,double y,double w,String id)
    {
        // Create a new instance of PackageClassBox
        PackageClassBox classBox = controller.addPackageClassBox(diagramPane,this,packageComponent,pcc);

        classBox.setId(id);

        // Set the default size of the class box
        classBox.setPrefWidth(w); // Set width to 100 pixels (default size)
        classBox.setPrefHeight(80); // Set height to 80 pixels (default size)

        // Calculate the new position of the class box, ensuring it stays within the bounds of the PackageBox
        double newLayoutX = x + 10; // Offset for visibility
        double newLayoutY = y + 10;

        // Ensure the new layoutX and layoutY are within the bounds of the PackageBox
        double maxX = getLayoutX() + getPrefWidth() - classBox.getPrefWidth();
        double maxY = getLayoutY() + getPrefHeight() - classBox.getPrefHeight();
        if (newLayoutX > maxX) {
            newLayoutX = maxX; // Restrict to the right boundary
        }
        if (newLayoutY > maxY) {
            newLayoutY = maxY; // Restrict to the bottom boundary
        }

        classBox.setLayoutX(newLayoutX);
        classBox.setLayoutY(newLayoutY);

        packageClassBoxes.add(classBox);
        // Add the class box to the diagram pane
        diagramPane.addClassBox(classBox,this);
    }
    /**
     * Deletes this PackageBox and all associated relationships and class boxes.
     */
    private void deletePackage() {
        if (confirmAction("Delete Package Box", "Are you sure you want to delete this package box and all its relationships?")) {
            List<PackageRelationship> relationshipsToRemove = new ArrayList<>();

            // Collect relationships involving this package
            for (PackageRelationship relationship : diagramPane.getRelationships()) {
                if (relationship.getStartPackage() == this || relationship.getEndPackage() == this) {
                    relationshipsToRemove.add(relationship);
                }
            }

            // Remove the collected relationships
            for (PackageRelationship relationship : relationshipsToRemove) {
                diagramPane.removeRelationship(relationship);
            }

            // Remove all associated PackageClassBoxes from the diagram pane
            for (PackageClassBox classBox : packageClassBoxes) {
                diagramPane.getChildren().remove(classBox); // Remove the class box from the diagram pane
            }

            // Remove the PackageComponent from the business layer (activePackageDiagram)
            PackageComponent packageComponent = this.getPackageComponent();
            diagramPane.removePackageComponent(packageComponent);

            // Finally, remove the PackageBox itself from the diagram pane
            diagramPane.getChildren().remove(this);
        }
    }



    // drag and resize functions
    /**
     * Handles the mouse released event, resetting the resizing state.
     *
     * @param event the MouseEvent triggered when the mouse is released.
     */
    private void handleMousePressed(MouseEvent event) {
        if (isOnEdge(event)) {
            // Start resizing
            isResizing = true;
            initialWidth = getPrefWidth();
            initialHeight = getPrefHeight();
            initialMouseX = event.getSceneX();
            initialMouseY = event.getSceneY();
        } else {
            // Start dragging
            isResizing = false;
            offsetX = event.getSceneX() - getLayoutX();
            offsetY = event.getSceneY() - getLayoutY();
        }
    }

    private void handleMouseDragged(MouseEvent event) {
        if (isResizing) {
            resize(event);
        } else {
            double newX = event.getSceneX() - offsetX;
            double newY = event.getSceneY() - offsetY;

            double deltaX = newX - getLayoutX();
            double deltaY = newY - getLayoutY();

            setLayoutX(newX);
            setLayoutY(newY);

            // Move all child class boxes
            moveClassBoxes(deltaX, deltaY);
        }
    }

    private boolean isOnEdge(MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();
        double width = getWidth();
        double height = getHeight();
        double edgeThreshold = 10;

        if (mouseX <= edgeThreshold && mouseY <= edgeThreshold) {
            resizeDirection = "TOP_LEFT";
            return true;
        } else if (mouseX >= width - edgeThreshold && mouseY >= height - edgeThreshold) {
            resizeDirection = "BOTTOM_RIGHT";
            return true;
        } else if (mouseX >= width - edgeThreshold) {
            resizeDirection = "RIGHT";
            return true;
        } else if (mouseY >= height - edgeThreshold) {
            resizeDirection = "BOTTOM";
            return true;
        } else if (mouseX <= edgeThreshold) {
            resizeDirection = "LEFT";
            return true;
        } else if (mouseY <= edgeThreshold) {
            resizeDirection = "TOP";
            return true;
        } else {
            resizeDirection = "";
            return false;
        }
    }

    public void updateBox(double x, double y, double width, double height) {
        // Update the position of the PackageBox
        setLayoutX(x);
        setLayoutY(y);

        // Update the size of the PackageBox
        setPrefWidth(width);
        setPrefHeight(height);

        // Update the size of the top rectangle
        topRectangle.setWidth(width * 0.45); // Adjust top rectangle width accordingly

        // Update the width and height in the PackageComponent model as well
        packageComponent.setWidth(width);
        packageComponent.setHeight(height);

        // If needed, adjust the layout of any child elements (e.g., PackageClassBox) based on the new size
        moveClassBoxes(0, 0); // Optionally, this can be adjusted if you want to move the child boxes as well
    }


    private void resize(MouseEvent event) {
        double deltaX = event.getSceneX() - initialMouseX;
        double deltaY = event.getSceneY() - initialMouseY;

        if (resizeDirection.contains("RIGHT")) {
            double newWidth = initialWidth + deltaX;
            if (newWidth >= 100) {
                setPrefWidth(newWidth);
                topRectangle.setWidth(newWidth * 0.45); // Update top rectangle width as well
                //packageComponent.setWidth(newWidth);
            }
        }

        if (resizeDirection.contains("BOTTOM")) {
            double newHeight = initialHeight + deltaY;
            if (newHeight >= 100) {
                setPrefHeight(newHeight);

            }
        }

        if (resizeDirection.contains("LEFT")) {
            double newWidth = initialWidth - deltaX;
            if (newWidth >= 100) {
                setPrefWidth(newWidth);
                setLayoutX(getLayoutX() + deltaX);
                topRectangle.setWidth(newWidth * 0.45); // Update top rectangle width
            }
        }

        if (resizeDirection.contains("TOP")) {
            double newHeight = initialHeight - deltaY;
            if (newHeight >= 100) {
                setPrefHeight(newHeight);
                setLayoutY(getLayoutY() + deltaY);
            }
        }
    }

    private void updateCursor(MouseEvent event) {
        if (isOnEdge(event)) {
            switch (resizeDirection) {
                case "RIGHT":
                    setCursor(Cursor.E_RESIZE);
                    break;
                case "BOTTOM":
                    setCursor(Cursor.S_RESIZE);
                    break;
                case "BOTTOM_RIGHT":
                    setCursor(Cursor.SE_RESIZE);
                    break;
                case "LEFT":
                    setCursor(Cursor.W_RESIZE);
                    break;
                case "TOP":
                    setCursor(Cursor.N_RESIZE);
                    break;
                case "TOP_LEFT":
                    setCursor(Cursor.NW_RESIZE);
                    break;
                default:
                    setCursor(Cursor.DEFAULT);
                    break;
            }
        } else {
            setCursor(Cursor.MOVE);
        }
    }

    private void handleMouseReleased(MouseEvent event) {
        isResizing = false;
        resizeDirection = "";
    }


    // setter and getters
    /**
     * Gets the diagram pane this PackageBox belongs to.
     *
     * @return the PackageDiagramPane containing this PackageBox.
     */
    public PackageDiagramPane getDiagramPane() {
        return diagramPane;
    }
    /**
     * Gets the PackageComponent represented by this PackageBox.
     *
     * @return the PackageComponent instance.
     */
    public PackageComponent getPackageComponent() {
        return packageComponent;
    }


    public PackageComponent getPackageDiagram() {
        return packageComponent;
    }

    /**
     * Gets the name of the PackageComponent.
     *
     * @return the name of the PackageComponent.
     */
    public String getName() {
        return nameLabel.getText();
    }
    /**
     * Sets the name of the PackageComponent and updates the UI.
     *
     * @param name the new name for the PackageComponent.
     */
    public void setName(String name) {
        this.nameLabel.setText(name);
    }

    /**
     * Gets the list of PackageClassBoxes contained within this PackageBox.
     *
     * @return an ArrayList of PackageClassBoxes.
     */
    public ArrayList<PackageClassBox> getPackageClassBoxes()
    {
        return packageClassBoxes;
    }
}