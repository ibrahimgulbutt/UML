package org.example.scdpro2.ui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.scdpro2.business.services.DiagramService;
import org.example.scdpro2.ui.controllers.MainController;

import java.io.File;
/**
 * The {@code StartPageView} class represents the start page of the SCDPro2 application.
 * It provides users with options to create a new project or open an existing one.
 */
public class StartPageView extends BorderPane {
    private final Stage primaryStage;
    private final MainController mainController;
    private MainView mainView;

    /**
     * Constructs a new instance of {@code StartPageView}.
     *
     * @param primaryStage the primary {@link Stage} used to display this view
     */
    public StartPageView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        DiagramService diagramService = new DiagramService();
        this.mainController = new MainController(diagramService);

        initializeUI();
    }
    /**
     * Initializes the user interface components for the start page.
     * Includes a welcome message, project buttons, and a styled layout.
     */
    private void initializeUI() {
        // Title Label with modern styling
        Label titleLabel = new Label("Welcome to SCDPro2");
        titleLabel.getStyleClass().addAll("h1", "text-primary", "text-center", "mb-4");

        // Description label below the title
        Label descriptionLabel = new Label("Design and manage your software diagrams effortlessly.");
        descriptionLabel.getStyleClass().addAll("text-muted", "text-center", "mb-4");

        // New Project Button
        Button newProjectButton = new Button("New Project");
        newProjectButton.getStyleClass().addAll("btn", "btn-success", "btn-lg", "mb-3");
        newProjectButton.setOnAction(event -> showNewProjectDialog());

        // Open Project Button
        Button openProjectButton = new Button("Open Project");
        openProjectButton.getStyleClass().addAll("btn", "btn-outline-primary", "btn-lg", "mb-3");
        openProjectButton.setOnAction(event -> openExistingProject());

        // Buttons Container with vertical spacing
        VBox buttonContainer = new VBox(15, newProjectButton, openProjectButton);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets(20));

        // Main Layout Container with proper spacing and alignment
        VBox layoutContainer = new VBox(30, titleLabel, descriptionLabel, buttonContainer);
        layoutContainer.getStyleClass().add("container");
        layoutContainer.setAlignment(Pos.CENTER);
        layoutContainer.setPadding(new Insets(50)); // Padding around the main container
        layoutContainer.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-border-radius: 10; -fx-background-radius: 10;");

        // Center the main layout in the scene
        setCenter(layoutContainer);
    }
    /**
     * Displays a dialog to create a new project and initializes the main view.
     */
    private void showNewProjectDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Create New Project");
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        // Create a VBox container for dialog content
        VBox dialogContent = new VBox(15);
        dialogContent.setPadding(new Insets(20));
        dialogContent.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dddddd; -fx-border-radius: 10; -fx-background-radius: 10;");

        // Project type label
        Label projectTypeLabel = new Label("Select Project Type:");
        projectTypeLabel.getStyleClass().addAll("h5", "text-primary", "mb-2");

        // Radio buttons for project type selection
        ToggleGroup projectTypeGroup = new ToggleGroup();
        RadioButton classDiagramButton = new RadioButton("Class Diagram");
        classDiagramButton.setToggleGroup(projectTypeGroup);
        classDiagramButton.getStyleClass().add("form-check");

        RadioButton packageDiagramButton = new RadioButton("Package Diagram");
        packageDiagramButton.setToggleGroup(projectTypeGroup);
        packageDiagramButton.getStyleClass().add("form-check");

        VBox projectTypeOptions = new VBox(10, classDiagramButton, packageDiagramButton);
        projectTypeOptions.getStyleClass().add("form-group");

        // Project name label and text field
        Label projectNameLabel = new Label("Enter Project Name:");
        projectNameLabel.getStyleClass().addAll("h5", "text-primary", "mb-2");

        TextField projectNameField = new TextField();
        projectNameField.setPromptText("Enter a unique project name");
        projectNameField.getStyleClass().add("form-control");

        // Add components to dialog content
        dialogContent.getChildren().addAll(
                projectTypeLabel, projectTypeOptions,
                projectNameLabel, projectNameField
        );

        // Set dialog content
        dialog.getDialogPane().setContent(dialogContent);

        // Add buttons with styling
        ButtonType okButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);

        // Apply BootstrapFX styles to buttons
        Button okButton = (Button) dialog.getDialogPane().lookupButton(okButtonType);
        okButton.getStyleClass().addAll("btn", "btn-success");

        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);
        cancelButton.getStyleClass().addAll("btn", "btn-outline-secondary");

        // Show dialog and handle response
        dialog.showAndWait().ifPresent(response -> {
            if (response == okButtonType) {
                String projectName = projectNameField.getText().trim();
                RadioButton selectedType = (RadioButton) projectTypeGroup.getSelectedToggle();

                if (!projectName.isEmpty() && selectedType != null) {
                    mainController.createNewProject(projectName);
                    openMainView(selectedType.getText());
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill all fields.", ButtonType.OK);
                    alert.showAndWait();
                }
            }
        });
    }
    /**
     * Opens an existing project by initializing the main view and setting up
     * the controller to load the project data.
     */
    private void openExistingProject() {
        String projectName = "ClassDiagram";
        String projectType = "Class Diagram";

        mainController.createNewProject(projectName);

        MainView mainView = new MainView(mainController, projectType); // Reuse the existing controller
        this.mainController.setMainView(mainView);

        primaryStage.setScene(new Scene(mainView, 800, 600));

        if(mainController.getmainview()==null)
        {
            System.out.println("Main view is nll baby");
        }
        mainController.loadProject();
    }
    /**
     * Opens the main view of the application and initializes the selected project type.
     *
     * @param projectType the type of the project, either "Class Diagram" or "Package Diagram"
     */
    private void openMainView(String projectType) {
        MainView mainView = new MainView(mainController, projectType); // Reuse the existing controller
        this.mainController.setMainView(mainView);
        primaryStage.setScene(new Scene(mainView, 800, 600));

        // Optionally handle project type
        if ("Class Diagram".equals(projectType)) {
            mainController.addClassDiagram();
        } else if ("Package Diagram".equals(projectType)) {
            mainController.addPackageDiagram();
        }
    }
}