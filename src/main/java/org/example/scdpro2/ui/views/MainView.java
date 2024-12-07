package org.example.scdpro2.ui.views;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.stage.FileChooser;
import org.example.scdpro2.business.models.Diagram;
import org.example.scdpro2.ui.controllers.MainController;
import org.example.scdpro2.business.services.DiagramService;
import org.example.scdpro2.ui.views.RelationshipLine.RelationshipType;
import org.kordamp.bootstrapfx.BootstrapFX;
import org.example.scdpro2.business.models.Relationship;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainView extends BorderPane {
    private final MainController controller;

    public ClassDiagramPane classDiagramPane;
    public PackageDiagramPane packageDiagramPane;

    private ToggleButton relationshipModeToggle;
    private RelationshipType selectedRelationshipType; // Current selected relationship type

    private ClassBox sourceClassBox; // Temporarily holds the source ClassBox for relationship
    private boolean relationshipMode = false;

    private PackageBox sourcePackageBox; // To track the source for relationships

    public final ListView<String> classListView; // Dynamic list of class names
    private TreeView<String> projectExplorer;
    private VBox rightSideToolbar;


    public MainView(MainController controller, String diagramType) {
        this.controller = controller; // Use the passed controller directly

        this.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        this.getStylesheets().add(getClass().getResource("/org/example/scdpro2/styles/styles.css").toExternalForm());

        DiagramService diagramService = controller.getDiagramService();

        if ("Class Diagram".equals(diagramType)) {
            this.classDiagramPane = new ClassDiagramPane(this, controller, diagramService);
            setCenter(classDiagramPane); // Set ClassDiagramPane as the center
        } else if ("Package Diagram".equals(diagramType)) {
            this.packageDiagramPane = new PackageDiagramPane(this, controller, diagramService);
            setCenter(packageDiagramPane); // Set PackageDiagramPane as the center
        } else {
            throw new IllegalArgumentException("Invalid diagram type: " + diagramType);
        }

        classListView = new ListView<>(); // Initialize dynamic list
        classListView.setPrefWidth(200); // Optional: Set a preferred width

        // Initialize the projectExplorer
        this.projectExplorer = createProjectExplorer();

        MenuBar menuBar = createMenuBar();
        menuBar.getStyleClass().add("navbar");
        menuBar.getStyleClass().add("navbar-default");

        ToolBar toolbar = createToolbar();
        VBox classListPanel = createClassListPanel();
        VBox codeGenerationPanel = createCodeGenerationPanel();


        setTop(new VBox(menuBar, toolbar));
        setLeft(classListPanel);
        setRight(codeGenerationPanel);

        this.rightSideToolbar = createRightSideToolbar();
        setRight(rightSideToolbar);

    }

    // UI functions
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.getStyleClass().addAll("navbar", "navbar-light", "bg-light");

        Menu projectMenu = new Menu("Project");
        MenuItem saveProjectItem = new MenuItem("Save Project");
        saveProjectItem.setOnAction(e -> controller.saveProject());
        MenuItem loadProjectItem = new MenuItem("Load Project");
        loadProjectItem.setOnAction(e -> controller.loadProject());
        projectMenu.getItems().addAll(saveProjectItem, loadProjectItem);

        Menu diagramMenu = new Menu("Diagrams");
        MenuItem addClassDiagramItem = new MenuItem("New Class Diagram");
        addClassDiagramItem.setOnAction(e -> controller.addClassDiagram());
        MenuItem addPackageDiagramItem = new MenuItem("New Package Diagram");
        addPackageDiagramItem.setOnAction(e -> controller.addPackageDiagram());
        diagramMenu.getItems().addAll(addClassDiagramItem, addPackageDiagramItem);

        menuBar.getMenus().addAll(projectMenu, diagramMenu);
        return menuBar;
    }

    private TreeView<String> createProjectExplorer() {
        TreeItem<String> rootItem = new TreeItem<>("Project Explorer");
        rootItem.setExpanded(true);
        return new TreeView<>(rootItem);
    }

    private ToolBar createToolbar() {
        ToolBar toolbar = new ToolBar();
        toolbar.getStyleClass().add("custom-toolbar");

        // Create HBox for the sections (first, second, third)
        HBox firstSection = new HBox(10); // First section (left-aligned)
        HBox secondSection = new HBox(10); // Second section (center-aligned)
        HBox thirdSection = new HBox(10); // Third section (right-aligned)

        if (classDiagramPane != null) { // Class Diagram-specific toolbar

            // First Section (Left-aligned): Add Class and Add Interface buttons
            Button addClassButton = new Button("Add Class");
            addClassButton.getStyleClass().addAll("btn", "btn-primary");
            Tooltip.install(addClassButton, new Tooltip("Click to add a new class to the diagram."));
            addClassButton.setOnAction(event -> {
                controller.addClassBox(classDiagramPane);
                updateClassListView(); // Update the class list
            });

            Button addInterfaceButton = new Button("Add Interface");
            addInterfaceButton.getStyleClass().addAll("btn", "btn-secondary");
            addInterfaceButton.setOnAction(event -> {
                controller.addInterfaceBox(classDiagramPane);
                updateClassListView(); // Update the class list
            });

            firstSection.getChildren().addAll(addClassButton, addInterfaceButton);
            firstSection.setAlignment(Pos.CENTER_LEFT); // Left-align the first section

            // Second Section (Center-aligned): Relationship Mode Toggle and Relationship Type buttons
            relationshipModeToggle = new ToggleButton("Relationship Mode");
            relationshipModeToggle.getStyleClass().addAll("btn", "btn-outline-warning");
            relationshipModeToggle.setMinWidth(180);  // Set a fixed width to avoid resizing
            relationshipModeToggle.setMaxWidth(180);  // Ensure it doesn't stretch or shrink
            relationshipModeToggle.setPrefWidth(180); // Ensure consistent width
            relationshipModeToggle.setStyle("-fx-font-size: 14px;");  // Set a consistent font size

            relationshipModeToggle.setOnAction(event -> {
                boolean isActive = relationshipModeToggle.isSelected();
                classDiagramPane.setRelationshipModeEnabled(isActive);

                // Keep the text the same, no text change, just toggle button style
                relationshipModeToggle.getStyleClass().clear();
                relationshipModeToggle.getStyleClass().add("btn-outline-warning");

                if (isActive) {
                    relationshipModeToggle.getStyleClass().add("btn-warning");
                } else {
                    relationshipModeToggle.getStyleClass().add("btn-outline-warning");
                }

                // Maintain the consistent button width to prevent expansion
                relationshipModeToggle.setPrefWidth(180);
            });

            // Relationship Mode Toggle Button
            relationshipModeToggle = new ToggleButton("Relationship Mode");
            relationshipModeToggle.getStyleClass().addAll("btn", "btn-outline-warning");
            relationshipModeToggle.setMinWidth(180);  // Set a fixed width to avoid resizing
            relationshipModeToggle.setMaxWidth(180);  // Ensure it doesn't stretch or shrink
            relationshipModeToggle.setPrefWidth(180); // Ensure consistent width
            relationshipModeToggle.setStyle("-fx-font-size: 14px;");  // Set a consistent font size

            relationshipModeToggle.setOnAction(event -> {
                boolean isActive = relationshipModeToggle.isSelected();
                classDiagramPane.setRelationshipModeEnabled(isActive);

                // Toggle button style
                relationshipModeToggle.getStyleClass().clear();
                if (isActive) {
                    relationshipModeToggle.getStyleClass().add("btn-warning");  // Filled warning button
                } else {
                    relationshipModeToggle.getStyleClass().add("btn-outline-warning");  // Outline warning button
                }

                // Maintain the consistent button width to prevent expansion
                relationshipModeToggle.setPrefWidth(180);
            });

            // Relationship Type Radio Buttons
            ToggleGroup relationshipGroup = new ToggleGroup();
            RadioButton associationBtn = createRadioButton("Association", relationshipGroup, RelationshipType.ASSOCIATION);
            RadioButton aggregationBtn = createRadioButton("Aggregation", relationshipGroup, RelationshipType.AGGREGATION);
            RadioButton compositionBtn = createRadioButton("Composition", relationshipGroup, RelationshipType.COMPOSITION);
            RadioButton inheritanceBtn = createRadioButton("Inheritance", relationshipGroup, RelationshipType.INHERITANCE);

            relationshipGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
                if (newToggle != null) {
                    selectedRelationshipType = (RelationshipType) newToggle.getUserData();
                }
            });

            secondSection.getChildren().addAll(relationshipModeToggle, associationBtn, aggregationBtn, compositionBtn, inheritanceBtn);
            secondSection.setAlignment(Pos.CENTER); // Center-align the second section

            // Third Section (Right-aligned): Zoom In/Out, Generate Code, Save as Image
            Button zoomInButton = new Button("+");
            zoomInButton.getStyleClass().add("zoom-button");

            Button zoomOutButton = new Button("-");
            zoomOutButton.getStyleClass().add("zoom-button");

            zoomInButton.setOnAction(event -> {
                classDiagramPane.zoomFactor += 0.1;
                classDiagramPane.setScaleX(classDiagramPane.zoomFactor);
                classDiagramPane.setScaleY(classDiagramPane.zoomFactor);
            });

            zoomOutButton.setOnAction(event -> {
                classDiagramPane.zoomFactor -= 0.1;
                if (classDiagramPane.zoomFactor < 0.1) classDiagramPane.zoomFactor = 0.1;
                classDiagramPane.setScaleX(classDiagramPane.zoomFactor);
                classDiagramPane.setScaleY(classDiagramPane.zoomFactor);
            });

            // Generate Code Button (Metallic style)
            Button generateButton = new Button("Generate Code");
            generateButton.getStyleClass().addAll("btn", "btn-outline-secondary");
            generateButton.setOnAction(event -> controller.generateCode());

            Button saveAsImageButton = new Button("Save as JPEG");
            saveAsImageButton.getStyleClass().addAll("btn", "save-image-button");
            saveAsImageButton.setOnAction(event -> saveDiagramAsImage());

            thirdSection.getChildren().addAll(zoomInButton, zoomOutButton, generateButton, saveAsImageButton);
            thirdSection.setAlignment(Pos.CENTER_RIGHT); // Right-align the third section

            // Add the sections to the toolbar with separators between them
            toolbar.getItems().addAll(firstSection, new Separator(), secondSection, new Separator(), thirdSection);

        } else if (packageDiagramPane != null) { // Package Diagram-specific toolbar
            Button addPackageButton = new Button("Add Package");
            addPackageButton.getStyleClass().addAll("btn", "btn-success");
            addPackageButton.setOnAction(event -> {
                controller.addPackageBox(packageDiagramPane);
            });

            relationshipModeToggle = new ToggleButton("Relationship Mode");
            relationshipModeToggle.getStyleClass().addAll("btn", "btn-outline-warning");
            relationshipModeToggle.setOnAction(event -> {
                boolean isActive = relationshipModeToggle.isSelected();
                packageDiagramPane.setPackageModeEnabled(isActive);
                relationshipModeToggle.setText(isActive ? "Exit Relationship Mode" : "Relationship Mode");
                relationshipModeToggle.getStyleClass().clear();
                relationshipModeToggle.getStyleClass().add(isActive ? "btn" : "btn-outline-warning");
            });

            toolbar.getItems().addAll(addPackageButton, relationshipModeToggle);
        }

        return toolbar;
    }

    private RadioButton createRadioButton(String text, ToggleGroup group, RelationshipType type) {
        RadioButton button = new RadioButton(text);
        button.setToggleGroup(group);
        button.setUserData(type);
        button.getStyleClass().add("btn-toggle");
        return button;
    }


    private VBox createRightSideToolbar() {
        VBox toolbar = new VBox();
        toolbar.setPadding(new Insets(10));
        toolbar.setSpacing(10);
        toolbar.setStyle("-fx-background-color: #f8f9fa;"); // Light grey background similar to Bootstrap

        // Title with enhanced styling
        Label title = new Label("Details");
        title.getStyleClass().add("h4"); // Bootstrap's heading class for a larger, bold title
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #343a40;"); // Dark color for contrast

        // Adding the title to the toolbar
        toolbar.getChildren().add(title);

        // Optionally, you can add more content here (buttons, text fields, etc.)

        return toolbar;
    }

    private VBox createClassListPanel() {
        VBox panel = new VBox();
        panel.setSpacing(15); // Modern spacing
        panel.setPadding(new Insets(10)); // Padding around the content
        panel.setStyle("-fx-background-color: #f8f9fa;"); // Light background

        Label titleLabel = new Label("Class List");
        panel.getChildren().addAll(titleLabel, classListView);
        classListView.setOnMouseClicked(event -> {
            String selectedItem = classListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                // Check if the clicked item is already selected
                if (classListView.getSelectionModel().getSelectedItems().contains(selectedItem)) {
                    // Unselect the class by clearing the selection
                    classListView.getSelectionModel().clearSelection();
                    classDiagramPane.unhighlightAllClassBoxes(); // Unhighlight all classes
                } else {
                    // Highlight the newly selected class
                    classDiagramPane.unhighlightAllClassBoxes(); // First, unhighlight all
                    classDiagramPane.highlightClassBox(selectedItem); // Then, highlight the selected one
                }
            } else {
                // If no item is selected, ensure all classes are unhighlighted
                classDiagramPane.unhighlightAllClassBoxes();
            }
        });


        classListView.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(event -> {
                String className = cell.getItem();
                if (className != null) {
                    Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION,
                            "Are you sure you want to delete the class \"" + className + "\"?",
                            ButtonType.YES, ButtonType.NO);
                    confirmationAlert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            // Remove the class from the ClassDiagramPane and classListView
                            controller.deleteClassBox(classDiagramPane,classDiagramPane.getClassBoxByTitle(className));
                            classListView.getItems().remove(className);
                        }
                    });
                }
            });

            classListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    classDiagramPane.unhighlightAllClassBoxes();
                    classDiagramPane.highlightClassBox(newSelection);
                } else {
                    classDiagramPane.unhighlightAllClassBoxes();
                }
            });

            classListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);



            contextMenu.getItems().add(deleteItem);
            cell.setContextMenu(contextMenu);
            cell.textProperty().bind(cell.itemProperty()); // Bind text to item
            return cell;
        });

        return panel;
    }

    private VBox createCodeGenerationPanel() {
        VBox codeGenerationPanel = new VBox();
        codeGenerationPanel.setSpacing(10);
        codeGenerationPanel.setPadding(new Insets(10));
        codeGenerationPanel.setStyle("-fx-background-color: #f4f4f4;");
        Label codeGenerationLabel = new Label("Code Generation");
        Button generateButton = new Button("Generate Code");
        generateButton.setOnAction(event -> controller.generateCode());
        codeGenerationPanel.getChildren().addAll(codeGenerationLabel, generateButton);
        return codeGenerationPanel;
    }

    public void updateRightSideToolbar(Object selectedItem) {
        rightSideToolbar.getChildren().clear();

        if (selectedItem instanceof ClassBox) {
            ClassBox classBox = (ClassBox) selectedItem;

            // Label for Class title
            Label nameLabel = new Label("Class: " + classBox.getTitle());
            nameLabel.getStyleClass().add("h5"); // Bootstrap-like heading

            // Labels for Attributes and Operations
            Label attributesLabel = new Label("Attributes: " + classBox.getAttributesBox().toString());
            attributesLabel.getStyleClass().add("text-muted"); // Muted color for description
            Label operationLabel = new Label("Operations: " + classBox.getOperationsBox().toString());
            operationLabel.getStyleClass().add("text-muted");

            // Add to the right-side toolbar
            rightSideToolbar.getChildren().addAll(nameLabel, attributesLabel, operationLabel);
        } else if (selectedItem instanceof RelationshipLine) {
            RelationshipLine relationshipLine = (RelationshipLine) selectedItem;
            Relationship relationship = controller.relationshipMapping.get(relationshipLine);

            TextField titleField = new TextField(relationshipLine.getTitle());
            titleField.setPromptText("Enter Relationship Title");
            titleField.getStyleClass().add("form-control"); // Bootstrap form control style
            titleField.setOnAction(e -> {
                String newTitle = titleField.getText();
                relationshipLine.setTitle(newTitle);
                relationship.relationshipLabel = newTitle; // Update model
                controller.relationshipMapping.put(relationshipLine, relationship); // Update map
            });

            TextField startMultiplicityField = new TextField(relationshipLine.getMultiplicityStart());
            startMultiplicityField.setPromptText("Start Multiplicity");
            startMultiplicityField.getStyleClass().add("form-control"); // Bootstrap form control style
            startMultiplicityField.setOnAction(e -> {
                String newStartMultiplicity = startMultiplicityField.getText();
                relationshipLine.setMultiplicityStart(newStartMultiplicity);
                relationship.sourceMultiplicity = newStartMultiplicity; // Update model
                controller.relationshipMapping.put(relationshipLine, relationship); // Update map
            });

            TextField endMultiplicityField = new TextField(relationshipLine.getMultiplicityEnd());
            endMultiplicityField.setPromptText("End Multiplicity");
            endMultiplicityField.getStyleClass().add("form-control"); // Bootstrap form control style
            endMultiplicityField.setOnAction(e -> {
                String newEndMultiplicity = endMultiplicityField.getText();
                relationshipLine.setMultiplicityEnd(newEndMultiplicity);
                relationship.targetMultiplicity = newEndMultiplicity; // Update model
                controller.relationshipMapping.put(relationshipLine, relationship); // Update map
            });

            // Add labels and fields to the toolbar
            // Adding Relationship Details Label with a style class
            Label relationshipDetailsLabel = new Label("Relationship Details");
            relationshipDetailsLabel.getStyleClass().add("h5");

            // Adding labels with text-muted class
            Label titleLabel = new Label("Title:");
            titleLabel.getStyleClass().add("text-muted");

            Label startMultiplicityLabel = new Label("Start Multiplicity:");
            startMultiplicityLabel.getStyleClass().add("text-muted");

            Label endMultiplicityLabel = new Label("End Multiplicity:");
            endMultiplicityLabel.getStyleClass().add("text-muted");

            // Add to the right side toolbar
            rightSideToolbar.getChildren().addAll(
                    relationshipDetailsLabel,
                    titleLabel, titleField,
                    startMultiplicityLabel, startMultiplicityField,
                    endMultiplicityLabel, endMultiplicityField
            );
        }
    }


    public void updateClassListView() {
        projectExplorer.setRoot(new TreeItem<>("Class Diagrams"));
        if (controller.getCurrentProject() != null) {
            for (Diagram diagram : controller.getCurrentProject().getDiagrams()) {
                TreeItem<String> item = new TreeItem<>(diagram.getTitle());
                projectExplorer.getRoot().getChildren().add(item);
            }
        }
        projectExplorer.refresh(); // Ensure the view is refreshed
    }

    public void addClassToList(String className) {
        classListView.getItems().add(className);
    }

    public void handleClassBoxClick(ClassBox clickedClassBox) {
        System.out.println("handleClassBoxClick called for " + clickedClassBox.getClassDiagram().getTitle());
        handleSelection(clickedClassBox);

        if (!relationshipModeToggle.isSelected() || selectedRelationshipType == null) {
            System.out.println("Relationship mode not active or no type selected.");
            return;
        }

        if (sourceClassBox == null) {
            sourceClassBox = clickedClassBox;
            sourceClassBox.setStyle("-fx-padding: 5;-fx-border-color: blue;-fx-background-color: #e0e0e0;"); // Highlight the source
            System.out.println("Source class box is selected: " + clickedClassBox.getClassDiagram().getTitle());
        } else if (!sourceClassBox.equals(clickedClassBox)) {
            controller.createRelationship(classDiagramPane, sourceClassBox, "right", clickedClassBox, "left", selectedRelationshipType);
            sourceClassBox.setStyle("-fx-border-color: black; -fx-padding: 5; -fx-background-color: #e0e0e0;"); // Reset source style
            sourceClassBox = null; // Clear source selection
            System.out.println("Target class box is selected: " + clickedClassBox.getClassDiagram().getTitle());
        } else {
            sourceClassBox.setStyle("-fx-border-color: black; -fx-padding: 5; -fx-background-color: #e0e0e0;");
            sourceClassBox = null;
            System.out.println("Source selection cleared.");
        }
    }

    // UI helper functions
    public void saveDiagramAsImage() {
        // Determine which pane to capture: ClassDiagramPane or PackageDiagramPane
        WritableImage snapshot = null;

        if (classDiagramPane != null) {
            snapshot = classDiagramPane.snapshot(new SnapshotParameters(), null);
        } else if (packageDiagramPane != null) {
            snapshot = packageDiagramPane.snapshot(new SnapshotParameters(), null);
        } else {
            System.out.println("No diagram pane available for saving as an image.");
            return;
        }

        // Open a file chooser to save the image
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Diagram as Image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPEG Files", "*.jpg"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("BMP Files", "*.bmp"));

        File file = fileChooser.showSaveDialog(this.getScene().getWindow());
        if (file != null) {
            try {
                String extension = getFileExtension(file.getName());
                if (extension == null) {
                    // Default to PNG if no extension is specified
                    extension = "png";
                    file = new File(file.getAbsolutePath() + ".png");
                }

                ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), extension, file);
                System.out.println("Diagram saved as: " + file.getAbsolutePath());
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("Failed to save the diagram as an image.");
            }
        }
    }

    public void handleSelection(Object selectedItem) {
        this.updateRightSideToolbar(selectedItem);
    }


    // setter getters
    public void setRelationshipModeEnabled(boolean enabled) {
        this.relationshipMode = enabled;
        if (!enabled) {
            packageDiagramPane.clearSelectedPackage(); // Clear selection when exiting relationship mode
        }
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex > 0 && dotIndex < fileName.length() - 1) ? fileName.substring(dotIndex + 1) : null;
    }

    public ClassDiagramPane getClassDiagramPane() {
        return classDiagramPane;
    }

    public MainController getController() {
        return controller;
    }


}
