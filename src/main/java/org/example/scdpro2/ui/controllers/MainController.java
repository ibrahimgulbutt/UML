package org.example.scdpro2.ui.controllers;

import org.example.scdpro2.business.models.ClassDiagram;
import org.example.scdpro2.business.models.Relationship;
import org.example.scdpro2.business.services.DiagramService;
import org.example.scdpro2.data.dao.ProjectDAOImpl;
import org.example.scdpro2.business.services.ProjectService;
import org.example.scdpro2.business.services.CodeGenerationService;
import org.example.scdpro2.business.models.Project;
import org.example.scdpro2.business.models.Diagram;
import org.example.scdpro2.ui.views.ClassBox;
import org.example.scdpro2.ui.views.ClassDiagramPane;
import org.example.scdpro2.ui.views.RelationshipLine;

public class MainController {
    private ProjectService projectService;
    private CodeGenerationService codeGenerationService;
    private ProjectDAOImpl projectDAO;
    private final DiagramService diagramService;

    public MainController(DiagramService diagramService) {
        this.diagramService = diagramService;
        this.projectDAO = new ProjectDAOImpl();
        this.projectService = new ProjectService(projectDAO);
        this.codeGenerationService = new CodeGenerationService();

        // Automatically create a new project at startup
        Project newProject = projectService.createProject("AutoCreatedProject");
        diagramService.setCurrentProject(newProject);  // Set current project in DiagramService
        System.out.println("New Project Created at Startup: " + newProject.getName());
    }

    public void createNewProject() {
        Project project = projectService.createProject("New Project");
        System.out.println("New Project Created: " + project.getName());
    }

    public void saveProject() {
        Project project = projectService.getCurrentProject();
        if (project != null) {
            projectService.saveProject(project);
            System.out.println("Project Saved");
        }
    }

    public void loadProject() {
        Project project = projectService.loadProject("Sample Project").orElse(null);
        if (project != null) {
            System.out.println("Project Loaded: " + project.getName());
        } else {
            System.out.println("Project not found");
        }
    }

    // New method to generate code for the current diagram
    public void generateCode() {
        Project project = projectService.getCurrentProject();
        if (project != null && !project.getDiagrams().isEmpty()) {
            Diagram diagram = project.getDiagrams().get(0); // For demonstration, we use the first diagram
            String generatedCode = codeGenerationService.generateCode(diagram);
            System.out.println("Generated Code:\n" + generatedCode);
        } else {
            System.out.println("No diagram available for code generation.");
        }
    }
    
    // Method to add a new ClassBox to the ClassDiagramPane
    public void addClassBox(ClassDiagramPane diagramPane) {
        // Ensure a project is initialized
        if (projectService.getCurrentProject() == null) {
            projectService.createProject("Untitled Project");
        }

        // Create a new ClassDiagram and add it to the business layer
        ClassDiagram classDiagram = new ClassDiagram("NewClass");
        diagramService.addDiagram(classDiagram);  // Add to the service

        // Create a ClassBox and add it to the UI layer
        ClassBox classBox = new ClassBox(classDiagram);
        diagramPane.addClassBox(classBox); // Ensure click handler is registered
        diagramPane.getChildren().add(classBox);
        System.out.println("ClassBox added for: " + classDiagram.getTitle());
    }

    public void createRelationship(ClassDiagramPane pane, ClassBox source, ClassBox target, RelationshipLine.RelationshipType type) {
        if (source == null || target == null || type == null) {
            System.out.println("Error: Invalid relationship parameters");
            return;
        }

        System.out.println("Creating relationship from " + source.getClassDiagram().getTitle() +
                " to " + target.getClassDiagram().getTitle() + " of type " + type);

        pane.addRelationship(source, target, type);

        // Update the business layer
        Relationship relationship = new Relationship(source.getClassDiagram(), target.getClassDiagram(), type);
        diagramService.addRelationship(relationship);
    }




}
