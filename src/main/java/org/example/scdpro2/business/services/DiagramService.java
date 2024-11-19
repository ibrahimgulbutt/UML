package org.example.scdpro2.business.services;

import org.example.scdpro2.business.models.ClassDiagram;
import org.example.scdpro2.business.models.Diagram;
import org.example.scdpro2.business.models.Project;
import org.example.scdpro2.business.models.Relationship;

import java.util.Optional;

public class DiagramService {
    private Project currentProject;

    public DiagramService() {
        this.currentProject = null;
    }

    public void setCurrentProject(Project project) {
        this.currentProject = project;
    }

    public Project getCurrentProject() {
        return currentProject;
    }

    public void addDiagram(Diagram diagram) {
        if (currentProject == null) {
            throw new IllegalStateException("No project is loaded. Please create or load a project first.");
        }
        currentProject.addDiagram(diagram);
    }

    public void removeDiagram(Diagram diagram) {
        if (currentProject != null) {
            currentProject.removeDiagram(diagram);
        }
    }

    public Optional<Diagram> findDiagramByTitle(String title) {
        if (currentProject == null) return Optional.empty();
        return currentProject.getDiagrams().stream()
                .filter(diagram -> diagram.getTitle().equalsIgnoreCase(title))
                .findFirst();
    }

    public void addRelationship(Relationship relationship) {
        if (currentProject == null) {
            throw new IllegalStateException("No project is loaded. Please create or load a project first.");
        }
        currentProject.addRelationship(relationship);
    }

    public void removeRelationship(ClassDiagram source, ClassDiagram target) {
        if (source == null || target == null) return;

        source.getRelationships().removeIf(rel -> rel.getSourceDiagram().equals(source) && rel.getTargetDiagram().equals(target));
        target.getRelationships().removeIf(rel -> rel.getSourceDiagram().equals(source) && rel.getTargetDiagram().equals(target));

        System.out.println("Removed relationship between " + source.getTitle() + " and " + target.getTitle());
    }
}
