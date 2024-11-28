package org.example.scdpro2.ui.views;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
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
        this.mainController = new MainController(diagramService); // Create the controller

        initializeUI();
    }

    private void initializeUI() {
        Label titleLabel = new Label("Welcome to SCDPro2");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button newProjectButton = new Button("New Project");
        newProjectButton.setOnAction(event -> showNewProjectDialog());

        Button openProjectButton = new Button("Open Project");
        openProjectButton.setOnAction(event -> openExistingProject());

        VBox buttonContainer = new VBox(15, titleLabel, newProjectButton, openProjectButton);
        buttonContainer.setStyle("-fx-alignment: center;");
        buttonContainer.setPadding(new Insets(20));
        setCenter(buttonContainer);
    }

    private void showNewProjectDialog() {
        String projectName = "ClassDiagram";
        String projectPath = "C:\\Users\\DeLL\\Desktop";
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
        MainView mainView = new MainView(mainController,projectType); // Reuse the existing controller
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
