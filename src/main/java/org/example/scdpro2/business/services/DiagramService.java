package org.example.scdpro2.business.services;

import org.example.scdpro2.business.models.ClassDiagram;
import org.example.scdpro2.business.models.Diagram;
import org.example.scdpro2.business.models.Project;
import org.example.scdpro2.business.models.Relationship;

import java.util.Optional;

public class DiagramService {
    private Project currentProject;

    public DiagramService() {
        // Initialize without a project; it will be set later when created or loaded
        this.currentProject = null;
    }

    // Method to set or update the current project
    public void setCurrentProject(Project project) {
        this.currentProject = project;
    }

    public void addDiagram(Diagram diagram) {
        if (currentProject == null) {
            throw new IllegalStateException("No project is loaded. Please create or load a project first.");
        }
        currentProject.addDiagram(diagram);
    }

    public Optional<Diagram> findDiagramByTitle(String title) {
        if (currentProject == null) return Optional.empty();

        return currentProject.getDiagrams().stream()
                .filter(diagram -> diagram.getTitle().equalsIgnoreCase(title))
                .findFirst();
    }

    public void removeDiagram(Diagram diagram) {
        if (currentProject != null) {
            currentProject.removeDiagram(diagram);
        }
    }
    public void addRelationship(Relationship relationship) {
        if (currentProject == null) {
            throw new IllegalStateException("No project is loaded. Please create or load a project first.");
        }
        currentProject.addRelationship(relationship);
    }
    public void removeRelationship(ClassDiagram source, ClassDiagram target) {
        if (source == null || target == null) return;

        // Remove the relationship from the source diagram
        source.getRelationships().removeIf(rel ->
                rel.getSourceDiagram().equals(source) && rel.getTargetDiagram().equals(target)
        );

        // Remove the relationship from the target diagram
        target.getRelationships().removeIf(rel ->
                rel.getSourceDiagram().equals(source) && rel.getTargetDiagram().equals(target)
        );

        System.out.println("Removed relationship between " + source.getTitle() + " and " + target.getTitle());
    }

}
