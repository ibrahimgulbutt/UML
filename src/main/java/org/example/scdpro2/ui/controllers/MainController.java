package org.example.scdpro2.ui.controllers;

import javafx.stage.FileChooser;
import org.example.scdpro2.business.models.*;
import org.example.scdpro2.business.models.BClassDiagarm.BClassBox;
import org.example.scdpro2.business.models.BClassDiagarm.Relationship;
import org.example.scdpro2.business.models.BPackageDiagarm.BPackageRelationShip;
import org.example.scdpro2.business.models.BPackageDiagarm.PackageClassComponent;
import org.example.scdpro2.business.models.BPackageDiagarm.PackageComponent;
import org.example.scdpro2.business.services.DiagramService;
import org.example.scdpro2.data.dao.ProjectDAOImpl;
import org.example.scdpro2.business.services.ProjectService;
import org.example.scdpro2.business.services.CodeGenerationService;
import org.example.scdpro2.ui.views.*;
import org.example.scdpro2.ui.views.ClassDiagram.ClassBox;
import org.example.scdpro2.ui.views.ClassDiagram.ClassDiagramPane;
import org.example.scdpro2.ui.views.ClassDiagram.RelationshipLine;
import org.example.scdpro2.ui.views.PackageDiagram.PackageBox;
import org.example.scdpro2.ui.views.PackageDiagram.PackageClassBox;
import org.example.scdpro2.ui.views.PackageDiagram.PackageDiagramPane;
import org.example.scdpro2.ui.views.PackageDiagram.PackageRelationship;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.List;
/**
 * The {@code MainController} class serves as the primary controller for managing
 * interactions between the UI and business logic in the diagram management application.
 * It handles the creation, saving, loading, and manipulation of projects and diagrams.
 */
public class MainController {
    private ProjectService projectService;
    private CodeGenerationService codeGenerationService;
    private final DiagramService diagramService;

    private ProjectDAOImpl projectDAO;

    private MainView mainView;

    private List<RelationshipLine> relationshipLines = new ArrayList<>();
    public List<Relationship> relationships = new ArrayList<>();
    public Map<RelationshipLine, Relationship> ClassRelationshipMapping = new HashMap<>();
    public Map<PackageRelationship, BPackageRelationShip> PackageRelationshipMapping = new HashMap<>();

    private static int countclasses = 1;
    private static int countinterface = 1;
    private static int countpackage = 1;
    private static int countpackageclass = 1;

    /**
     * Constructs a new {@code MainController} with the specified {@code DiagramService}.
     *
     * @param diagramService the diagram service responsible for managing diagram-related operations.
     */
    public MainController(DiagramService diagramService) {
        this.diagramService = diagramService;
        this.projectDAO = new ProjectDAOImpl();
        this.projectService = new ProjectService(projectDAO);
        this.codeGenerationService = new CodeGenerationService();

    }

