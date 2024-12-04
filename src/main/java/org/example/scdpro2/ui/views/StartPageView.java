package org.example.scdpro2.ui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.scdpro2.business.services.DiagramService;
import org.example.scdpro2.ui.controllers.MainController;

import java.io.File;

public class StartPageView extends BorderPane {
    private final Stage primaryStage;
    private final MainController mainController;

    public StartPageView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        DiagramService diagramService = new DiagramService();
        this.mainController = new MainController(diagramService);

        initializeUI();
    }

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

    private void showNewProjectDialog() {
        String projectName = "ClassDiagram";
        String projectType = "Class Diagram";

        mainController.createNewProject(projectName);
        openMainView(projectType);
    }

    private void openExistingProject() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Existing Project");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Project Files", "*.proj"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            mainController.loadProjectFromFile(selectedFile);
            openMainView(null);
        }
    }

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