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
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Create New Project");

        VBox dialogContent = new VBox(10);
        dialogContent.setPadding(new Insets(10));

        Label projectTypeLabel = new Label("Select Project Type:");
        ToggleGroup projectTypeGroup = new ToggleGroup();
        RadioButton classDiagramButton = new RadioButton("Class Diagram");
        classDiagramButton.setToggleGroup(projectTypeGroup);
        RadioButton packageDiagramButton = new RadioButton("Package Diagram");
        packageDiagramButton.setToggleGroup(projectTypeGroup);

        Label projectNameLabel = new Label("Enter Project Name:");
        TextField projectNameField = new TextField();

        Label projectPathLabel = new Label("Select Project Path:");
        Button selectPathButton = new Button("Select Path");
        TextField selectedPathField = new TextField();
        selectedPathField.setEditable(false);
        selectPathButton.setOnAction(e -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(primaryStage);
            if (selectedDirectory != null) {
                selectedPathField.setText(selectedDirectory.getAbsolutePath());
            }
        });

        dialogContent.getChildren().addAll(
                projectTypeLabel, classDiagramButton, packageDiagramButton,
                projectNameLabel, projectNameField,
                projectPathLabel, new HBox(5, selectedPathField, selectPathButton)
        );

        dialog.getDialogPane().setContent(dialogContent);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String projectName = projectNameField.getText().trim();
                String projectPath = selectedPathField.getText().trim();
                RadioButton selectedType = (RadioButton) projectTypeGroup.getSelectedToggle();

                if (!projectName.isEmpty() && !projectPath.isEmpty() && selectedType != null) {
                    mainController.createNewProject(projectName);
                    openMainView(selectedType.getText());
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill all fields.", ButtonType.OK);
                    alert.showAndWait();
                }
            }
        });
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
