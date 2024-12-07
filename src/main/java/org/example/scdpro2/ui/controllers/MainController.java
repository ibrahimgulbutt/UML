package org.example.scdpro2.ui.controllers;

import javafx.stage.FileChooser;
import org.example.scdpro2.business.models.*;
import org.example.scdpro2.business.models.BClassDiagarm.BClassBox;
import org.example.scdpro2.business.models.BClassDiagarm.Relationship;
import org.example.scdpro2.business.models.BPackageDiagarm.PackageComponent;
import org.example.scdpro2.business.models.BPackageDiagarm.PackageDiagram;
import org.example.scdpro2.business.services.DiagramService;
import org.example.scdpro2.data.dao.ProjectDAOImpl;
import org.example.scdpro2.business.services.ProjectService;
import org.example.scdpro2.business.services.CodeGenerationService;
import org.example.scdpro2.ui.views.*;
import org.example.scdpro2.ui.views.ClassDiagram.ClassBox;
import org.example.scdpro2.ui.views.ClassDiagram.ClassDiagramPane;
import org.example.scdpro2.ui.views.ClassDiagram.RelationshipLine;
import org.example.scdpro2.ui.views.PackageDiagram.PackageBox;
import org.example.scdpro2.ui.views.PackageDiagram.PackageDiagramPane;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.List;

public class MainController {
    private ProjectService projectService;
    private CodeGenerationService codeGenerationService;
    private final DiagramService diagramService;

    private ProjectDAOImpl projectDAO;

    private MainView mainView;

    private List<RelationshipLine> relationshipLines = new ArrayList<>();
    public List<Relationship> relationships = new ArrayList<>();
    public Map<RelationshipLine, Relationship> relationshipMapping = new HashMap<>();

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

            // Save UI-related data to business model
            for (Diagram diagram : currentProject.getDiagrams()) {
                if (diagram instanceof BClassBox bClassBox) {
                    if ("Class Box".equals(bClassBox.Type)) {
                        ClassBox classBox = mainView.getClassDiagramPane().getClassBoxForDiagram(bClassBox);
                        if (classBox != null) {
                            bClassBox.setX(classBox.getLayoutX());
                            bClassBox.setY(classBox.getLayoutY());
                            bClassBox.setAttributes(classBox.getAttributes());
                            bClassBox.setOperations(classBox.getOperations());
                        }
                    } else if ("Interface Box".equals(bClassBox.Type)) {
                        ClassBox interfaceBox = mainView.getClassDiagramPane().getClassBoxForDiagram(bClassBox);
                        if (interfaceBox != null) {
                            bClassBox.setX(interfaceBox.getLayoutX());
                            bClassBox.setY(interfaceBox.getLayoutY());
                            bClassBox.setOperations(interfaceBox.getOperations());
                        }
                    }
                }
            }

            // Save project relationships
            // Extract Relationships from relationshipMapping
            List<Relationship> projectRelationships = new ArrayList<>(relationshipMapping.values());
            currentProject.setRelationships(projectRelationships); // Replace or update relationships in the project

