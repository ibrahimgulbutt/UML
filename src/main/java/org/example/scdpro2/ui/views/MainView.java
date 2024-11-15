package org.example.scdpro2.ui.views;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import org.example.scdpro2.ui.controllers.MainController;
import org.example.scdpro2.business.services.DiagramService;
import org.example.scdpro2.ui.views.RelationshipLine.RelationshipType;

public class MainView extends BorderPane {
    private final MainController controller;
    private final ClassDiagramPane diagramPane;
    private ToggleButton relationshipModeToggle;
    private RelationshipType selectedRelationshipType; // Current selected relationship type
    private ClassBox sourceClassBox; // Temporarily holds the source ClassBox for relationship

    public MainView() {
        DiagramService diagramService = new DiagramService();
        this.controller = new MainController(diagramService);
        this.diagramPane = new ClassDiagramPane(this, controller, diagramService);


        MenuBar menuBar = createMenuBar();
        ToolBar toolbar = createToolbar();
        TreeView<String> projectExplorer = createProjectExplorer();
        VBox codeGenerationPanel = createCodeGenerationPanel();

        setTop(new VBox(menuBar, toolbar));
        setLeft(projectExplorer);
        setCenter(diagramPane);
        setRight(codeGenerationPanel);
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu projectMenu = new Menu("Project");

        MenuItem saveProjectItem = new MenuItem("Save Project");
        saveProjectItem.setOnAction(event -> controller.saveProject());

        MenuItem loadProjectItem = new MenuItem("Load Project");
        loadProjectItem.setOnAction(event -> controller.loadProject());

        projectMenu.getItems().addAll(new MenuItem("New Project"), saveProjectItem, loadProjectItem);

        Menu exportMenu = new Menu("Export");
        exportMenu.getItems().addAll(
                new MenuItem("Export Diagram as PNG"),
                new MenuItem("Export Diagram as JPEG")
        );

        menuBar.getMenus().addAll(projectMenu, exportMenu);
        return menuBar;
    }

    // Toolbar setup, including the "Add Class" button and relationship type selection
    private ToolBar createToolbar() {
        ToolBar toolbar = new ToolBar();

        // "Add Class" button
        Button addClassButton = new Button("Add Class");
        addClassButton.setOnAction(event -> controller.addClassBox(diagramPane)); // Call to addClassBox

        // Relationship Mode Toggle
        relationshipModeToggle = new ToggleButton("Relationship Mode");
        relationshipModeToggle.setOnAction(event -> {
            boolean isActive = relationshipModeToggle.isSelected();
            diagramPane.setRelationshipModeEnabled(isActive);
            relationshipModeToggle.setText(isActive ? "Exit Relationship Mode" : "Relationship Mode");
            System.out.println("Relationship mode: " + (isActive ? "Enabled" : "Disabled"));

            if (!isActive) {
                sourceClassBox = null; // Clear source selection when exiting relationship mode
                diagramPane.clearSelectedClass();
            }
        });



        // Relationship Type Selection Buttons
        ToggleGroup relationshipGroup = new ToggleGroup();

        RadioButton associationBtn = new RadioButton("Association");
        associationBtn.setToggleGroup(relationshipGroup);
        associationBtn.setUserData(RelationshipType.ASSOCIATION);

        RadioButton aggregationBtn = new RadioButton("Aggregation");
        aggregationBtn.setToggleGroup(relationshipGroup);
        aggregationBtn.setUserData(RelationshipType.AGGREGATION);

        RadioButton compositionBtn = new RadioButton("Composition");
        compositionBtn.setToggleGroup(relationshipGroup);
        compositionBtn.setUserData(RelationshipType.COMPOSITION);

        RadioButton inheritanceBtn = new RadioButton("Inheritance");
        inheritanceBtn.setToggleGroup(relationshipGroup);
        inheritanceBtn.setUserData(RelationshipType.INHERITANCE);

        // Update selected relationship type
        relationshipGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                selectedRelationshipType = (RelationshipType) newToggle.getUserData();
            }
        });

        toolbar.getItems().addAll(addClassButton, relationshipModeToggle,
                new Separator(), associationBtn, aggregationBtn, compositionBtn, inheritanceBtn);
        return toolbar;
    }

    private TreeView<String> createProjectExplorer() {
        TreeItem<String> rootItem = new TreeItem<>("Project Explorer");
        rootItem.setExpanded(true);
        return new TreeView<>(rootItem);
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

    // Handle relationship mode logic for connecting two ClassBox components
    public void handleClassBoxClick(ClassBox clickedClassBox) {
        System.out.println("handleClassBoxClick called for " + clickedClassBox.getClassDiagram().getTitle());

        if (!relationshipModeToggle.isSelected() || selectedRelationshipType == null) {
            System.out.println("Relationship mode not active or no type selected.");
            return;
        }

        if (sourceClassBox == null) {
            sourceClassBox = clickedClassBox;
            sourceClassBox.setStyle("-fx-border-color: blue;"); // Highlight the source
            System.out.println("Source class box is selected: " + clickedClassBox.getClassDiagram().getTitle());
        } else if (!sourceClassBox.equals(clickedClassBox)) {
            controller.createRelationship(diagramPane, sourceClassBox, clickedClassBox, selectedRelationshipType);
            sourceClassBox.setStyle("-fx-border-color: black;"); // Reset source style
            sourceClassBox = null; // Clear source selection
            System.out.println("Target class box is selected: " + clickedClassBox.getClassDiagram().getTitle());
        } else {
            sourceClassBox.setStyle("-fx-border-color: black;");
            sourceClassBox = null;
            System.out.println("Source selection cleared.");
        }
    }



}
