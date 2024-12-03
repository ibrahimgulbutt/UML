package org.example.scdpro2.ui.controllers;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import org.example.scdpro2.business.models.*;
import org.example.scdpro2.business.services.DiagramService;
import org.example.scdpro2.data.dao.ProjectDAOImpl;
import org.example.scdpro2.business.services.ProjectService;
import org.example.scdpro2.business.services.CodeGenerationService;
import org.example.scdpro2.ui.views.*;

import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.List;

public class MainController {
    private ProjectService projectService;
    private CodeGenerationService codeGenerationService;
    private ProjectDAOImpl projectDAO;
    private final DiagramService diagramService;
    private MainView mainView;
    private List<RelationshipLine> relationships = new ArrayList<>();

    private static int countclasses = 1;
    private static int countinterface = 1;


    public MainController(DiagramService diagramService) {
        this.diagramService = diagramService;
        this.projectDAO = new ProjectDAOImpl();
        this.projectService = new ProjectService(projectDAO);
        this.codeGenerationService = new CodeGenerationService();

    }

    public void setMainView(MainView mainView) {
        this.mainView = mainView;
    }

    public void createNewProject() {
        Project project = projectService.createProject("New Project");
        System.out.println("New Project Created: " + project.getName());
    }

    public DiagramService getDiagramService() {
        return diagramService;
    }

    public void saveProjectToFile(File file) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            Project currentProject = diagramService.getCurrentProject();
            if (currentProject == null) {
                System.out.println("No project to save.");
                return;
            }

            // Save diagram positions
            for (Diagram diagram : currentProject.getDiagrams()) {
                if (diagram instanceof ClassDiagram classDiagram) {
                    ClassBox classBox = mainView.getClassDiagramPane().getClassBoxForDiagram(classDiagram);
                    if (classBox != null) {
                        classDiagram.setX(classBox.getLayoutX());
                        classDiagram.setY(classBox.getLayoutY());
                    }
                }
            }

            // Log relationships for debugging
            System.out.println("Serializing relationships:");
            if (currentProject.getRelationships().isEmpty()) {
                System.out.println("No relationships to serialize.");
            } else {
                for (Relationship relationship : currentProject.getRelationships()) {
                    System.out.println("Relationship: Source = " + relationship.getSource().getTitle() +
                            ", Target = " + relationship.getTarget().getTitle() +
                            ", Type = " + relationship.getType());
                }
            }