            // Serialize project
            oos.writeObject(currentProject);
            System.out.println("Project saved successfully to: " + file.getPath());
        } catch (IOException e) {
            System.err.println("Error saving project: " + e.getMessage());
        }
    }

    private Relationship convertRelationshipLineToRelationship(RelationshipLine relationshipLine) {
        BClassBox source = (BClassBox) relationshipLine.getSource().getDiagram();
        BClassBox target = (BClassBox) relationshipLine.getTarget().getDiagram();
        RelationshipLine.RelationshipType type = relationshipLine.getType();
        return new Relationship(source, target, type,relationshipLine.getMultiplicityStart(),relationshipLine.getMultiplicityEnd(),relationshipLine.getRelationshipLabel());
    }

    public void loadProjectFromFile(File file) {
        System.out.println("LoadProject from filr is called ");
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Project loadedProject = (Project) ois.readObject();
            if (loadedProject != null) {
                diagramService.setCurrentProject(loadedProject);

                // Clear UI components
                mainView.getClassDiagramPane().clearDiagrams();

                if(mainView.getClassDiagramPane()==null)
                {System.out.println("Null class diagram pane");}
                else{System.out.println("not null class diagram pane");}

                // Restore diagrams (class boxes)
                for (Diagram diagram : loadedProject.getDiagrams()) {
                    if (diagram instanceof BClassBox bClassBox) {
                        if ("Class Box".equals(bClassBox.Type)) {
                            ClassBox classBox = new ClassBox(bClassBox, this, mainView.getClassDiagramPane(), bClassBox.Type);
                            classBox.setLayoutX(bClassBox.getX());
                            classBox.setLayoutY(bClassBox.getY());
                            classBox.setAttributes(bClassBox.getAttributes());
                            classBox.setOperations(bClassBox.getOperations());
                            mainView.getClassDiagramPane().addClassBox(classBox);
                            countclasses++;
                        } else if ("Interface Box".equals(bClassBox.Type)) {
                            ClassBox interfaceBox = new ClassBox(bClassBox, this, mainView.getClassDiagramPane(),"Interface Box");
                            interfaceBox.setLayoutX(bClassBox.getX());
                            interfaceBox.setLayoutY(bClassBox.getY());
                            interfaceBox.setOperations(bClassBox.getOperations());
                            mainView.getClassDiagramPane().addClassBox(interfaceBox);
                            countinterface++;
                        }
                    }
                }

                // Restore relationships
                for (Relationship relationship : loadedProject.getRelationships()) {
                    RelationshipLine.RelationshipType type = RelationshipLine.RelationshipType.valueOf(relationship.getTypee());

                    if(relationship.source.Type.equals("Class Box") && relationship.target.Type.equals("Class Box")) {
                        // Find source and target class boxes
                        ClassBox source = mainView.getClassDiagramPane().getClassBoxByTitle(relationship.getSource().getTitle());
                        ClassBox target = mainView.getClassDiagramPane().getClassBoxByTitle(relationship.getTarget().getTitle());

                        if (source != null && target != null) {
                            // Add relationship line
                            System.err.println("trying to create a relationhsio");
                            //mainView.getClassDiagramPane().addRelationship(source, target, type);
                            createRelationship(mainView.classDiagramPane, source, "right", target, "left", relationship.type);
                        } else {
                            System.err.println("Error: Could not find source or target ClassBox for relationship.");
                        }
                    }
                    else if(relationship.source.Type.equals("Class Box") && relationship.target.Type.equals("Interface Box")) {
                        // Find source and target class boxes
                        ClassBox source = mainView.getClassDiagramPane().getClassBoxByTitle(relationship.getSource().getTitle());
                        ClassBox target = mainView.getClassDiagramPane().getClassBoxByTitle(relationship.getTarget().getTitle());

                        if (source != null && target != null) {
                            // Add relationship line
                            System.err.println("trying to create a relationhsio");
                            //mainView.getClassDiagramPane().addRelationship(source, target, type);
                            createRelationship(mainView.classDiagramPane, source, "right", target, "left", relationship.type);
                        } else {
                            System.err.println("Error: Could not find source or target ClassBox for relationship.");
                        }
                    }
                }

                mainView.updateClassListView();
                System.out.println("Project loaded successfully from: " + file.getPath());
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading project: " + e.getMessage());
        }
    }

    public Relationship getRelationshipForLine(RelationshipLine line) {
        return relationshipMapping.get(line);
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
            mainView.classDiagramPane.clearDiagrams();
            mainView.classListView.getItems().clear();
            loadProjectFromFile(file);
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

    public String generateCode() {
        Project project = projectService.getCurrentProject();
        if (project != null && !project.getDiagrams().isEmpty()) {
            // Call the code generation service to generate the code
            return codeGenerationService.generateCode(project); // Return the generated code as a string
        } else {
            System.out.println("No diagrams available for code generation.");
            return null; // Return null if no diagrams are available
        }
    }


    public List<String> getAvailableClassNames() {
        Project currentProject = projectService.getCurrentProject();
        if (currentProject != null) {
            return currentProject.getDiagrams().stream()
                    .filter(diagram -> diagram instanceof BClassBox)
                    .map(diagram -> diagram.getTitle())
                    .collect(Collectors.toList());
        }
        return List.of(); // Return an empty list if no project or diagrams
    }

    public void addClassBox(ClassDiagramPane diagramPane) {
            // Ensure a project is initialized
            if (projectService.getCurrentProject() == null) {
                projectService.createProject("Untitled Project");
            }

            // Create a new ClassDiagram and add it to the business layer
            BClassBox BClassBox = new BClassBox("Class "+ countclasses);
            BClassBox.Type="Class Box";
            diagramService.addDiagram(BClassBox);  // Add to the service


            // Create a ClassBox and add it to the UI layer
            ClassBox classBox = new ClassBox(BClassBox, this, diagramPane, BClassBox.Type); // Pass available class names
            diagramPane.addClassBox(classBox); // Ensure click handler is registered
            System.out.println("ClassBox added for: " + BClassBox.getTitle());
            countclasses++;
        }

    public void addInterfaceBox(ClassDiagramPane diagramPane) {

        if (projectService.getCurrentProject() == null) {
            projectService.createProject("Untitled Project");
        }

        BClassBox interfacebox = new BClassBox("Interface "+countinterface);
        interfacebox.Type="Interface Box";
        diagramService.addDiagram(interfacebox);


        ClassBox classBox = new ClassBox(interfacebox, this, diagramPane, interfacebox.Type); // Pass available class names
        diagramPane.addClassBox(classBox); // Ensure click handler is registered
        System.out.println("InterfaceBox added for: " + interfacebox.getTitle());
        countinterface++;
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

        // Create the Relationship (business layer)
        Relationship relationship = new Relationship(source.BClassBox, target.BClassBox, type,line.getMultiplicityStart(),line.getMultiplicityEnd(),line.getRelationshipLabel());
        relationships.add(relationship);  // Track the new Relationship object

        source.BClassBox.addRelationship(relationship);

        relationshipMapping.put(line, relationship);


        System.out.println("Line is being created "+ relationshipIndex);
        line.setMainView(mainView);
        relationshipLines.add(line); // Track the new relationship
        pane.getChildren().add(line); // Add to UI
    }

    public int countRelationshipsBetween(ClassBox source, ClassBox target) {
        return (int) relationshipLines.stream()
                .filter(rel -> (rel.getSource() == source && rel.getTarget() == target) || (rel.getSource() == target && rel.getTarget() == source))
                .count();
    }

    public Project getCurrentProject() {
        return projectService.getCurrentProject();
    }

    public void addClassDiagram() {
        if (mainView != null) {
            mainView.updateClassListView();
        }
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

    public MainView getmainview() {
        return mainView;
    }
}
