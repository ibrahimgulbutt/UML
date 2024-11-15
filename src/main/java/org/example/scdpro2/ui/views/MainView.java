package org.example.scdpro2.ui.views;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import org.example.scdpro2.ui.controllers.MainController;
import org.example.scdpro2.business.services.DiagramService;

public class MainView extends BorderPane {
    private final MainController controller;
    private final ClassDiagramPane diagramPane;
    private ToggleButton relationshipModeToggle;

    public MainView() {
        DiagramService diagramService = new DiagramService();
        this.controller = new MainController(diagramService);
        this.diagramPane = new ClassDiagramPane(controller, diagramService);

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

    // Toolbar setup, including the "Add Class" button that calls addClassBox
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

            if (!isActive) {
                diagramPane.clearSelectedClass();
            }
        });

        toolbar.getItems().addAll(addClassButton, relationshipModeToggle);
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
}
