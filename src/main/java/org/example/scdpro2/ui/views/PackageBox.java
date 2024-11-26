package org.example.scdpro2.ui.views;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.example.scdpro2.business.models.PackageComponent;
import org.example.scdpro2.ui.controllers.MainController;

public class PackageBox extends VBox {
    private final PackageComponent packageComponent;
    private final MainController controller;
    private final PackageDiagramPane diagramPane;
    private double offsetX, offsetY;
    private final Rectangle resizeHandle = new Rectangle(10, 10); // Resize handle
    private final Label nameLabel = new Label(); // Displays package name
    private final VBox contentBox = new VBox(); // For attributes or sub-packages

    public PackageBox(PackageComponent packageComponent, MainController controller, PackageDiagramPane diagramPane) {
        this.packageComponent = packageComponent;
        this.controller = controller;
        this.diagramPane = diagramPane;

        // Initialize UI components
        setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: #cce5ff; -fx-border-radius: 5;");
        setPrefWidth(200);
        setPrefHeight(150);
        setAlignment(Pos.TOP_CENTER);

        nameLabel.setText(packageComponent.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        contentBox.setStyle("-fx-padding: 5; -fx-spacing: 5;");

        resizeHandle.setStyle("-fx-fill: grey;");
        resizeHandle.setCursor(Cursor.SE_RESIZE);
        resizeHandle.setOnMouseDragged(this::resize);

        // Add components to the PackageBox
        getChildren().addAll(nameLabel, contentBox, resizeHandle);

        // Enable dragging for repositioning
        setOnMousePressed(this::handleMousePressed);
        setOnMouseDragged(this::handleMouseDragged);

        // Enable name editing
        setupNameEditing();
    }

    private void setupNameEditing() {
        nameLabel.setOnMouseClicked(event -> {
            TextField nameField = new TextField(packageComponent.getName());
            nameField.setOnAction(e -> {
                String newName = nameField.getText().trim();
                if (!newName.isEmpty()) {
                    packageComponent.setName(newName);
                    nameLabel.setText(newName);
                    getChildren().remove(nameField); // Remove TextField
                    getChildren().add(0, nameLabel); // Re-add Label
                }
            });
            getChildren().remove(nameLabel); // Remove Label
            getChildren().add(0, nameField); // Add TextField
            nameField.requestFocus();
        });
    }

    private void handleMousePressed(MouseEvent event) {
        offsetX = event.getSceneX() - getLayoutX();
        offsetY = event.getSceneY() - getLayoutY();
    }

    private void handleMouseDragged(MouseEvent event) {
        setLayoutX(event.getSceneX() - offsetX);
        setLayoutY(event.getSceneY() - offsetY);
    }

    private void resize(MouseEvent event) {
        double newWidth = event.getX();
        double newHeight = event.getY();
        if (newWidth > getMinWidth()) {
            setPrefWidth(newWidth);
        }
        if (newHeight > getMinHeight()) {
            setPrefHeight(newHeight);
        }
    }

    // Add a method to add attributes or sub-components dynamically
    public void addComponent(String componentName) {
        Label componentLabel = new Label(componentName);
        componentLabel.setStyle("-fx-background-color: #e6e6e6; -fx-padding: 3; -fx-border-color: black; -fx-border-width: 1;");
        contentBox.getChildren().add(componentLabel);
    }

    public PackageComponent getPackageComponent() {
        return packageComponent;
    }
}
