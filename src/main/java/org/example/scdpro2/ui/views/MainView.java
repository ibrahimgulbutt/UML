package org.example.scdpro2.ui.views;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.stage.FileChooser;
import org.example.scdpro2.business.models.Diagram;
import org.example.scdpro2.ui.controllers.MainController;
import org.example.scdpro2.business.services.DiagramService;
import org.example.scdpro2.ui.views.RelationshipLine.RelationshipType;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.File;

public class MainView extends BorderPane {
    private final MainController controller;

    private ClassDiagramPane classDiagramPane;
    private PackageDiagramPane packageDiagramPane;

    private ToggleButton relationshipModeToggle;
    private RelationshipType selectedRelationshipType; // Current selected relationship type

    private ClassBox sourceClassBox; // Temporarily holds the source ClassBox for relationship
    private InterfaceBox sourceInterfaceBox;
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

        this.rightSideToolbar = createRightSideToolbar();
        setRight(rightSideToolbar);

        setTop(new VBox(menuBar, toolbar));
        setLeft(classListPanel);
        setRight(codeGenerationPanel);

        this.rightSideToolbar = createRightSideToolbar();
        setRight(rightSideToolbar);

    }

    // creational functions

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

    private VBox createRightSideToolbar() {
        VBox toolbar = new VBox();
        toolbar.setPadding(new Insets(10));
        toolbar.setSpacing(10);
        toolbar.setStyle("-fx-background-color: lightgrey;");

        Label title = new Label("Details");
        toolbar.getChildren().add(title);

        return toolbar;
    }

    private ToolBar createToolbar() {
        ToolBar toolbar = new ToolBar();

        if (classDiagramPane != null) { // Class Diagram-specific toolbar
            // Add Class Button (Primary button style)
            Button addClassButton = new Button("Add Class");
            addClassButton.getStyleClass().addAll("btn", "btn-primary"); // Bootstrap Primary Button

            Tooltip.install(addClassButton, new Tooltip("Click to add a new class to the diagram."));

            addClassButton.setOnAction(event -> {
                controller.addClassBox(classDiagramPane);
                updateClassListView(); // Update the class list
            });

            // Add Interface Button (Secondary button style)
            Button addInterfaceButton = new Button("Add Interface");
            addInterfaceButton.getStyleClass().addAll("btn", "btn-secondary"); // Bootstrap Secondary Button
            addInterfaceButton.setOnAction(event -> {
                controller.addInterfaceBox(classDiagramPane);
                updateClassListView(); // Update the class list
            });

            // Relationship Mode Toggle Button (Warning button style)
            relationshipModeToggle = new ToggleButton("Relationship Mode");
            relationshipModeToggle.getStyleClass().addAll("btn", "btn-warning"); // Bootstrap Warning Button

            relationshipModeToggle.setOnAction(event -> {
                boolean isActive = relationshipModeToggle.isSelected();
                classDiagramPane.setRelationshipModeEnabled(isActive);
                relationshipModeToggle.setText(isActive ? "Exit Relationship Mode" : "Relationship Mode");
            });

            // Relationship Type Buttons (Radio Buttons for toggle group)
            ToggleGroup relationshipGroup = new ToggleGroup();

            RadioButton associationBtn = new RadioButton("Association");
            associationBtn.setToggleGroup(relationshipGroup);
            associationBtn.setUserData(RelationshipType.ASSOCIATION);
            associationBtn.getStyleClass().add("btn-toggle"); // Custom Toggle Button Style

            RadioButton aggregationBtn = new RadioButton("Aggregation");
            aggregationBtn.setToggleGroup(relationshipGroup);
            aggregationBtn.setUserData(RelationshipType.AGGREGATION);
            aggregationBtn.getStyleClass().add("btn-toggle");

            RadioButton compositionBtn = new RadioButton("Composition");
            compositionBtn.setToggleGroup(relationshipGroup);
            compositionBtn.setUserData(RelationshipType.COMPOSITION);
            compositionBtn.getStyleClass().add("btn-toggle");

            RadioButton inheritanceBtn = new RadioButton("Inheritance");
            inheritanceBtn.setToggleGroup(relationshipGroup);
            inheritanceBtn.setUserData(RelationshipType.INHERITANCE);
            inheritanceBtn.getStyleClass().add("btn-toggle");

            relationshipGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
                if (newToggle != null) {
                    selectedRelationshipType = (RelationshipType) newToggle.getUserData();
                }
            });

            toolbar.getItems().addAll(
                    addClassButton, addInterfaceButton, relationshipModeToggle,
                    new Separator(), associationBtn, aggregationBtn, compositionBtn, inheritanceBtn
            );

        }
        else if (packageDiagramPane != null) { // Package Diagram-specific toolbar
            Button addPackageButton = new Button("Add Package");
            addPackageButton.getStyleClass().addAll("btn", "btn-success"); // Bootstrap Success Button
            addPackageButton.setOnAction(event -> {
                controller.addPackageBox(packageDiagramPane);
            });

            Button removePackageButton = new Button("Remove Package");
            removePackageButton.getStyleClass().addAll("btn", "btn-danger"); // Bootstrap Danger Button
            removePackageButton.setOnAction(event -> {
                // Example action
            });

            toolbar.getItems().addAll(addPackageButton, removePackageButton);
        }

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


    // non-creation functions

    public void updateRightSideToolbar(Object selectedItem) {
        rightSideToolbar.getChildren().clear();

        if (selectedItem instanceof ClassBox) {
            ClassBox classBox = (ClassBox) selectedItem;

            Label nameLabel = new Label("Class: " + classBox.getTitle());
            Label AttributesLabel = new Label("Attributes : "+classBox.getAttributesBox().toString());
            Label OperationLabel = new Label("Operations : "+ classBox.getOperationsBox().toString());
            rightSideToolbar.getChildren().add(nameLabel);
            rightSideToolbar.getChildren().add(AttributesLabel);
            rightSideToolbar.getChildren().add(OperationLabel);

        }
        else if (selectedItem instanceof RelationshipLine) {
            RelationshipLine relationship = (RelationshipLine) selectedItem;

            TextField titleField = new TextField(relationship.getTitle());
            titleField.setPromptText("Enter Relationship Title");
            titleField.setOnAction(e -> relationship.setTitle(titleField.getText()));

            TextField startMultiplicityField = new TextField(relationship.getMultiplicityStart());
            startMultiplicityField.setPromptText("Start Multiplicity");
            startMultiplicityField.setOnAction(e -> relationship.setMultiplicityStart(startMultiplicityField.getText()));

            TextField endMultiplicityField = new TextField(relationship.getMultiplicityEnd());
            endMultiplicityField.setPromptText("End Multiplicity");
            endMultiplicityField.setOnAction(e -> relationship.setMultiplicityEnd(endMultiplicityField.getText()));

            rightSideToolbar.getChildren().addAll(
                    new Label("Relationship Details"),
                    new Label("Title:"), titleField,
                    new Label("Start Multiplicity:"), startMultiplicityField,
                    new Label("End Multiplicity:"), endMultiplicityField
            );
        }
    }

    public void handleSelection(Object selectedItem) {
        this.updateRightSideToolbar(selectedItem);
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

    public void setRelationshipModeEnabled(boolean enabled) {
        this.relationshipMode = enabled;
        if (!enabled) {
            packageDiagramPane.clearSelectedPackage(); // Clear selection when exiting relationship mode
        }
    }

    public void handlePackageBoxClick(PackageBox clickedPackageBox) {
        if (!relationshipMode) return;

        if (sourcePackageBox == null) {
            sourcePackageBox = clickedPackageBox;
            sourcePackageBox.setStyle("-fx-border-color: blue;");
        } else {
            packageDiagramPane.addRelationshipLine(sourcePackageBox.getPackageComponent(), clickedPackageBox.getPackageComponent(), "Dependency");
            sourcePackageBox.setStyle("-fx-border-color: black;");
            sourcePackageBox = null;
        }
    }

    // Handle relationship mode logic for connecting two ClassBox components
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

    public void handleInterfaceBoxClick(InterfaceBox clickedInterfaceBox) {
        System.out.println("handleInterfaceBoxClick called for " + clickedInterfaceBox.getInterfaceDiagram().getTitle());
        handleSelection(clickedInterfaceBox);

        if (!relationshipModeToggle.isSelected() || selectedRelationshipType == null) {
            System.out.println("Relationship mode not active or no type selected.");
            return;
        }

        if (sourceClassBox == null && sourceInterfaceBox == null) {
            sourceInterfaceBox = clickedInterfaceBox;
            clickedInterfaceBox.setStyle("-fx-border-color: blue;"); // Highlight the source
            System.out.println("Source interface box is selected: " + clickedInterfaceBox.getInterfaceDiagram().getTitle());
        } else {
            if (sourceClassBox != null) {
                controller.createRelationship(classDiagramPane, sourceClassBox, "right", clickedInterfaceBox, "left", selectedRelationshipType);
                sourceClassBox.setStyle("-fx-border-color: blue; -fx-padding: 5; -fx-background-color: #e0e0e0;"); // Reset style
                sourceClassBox = null;
            } else if (sourceInterfaceBox != null) {
                //controller.createRelationship(classDiagramPane, sourceInterfaceBox, "right", clickedInterfaceBox, "left", selectedRelationshipType);
                sourceInterfaceBox.setStyle(""); // Reset style
                sourceInterfaceBox = null;
            }
            System.out.println("Relationship created with interface box as target.");
        }
    }

    public ClassDiagramPane getClassDiagramPane() {
        return classDiagramPane;
    }

    public MainController getController() {
        return controller;
    }

}
