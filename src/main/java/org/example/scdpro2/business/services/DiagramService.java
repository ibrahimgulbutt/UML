package org.example.scdpro2.business.services;

import org.example.scdpro2.business.models.*;
import org.example.scdpro2.business.models.BClassDiagarm.BClassBox;
import org.example.scdpro2.business.models.BClassDiagarm.Relationship;
import org.example.scdpro2.business.models.BPackageDiagarm.PackageComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The DiagramService class provides functionality to manage {@link Diagram} objects
 * within the context of a {@link Project}. It allows adding and removing diagrams,
 * as well as managing relationships between diagrams.
 */
public class DiagramService {
    /**
     * The currently loaded {@link Project}, which contains the diagrams managed by this service.
     */
    private Project currentProject;

    /**
     * Constructs a new DiagramService instance with no project initially loaded.
     */
    public DiagramService() {
        this.currentProject = null;
    }
    /**
     * Sets the currently active {@link Project}.
     *
     * @param project the {@link Project} to set as the current project.
     */
    public void setCurrentProject(Project project) {
        this.currentProject = project;
    }
    /**
     * Retrieves the currently active {@link Project}.
     *
     * @return the current {@link Project}, or {@code null} if no project is loaded.
     */
    public Project getCurrentProject() {
        return currentProject;
    }
    /**
     * Adds a {@link Diagram} to the currently active {@link Project}.
     *
     * @param diagram the {@link Diagram} to add.
     * @throws IllegalStateException if no project is currently loaded.
     */
    public void addDiagram(Diagram diagram) {
        if (currentProject == null) {
            throw new IllegalStateException("No project is loaded. Please create or load a project first.");
        }
        currentProject.addDiagram(diagram);
    }
    /**
     * Removes a {@link Diagram} from the currently active {@link Project}.
     *
     * @param diagram the {@link Diagram} to remove. If no project is loaded, this method does nothing.
     */
    public void removeDiagram(Diagram diagram) {
        if (currentProject != null) {
            currentProject.removeDiagram(diagram);
        }
    }
    /**
     * Removes a relationship between two {@link Diagram} objects, if it exists.
     *
     * @param source the source {@link Diagram}.
     * @param target the target {@link Diagram}.
     */
    public void removeRelationship(Diagram source, Diagram target) {
        if (source == null || target == null) return;

        if (source instanceof PackageComponent sourcePackage && target instanceof PackageComponent targetPackage) {
            //sourcePackage.getRelationships().removeIf(rel -> rel.getTarget() == target);
            //targetPackage.getRelationships().removeIf(rel -> rel.getSource() == source);
        }

        if (source instanceof BClassBox sourceClass && target instanceof BClassBox targetClass) {
            sourceClass.getRelationships().removeIf(rel -> rel.getTargetDiagram().equals(target));
            targetClass.getRelationships().removeIf(rel -> rel.getSourceDiagram().equals(source));
        }

        System.out.println("Removed relationship between " + source.getTitle() + " and " + target.getTitle());
    }
}