            oos.writeObject(currentProject); // Serialize the entire project
            System.out.println("Project saved successfully to: " + file.getPath());
        } catch (IOException e) {
            System.err.println("Error saving project: " + e.getMessage());
        }
    }

    public void loadProjectFromFile(File file) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Project loadedProject = (Project) ois.readObject();
            if (loadedProject != null) {
                diagramService.setCurrentProject(loadedProject);
                mainView.getClassDiagramPane().clearDiagrams(); // Clear existing UI

                // Redraw diagrams (classes/interfaces)
                for (Diagram diagram : loadedProject.getDiagrams()) {
                    if (diagram instanceof ClassDiagram classDiagram) {
                        ClassBox classBox = new ClassBox(classDiagram, this, mainView.getClassDiagramPane());
                        classBox.setLayoutX(classDiagram.getX());
                        classBox.setLayoutY(classDiagram.getY());
                        mainView.getClassDiagramPane().addClassBox(classBox);
                    }
                }

                // Log relationships for debugging
                System.out.println("Deserializing relationships:");
                for (Relationship relationship : loadedProject.getRelationships()) {
                    System.out.println("Relationship: Source = " + relationship.getSource().getTitle() +
                            ", Target = " + relationship.getTarget().getTitle() +
                            ", Type = " + relationship.getType());

                    ClassBox sourceBox = mainView.getClassDiagramPane().getClassBoxForDiagram((ClassDiagram) relationship.getSource());
                    ClassBox targetBox = mainView.getClassDiagramPane().getClassBoxForDiagram((ClassDiagram) relationship.getTarget());
                    if (sourceBox != null && targetBox != null) {
                        mainView.getClassDiagramPane().addRelationship(sourceBox, targetBox, relationship.getType());
                    } else {
                        System.err.println("Error: Source or Target ClassBox not found for relationship.");
                    }
                }

                mainView.updateClassListView(); // Update class list
                System.out.println("Project loaded successfully from: " + file.getPath());
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading project: " + e.getMessage());
        }
    }

    public void createNewProject(String projectName) {
        if (diagramService.getCurrentProject() == null) {
            Project newProject = projectService.createProject(projectName);
            diagramService.setCurrentProject(newProject);
            System.out.println("New project created: " + newProject.getName());
        }

        if (mainView != null) {
            mainView.updateClassListView();
        } else {
            System.err.println("MainView is not set in MainController.");
        }
    }

    public List<String> getClassNames() {
        return diagramService.getCurrentProject().getDiagrams().stream()
                .map(Diagram::getTitle)
                .collect(Collectors.toList());
    }

    // New method to generate code for the current diagram
    public void generateCode() {
        Project project = projectService.getCurrentProject();
        if (project != null && !project.getDiagrams().isEmpty()) {
            String generatedCode = codeGenerationService.generateCode(project);
            System.out.println("Generated Code:\n" + generatedCode);
        } else {
            System.out.println("No diagrams available for code generation.");
        }
    }

    public List<String> getAvailableClassNames() {
        Project currentProject = projectService.getCurrentProject();
        if (currentProject != null) {
            return currentProject.getDiagrams().stream()
                    .filter(diagram -> diagram instanceof ClassDiagram)
                    .map(diagram -> diagram.getTitle())
                    .collect(Collectors.toList());
        }
        return List.of(); // Return an empty list if no project or diagrams
    }

        // Method to add a new ClassBox to the ClassDiagramPane
    public void addClassBox(ClassDiagramPane diagramPane) {
            // Ensure a project is initialized
            if (projectService.getCurrentProject() == null) {
                projectService.createProject("Untitled Project");
            }

            // Create a new ClassDiagram and add it to the business layer
            ClassDiagram classDiagram = new ClassDiagram("Class "+countclasses);
            diagramService.addDiagram(classDiagram);  // Add to the service


            // Create a ClassBox and add it to the UI layer
            ClassBox classBox = new ClassBox(classDiagram, this, diagramPane); // Pass available class names
            diagramPane.addClassBox(classBox); // Ensure click handler is registered
            System.out.println("ClassBox added for: " + classDiagram.getTitle());
            countclasses++;
        }

    public void deleteClassBox(ClassDiagramPane pane, ClassBox classBox) {
        if (classBox == null) {
            System.out.println("Error: ClassBox to delete is null");
            return;
        }

        // Remove all connected relationships
        classBox.deleteConnectedRelationships(pane);
        // Remove all associated relationship lines
        List<RelationshipLine> linesToRemove = pane.getRelationshipLinesConnectedTo(classBox);
        for (RelationshipLine line : linesToRemove) {
            pane.removeRelationshipLine(line);
            diagramService.removeRelationship(line.getSourceDiagram(), line.getTargetDiagram());

        }

        // Remove the ClassBox from the pane
        pane.getChildren().remove(classBox);

        // Remove the ClassDiagram from the business layer
        diagramService.removeDiagram(classBox.getClassDiagram());
        System.out.println("Deleted ClassBox and all associated relationships for: " + classBox.getClassDiagram().getTitle());
        if (mainView != null) {
            mainView.classListView.getItems().remove(classBox.getClassName());
        }
    }

    public void createRelationship(ClassDiagramPane pane, ClassBox source, String sourceSide, ClassBox target, String targetSide, RelationshipLine.RelationshipType type) {
        int relationshipIndex = countRelationshipsBetween(source, target);
        System.out.println(relationshipIndex);
        RelationshipLine line = new RelationshipLine(source, sourceSide, target, targetSide, type, 0, 0, relationshipIndex);

        line.setMainView(mainView);
        relationships.add(line); // Track the new relationship
        pane.getChildren().add(line); // Add to UI
    }

    public void createRelationship(ClassDiagramPane pane, InterfaceBox source, String sourceSide, ClassBox target, String targetSide, RelationshipLine.RelationshipType type) {
        //int relationshipIndex = countRelationshipsBetween(source, target);
        RelationshipLine line = new RelationshipLine(source, sourceSide, target, targetSide, type, 0, 0, 0);

        relationships.add(line); // Track the new relationship
        pane.getChildren().add(line); // Add to UI
    }

    public void createRelationship(ClassDiagramPane pane, ClassBox source, String sourceSide, InterfaceBox target, String targetSide, RelationshipLine.RelationshipType type) {
        //int relationshipIndex = countRelationshipsBetween(source, target);
        RelationshipLine line = new RelationshipLine(source, sourceSide, target, targetSide, type, 0, 0, 0);

        relationships.add(line); // Track the new relationship
        pane.getChildren().add(line); // Add to UI
    }

    private int countRelationshipsBetween(ClassBox source, ClassBox target) {
        return (int) relationships.stream()
                .filter(rel -> (rel.getSource() == source && rel.getTarget() == target) || (rel.getSource() == target && rel.getTarget() == source))
                .count();
    }

    public void addInterfaceBox(ClassDiagramPane diagramPane) {

        if (projectService.getCurrentProject() == null) {
            projectService.createProject("Untitled Project");
        }

        ClassDiagram interfaceDiagram = new ClassDiagram("Interface "+countinterface);
        diagramService.addDiagram(interfaceDiagram);


        InterfaceBox interfaceBox = new InterfaceBox(interfaceDiagram, this ,diagramPane);
        diagramPane.addInterfaceBox(interfaceBox);
        countinterface++;
    }

    public Project getCurrentProject() {
        return projectService.getCurrentProject();
    }

    public void addClassDiagram() {
        ClassDiagram classDiagram = new ClassDiagram("NewClassDiagram");
        diagramService.addDiagram(classDiagram);
        if (mainView != null) {
            mainView.updateClassListView();
        }
        System.out.println("Class Diagram added: " + classDiagram.getTitle());
    }

    public void addPackageDiagram() {
        Project project = diagramService.getCurrentProject();
        if (project == null) {
            throw new IllegalStateException("No project loaded.");
        }
        PackageDiagram packageDiagram = new PackageDiagram("New Package Diagram");
        diagramService.addPackageDiagram(packageDiagram);
        System.out.println("Package diagram added: " + packageDiagram.getTitle());
    }

    public void saveProject() {
        File file = new FileChooser().showSaveDialog(null);
        if (file != null) {
            saveProjectToFile(file);
        }
    }

    public void loadProject() {
        File file = new FileChooser().showOpenDialog(null);
        if (file != null) {
            loadProjectFromFile(file);
        }
    }

    public void addPackageBox(PackageDiagramPane diagramPane) {
        if (projectService.getCurrentProject() == null) {
            projectService.createProject("Untitled Project");
        }

        PackageDiagram packageDiagram = diagramService.getOrCreateActivePackageDiagram();

        String packageName = "NewPackage";
        PackageComponent newPackage = new PackageComponent(packageName);
        packageDiagram.addPackage(newPackage);

        PackageBox packageBox = new PackageBox(newPackage, this, diagramPane);
        diagramPane.addPackageBox(packageBox);

        System.out.println("Package added: " + packageName);
    }

    public void createPackageRelationship(PackageComponent source, PackageComponent target, RelationshipLine.RelationshipType type) {
        Relationship relationship = new Relationship(source, target, type);
        diagramService.addRelationship(relationship);
        System.out.println("Created relationship: " + relationship);
    }

    public void deleteInterfaceBox(ClassDiagramPane pane, InterfaceBox interfaceBox) {
        if (interfaceBox == null) {
            System.out.println("Error: ClassBox to delete is null");
            return;
        }

        // Remove all connected relationships
        interfaceBox.deleteConnectedRelationships(pane);
        // Remove all associated relationship lines
        List<RelationshipLine> linesToRemove = pane.getRelationshipLinesConnectedTo(interfaceBox);
        for (RelationshipLine line : linesToRemove) {
            pane.removeRelationshipLine(line);
            diagramService.removeRelationship(line.getSourceDiagram(), line.getTargetDiagram());

        }

        // Remove the ClassBox from the pane
        pane.getChildren().remove(interfaceBox);

        // Remove the ClassDiagram from the business layer
        diagramService.removeDiagram(interfaceBox.getInterfaceDiagram());
        System.out.println("Deleted ClassBox and all associated relationships for: " + interfaceBox.getInterfaceDiagram().getTitle());
        if (mainView != null) {
            mainView.classListView.getItems().remove(interfaceBox.getClassName());
        }
        }
}