    /**
     * Sets the {@code MainView} for this controller.
     *
     * @param mainView the main view associated with this controller.
     */
    public void setMainView(MainView mainView) {
        this.mainView = mainView;
    }
    /**
     * Creates a new project with a default name and logs its creation.
     */
    public void createNewProject() {
        Project project = projectService.createProject("New Project");
        System.out.println("New Project Created: " + project.getName());
    }
    /**
     * Retrieves the {@code DiagramService} instance used by this controller.
     *
     * @return the {@code DiagramService} instance.
     */
    public DiagramService getDiagramService() {
        return diagramService;
    }
    /**
     * Saves the current class project to the specified file. The method serializes the project's state,
     * including diagrams and relationships, and writes it to the file.
     *
     * @param file the file to which the project will be saved.
     */
    public void saveClassProjectToFile(File file) {
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
            List<Relationship> projectRelationships = new ArrayList<>(ClassRelationshipMapping.values());
            currentProject.setBClasssRelationships(projectRelationships); // Replace or update relationships in the project

            // Serialize project
            oos.writeObject(currentProject);
            System.out.println("Project saved successfully to: " + file.getPath());
        } catch (IOException e) {
            System.err.println("Error saving project: " + e.getMessage());
        }
    }
    /**
     * Saves the current package project to the specified file. The method serializes package diagrams
     * and their relationships, ensuring all UI-related data is persisted.
     *
     * @param file the file to which the project will be saved.
     */
    public void savePackageProjectToFile(File file) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            Project currentProject = diagramService.getCurrentProject();
            if (currentProject == null) {
                System.out.println("No project to save.");
                return;
            }

            // Save UI-related data to business model
            for (Diagram diagram : currentProject.getDiagrams()) {
                System.out.println("i ma holding a project ");
                if (diagram instanceof PackageComponent packageComponent) {
                    System.out.println("I have package component object ");
                    // Save additional package-specific data
                    PackageBox packageBox = mainView.getPackageDiagramPane().getPackageBoxForDiagram(packageComponent);
                    if (packageBox != null) {
                        System.out.println("1 package is being added ");
                        packageComponent.setName(packageBox.getName());
                        packageComponent.setX(packageBox.getLayoutX());
                        packageComponent.setY(packageBox.getLayoutY());
                        packageComponent.setWidth(packageBox.getWidth());
                        packageComponent.setHeight(packageBox.getHeight());
                        int k=0;
                        List<PackageClassBox> packageClassBoxes = new ArrayList<>(packageBox.getPackageClassBoxes());
                        for (PackageClassBox pcb : packageClassBoxes) {
                            System.out.println("package class box is being saved: " + k++);
                            pcb.getPackageClassComponent().xCoordinates=pcb.getLayoutX();
                            pcb.getPackageClassComponent().yCoordinates=pcb.getLayoutY();
                            pcb.getPackageClassComponent().width=pcb.getPrefWidth();
                            pcb.getPackageClassComponent().setName(pcb.getId());
                            packageComponent.addClassBox(pcb.getPackageClassComponent());
                        }


                    }
                }
            }

            ArrayList<BPackageRelationShip> projectRelationships = new ArrayList<>(PackageRelationshipMapping.values());
            for(BPackageRelationShip bpw: projectRelationships)
            {
                System.out.println(bpw.startPackagename+" :::: " + bpw.endPackagename);
            }
            System.out.println("save project keh waqat : "+projectRelationships.size());
            currentProject.setBPackageRelationships(projectRelationships); // Replace or update relationships in the project

            // Write the entire project to the file
            oos.writeObject(currentProject);
            System.out.println("Package project saved successfully to " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error while saving package project: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * Loads a package project from the specified file and updates the UI accordingly.
     *
     * @param file the file from which the project will be loaded.
     */
    public void loadPackageProjectFromFile(File file) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            // Read the Project object from the file
            Project loadedProject = (Project) ois.readObject();

            if (loadedProject == null) {
                System.out.println("No project found in the file.");
                return;
            }

            // Set the loaded project as the current project
            diagramService.setCurrentProject(loadedProject);

            // Restore package-specific UI elements
            for (Diagram diagram : loadedProject.getDiagrams()) {
                if (diagram instanceof PackageComponent packageComponent) {
                    PackageBox packageBox = mainView.getPackageDiagramPane().createPackageBoxForDiagram(packageComponent);
                    if (packageBox != null)
                    {
                        packageBox.setName(packageComponent.getName());
                        packageBox.setId(packageComponent.getName());
                        packageBox.updateBox(packageComponent.x,packageComponent.y,packageComponent.getWidth(),packageComponent.getHeight());
                        for(PackageClassComponent pcc : packageComponent.getPackageClassComponents())
                        {
                            packageBox.addClassBoxforload(pcc,pcc.xCoordinates,pcc.yCoordinates,pcc.width,pcc.getName());
                        }
                    }
                }
            }

            // Restore package relationships
            List<BPackageRelationShip> restoredRelationships = loadedProject.getBPackageRelationships();
            if (restoredRelationships != null)
            {
                System.out.println(restoredRelationships.size());
                PackageRelationshipMapping.clear(); // Clear existing relationships
                for (BPackageRelationShip relationship : restoredRelationships)
                {
                    System.out.println("Package relationship is being loaded.");
                    mainView.packageDiagramPane.createRelationship(mainView.packageDiagramPane.findNodeById(relationship.getStartPackageid()),mainView.packageDiagramPane.findNodeById(relationship.getEndPackageid()));
                }
            }
            else {

                System.out.println("Restored relationships are null");
            }

            System.out.println("Package project loaded successfully from " + file.getAbsolutePath());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error while loading package project: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads a class project from the specified file and updates the UI with the restored data.
     *
     * @param file the file from which the project will be loaded.
     */
    public void loadClassProjectFromFile(File file) {
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
                for (Relationship relationship : loadedProject.getBClasssRelationships()) {
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
    /**
     * Saves the current project to a user-selected file. The method distinguishes between class
     * and package projects based on the active diagram pane.
     */
    public void saveProject() {
        File file = new FileChooser().showSaveDialog(null);
        if (file != null&&mainView.classDiagramPane!=null) {
            saveClassProjectToFile(file);
        } else if (file != null&&mainView.packageDiagramPane!=null)
        {
            savePackageProjectToFile(file);
        }

    }
    /**
     * Loads a project from a user-selected file. The method distinguishes between class and package
     * projects based on the active diagram pane.
     */
    public void loadProject() {
        File file = new FileChooser().showOpenDialog(null);
        if (file != null && mainView.packageDiagramPane!=null) {
            mainView.packageDiagramPane.clearDiagrams();
            loadPackageProjectFromFile(file);
        }
        else if (file != null && mainView.classDiagramPane!=null) {
            mainView.classDiagramPane.clearDiagrams();
            mainView.classListView.getItems().clear();
            loadClassProjectFromFile(file);
        }
    }
    /**
     * Creates a new project with the specified name. If the main view is set, updates the class list view.
     *
     * @param projectName the name of the new project.
     */
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
    /**
     * Generates code from the current project using the {@code CodeGenerationService}.
     *
     * @return the generated code as a {@code String}, or {@code null} if no diagrams are available.
     */
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

    /**
     * Adds a new class box to the class diagram pane and the underlying project structure.
     *
     * @param diagramPane the pane to which the class box will be added.
     */
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
    /**
     * Adds a new interface box to the class diagram pane and the underlying project structure.
     *
     * @param diagramPane the pane to which the interface box will be added.
     */
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
    /**
     * Deletes a specified class box along with all associated relationships from the class diagram pane and project structure.
     *
     * @param pane      the diagram pane from which the class box will be removed.
     * @param classBox  the class box to be deleted.
     */
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

    /**
     * Creates a relationship between two class boxes and adds it to both the UI and the business layer.
     *
     * @param pane        the diagram pane where the relationship will be visualized.
     * @param source      the source class box.
     * @param sourceSide  the side of the source class box where the relationship originates.
     * @param target      the target class box.
     * @param targetSide  the side of the target class box where the relationship terminates.
     * @param type        the type of relationship being created.
     */
    public void createRelationship(ClassDiagramPane pane, ClassBox source, String sourceSide, ClassBox target, String targetSide, RelationshipLine.RelationshipType type) {
        int relationshipIndex = countRelationshipsBetween(source, target);
        System.out.println(relationshipIndex);
        RelationshipLine line = new RelationshipLine(source, sourceSide, target, targetSide, type, 0, 0, relationshipIndex);

        // Create the Relationship (business layer)
        Relationship relationship = new Relationship(source.BClassBox, target.BClassBox, type,line.getMultiplicityStart(),line.getMultiplicityEnd(),line.getRelationshipLabel());
        relationships.add(relationship);  // Track the new Relationship object

        source.BClassBox.addRelationship(relationship);

        ClassRelationshipMapping.put(line, relationship);


        System.out.println("Line is being created "+ relationshipIndex);
        line.setMainView(mainView);
        relationshipLines.add(line); // Track the new relationship
        pane.getChildren().add(line); // Add to UI
    }
    /**
     * Counts the number of relationships between two specified class boxes.
     *
     * @param source the source class box.
     * @param target the target class box.
     * @return the number of relationships between the source and target.
     */
    public int countRelationshipsBetween(ClassBox source, ClassBox target) {
        return (int) relationshipLines.stream()
                .filter(rel -> (rel.getSource() == source && rel.getTarget() == target) || (rel.getSource() == target && rel.getTarget() == source))
                .count();
    }
    /**
     * Retrieves the current project from the project service.
     *
     * @return the current project or null if no project is initialized.
     */
    public Project getCurrentProject() {
        return projectService.getCurrentProject();
    }
    /**
     * Updates the main view to reflect changes in the class list, typically after adding a class diagram.
     */
    public void addClassDiagram() {
        if (mainView != null) {
            mainView.updateClassListView();
        }
    }
    /**
     * Updates the main view to reflect changes in the package list, typically after adding a package diagram.
     */
    public void addPackageDiagram() {
        if (mainView != null) {
            mainView.updateClassListView();
        }
    }

    /**
     * Adds a new package box to the package diagram pane and updates the business layer with the package component.
     *
     * @param diagramPane the pane to which the package box will be added.
     */
    public void addPackageBox(PackageDiagramPane diagramPane) {
        if (projectService.getCurrentProject() == null) {
            projectService.createProject("Untitled Project");
        }


        String packageName = "NewPackage " + countpackage;
        PackageComponent newPackage = new PackageComponent(packageName);
        PackageBox packageBox = new PackageBox(newPackage, this, diagramPane);
        packageBox.setId(packageName);
        packageBox.setPackageComponentid(packageName);
        diagramPane.addPackageBox(packageBox);
        diagramService.addDiagram(newPackage);

        countpackage++;

        mainView.addClassToList(packageName);
        System.out.println("Package added: " + packageName);
    }
    /**
     * Adds a new class to an existing package box in the package diagram pane.
     *
     * @param diagramPane      the pane to which the package class box will be added.
     * @param packageBox       the package box where the class will be added.
     * @param packageComponent the package component associated with the class.
     * @return the created package class box.
     */
    public PackageClassBox addPackageClassBox(PackageDiagramPane diagramPane, PackageBox packageBox,PackageComponent packageComponent) {
        if (projectService.getCurrentProject() == null) {
            projectService.createProject("Untitled Project");
        }

        String packageName = "NewCLass " + countpackageclass;
        PackageClassComponent newPackage = new PackageClassComponent(packageComponent,packageName,"+");
        diagramService.addDiagram(newPackage);
        countpackageclass++;

        PackageClassBox classBox = new PackageClassBox(packageBox,newPackage);

        classBox.setId(packageName);

        mainView.addClassToList(packageName);
        System.out.println("Package added: " + packageName);
        return classBox;
    }
    /**
     * Adds a new class to an existing package box using an existing package class component.
     *
     * @param diagramPane      the pane to which the package class box will be added.
     * @param packageBox       the package box where the class will be added.
     * @param packageComponent the package component associated with the class.
     * @param pcc              the package class component to be added.
     * @return the created package class box.
     */
    public PackageClassBox addPackageClassBox(PackageDiagramPane diagramPane, PackageBox packageBox,PackageComponent packageComponent,PackageClassComponent pcc)
    {
        if (projectService.getCurrentProject() == null) {
            projectService.createProject("Untitled Project");
        }

        PackageClassComponent newPackage = pcc;

        PackageClassBox classBox = new PackageClassBox(packageBox,newPackage);

        mainView.addClassToList(pcc.getName());
        return classBox;
    }
    /**
     * Retrieves the main view of the application.
     *
     * @return the main view instance.
     */
    public MainView getmainview() {
        return mainView;
    }
    /**
     * Adds a new relationship between two packages to the business layer and updates the mapping.
     *
     * @param relationship the package relationship to be added.
     */
    public void addPackageRelationship(PackageRelationship relationship) {

        BPackageRelationShip bPackageRelationShip = new BPackageRelationShip<>(relationship.startPackage.getId(),relationship.endPackage.getId());
        bPackageRelationShip.startPackagename=relationship.startPackage.getId();
        bPackageRelationShip.endPackagename=relationship.endPackage.getId();
        PackageRelationshipMapping.put(relationship,bPackageRelationShip);

    }

    public void setCodeGenerationService(CodeGenerationService codeGenerationService) {
        this.codeGenerationService = codeGenerationService;
    }

    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }
}
