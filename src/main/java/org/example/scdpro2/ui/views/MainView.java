package org.example.scdpro2.ui.views;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.stage.FileChooser;
import org.example.scdpro2.business.models.ClassDiagram;
import org.example.scdpro2.business.models.Diagram;
import org.example.scdpro2.business.models.Project;
import org.example.scdpro2.ui.controllers.MainController;
import org.example.scdpro2.business.services.DiagramService;
import org.example.scdpro2.ui.views.RelationshipLine.RelationshipType;

import java.io.File;

public class MainView extends BorderPane {
    private final MainController controller;
    private final ClassDiagramPane diagramPane;
    private ToggleButton relationshipModeToggle;
    private RelationshipType selectedRelationshipType; // Current selected relationship type
    private ClassBox sourceClassBox; // Temporarily holds the source ClassBox for relationship
    private final ListView<String> classListView; // Dynamic list of class names
    private TreeView<String> projectExplorer;

    public MainView() {
        DiagramService diagramService = new DiagramService();
        this.controller = new MainController(diagramService, this); // Pass MainView to controller
        this.diagramPane = new ClassDiagramPane(this, controller, diagramService);

        classListView = new ListView<>(); // Initialize dynamic list
        classListView.setPrefWidth(200); // Optional: Set a preferred width

        // Initialize the projectExplorer
        this.projectExplorer = createProjectExplorer();

        MenuBar menuBar = createMenuBar();
        ToolBar toolbar = createToolbar();
        VBox classListPanel = createClassListPanel();
        VBox codeGenerationPanel = createCodeGenerationPanel();

        setTop(new VBox(menuBar, toolbar));
        setLeft(classListPanel);
        setCenter(diagramPane);
        setRight(codeGenerationPanel);
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu projectMenu = new Menu("Project");

        MenuItem saveProjectItem = new MenuItem("Save Project");
        saveProjectItem.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Project");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Project Files", "*.proj"));
            File selectedFile = fileChooser.showSaveDialog(getScene().getWindow());
            if (selectedFile != null) {
                controller.saveProjectToFile(selectedFile);
            }
        });

        MenuItem loadProjectItem = new MenuItem("Load Project");
        loadProjectItem.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load Project");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Project Files", "*.proj"));
            File selectedFile = fileChooser.showOpenDialog(getScene().getWindow());
            if (selectedFile != null) {
                controller.loadProjectFromFile(selectedFile);
            }
        });

        MenuItem newProjectItem = new MenuItem("New Project");
        newProjectItem.setOnAction(event -> {
            String projectName = promptForProjectName();
            controller.createNewProject(projectName);
            updateClassListView(); // Refresh the class list
        });

        projectMenu.getItems().addAll(newProjectItem, saveProjectItem, loadProjectItem);

        menuBar.getMenus().add(projectMenu);

        return menuBar;
    }

    private String promptForProjectName() {
        TextInputDialog dialog = new TextInputDialog("Untitled Project");
        dialog.setTitle("New Project");
        dialog.setHeaderText("Create a New Project");
        dialog.setContentText("Enter project name:");
        return dialog.showAndWait().orElse("Untitled Project");
    }


    private void saveProject() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Project");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Serialized Files", "*.ser"));
        File file = fileChooser.showSaveDialog(getScene().getWindow());

        if (file != null) {
            controller.saveProjectToFile(file);
        }
    }

    private void loadProject() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Project");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Serialized Files", "*.ser"));
        File file = fileChooser.showOpenDialog(getScene().getWindow());

        if (file != null) {
            controller.loadProjectFromFile(file);
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

    private TreeView<String> createProjectExplorer() {
        TreeItem<String> rootItem = new TreeItem<>("Project Explorer");
        rootItem.setExpanded(true);
        return new TreeView<>(rootItem);
    }







    // Toolbar setup, including the "Add Class" button and relationship type selection
    private ToolBar createToolbar() {
        ToolBar toolbar = new ToolBar();

        // Add Class and Interface buttons
        Button addClassButton = new Button("Add Class");
        addClassButton.setOnAction(event -> {
            controller.addClassBox(diagramPane);
            updateClassListView(); // Update list
        });

        Button addInterfaceButton = new Button("Add Interface");
        addInterfaceButton.setOnAction(event -> {
            controller.addInterfaceBox(diagramPane);
            updateClassListView(); // Update list
        });

        // Relationship Mode Toggle
        relationshipModeToggle = new ToggleButton("Relationship Mode");
        relationshipModeToggle.setOnAction(event -> {
            boolean isActive = relationshipModeToggle.isSelected();
            diagramPane.setRelationshipModeEnabled(isActive);
            relationshipModeToggle.setText(isActive ? "Exit Relationship Mode" : "Relationship Mode");
            System.out.println("Relationship mode: " + (isActive ? "Enabled" : "Disabled"));
        });

        // Relationship Type Buttons
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

        // Update the selected relationship type
        relationshipGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                selectedRelationshipType = (RelationshipType) newToggle.getUserData();
            }
        });

        toolbar.getItems().addAll(addClassButton, addInterfaceButton, relationshipModeToggle,
                new Separator(), associationBtn, aggregationBtn, compositionBtn, inheritanceBtn);
        return toolbar;
    }


    private VBox createClassListPanel() {
        VBox panel = new VBox();
        panel.setSpacing(10);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-background-color: #f4f4f4;");
        Label titleLabel = new Label("Class List");
        panel.getChildren().addAll(titleLabel, classListView);
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

    public void handleInterfaceBoxClick(InterfaceBox clickedInterfaceBox) {
        System.out.println("handleInterfaceBoxClick called for " + clickedInterfaceBox.getInterfaceDiagram().getTitle());

        if (!relationshipModeToggle.isSelected() || selectedRelationshipType == null) {
            System.out.println("Relationship mode not active or no type selected.");
            return;
        }

        if (sourceClassBox == null) {
            clickedInterfaceBox.setStyle("-fx-border-color: blue;"); // Highlight the source
            sourceClassBox = null; // Clear any selected class box
            System.out.println("Source interface box is selected: " + clickedInterfaceBox.getInterfaceDiagram().getTitle());
        } else {
            controller.createRelationship(diagramPane, sourceClassBox, clickedInterfaceBox, selectedRelationshipType);
            sourceClassBox = null; // Clear source selection
            System.out.println("Relationship created with interface box as target.");
        }
    }


    public ClassDiagramPane getDiagramPane() {
        return diagramPane;
    }
}
