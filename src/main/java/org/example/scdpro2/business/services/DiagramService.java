package org.example.scdpro2.business.services;

import org.example.scdpro2.business.models.*;
import org.example.scdpro2.business.models.BClassDiagarm.BClassBox;
import org.example.scdpro2.business.models.BClassDiagarm.Relationship;
import org.example.scdpro2.business.models.BPackageDiagarm.PackageComponent;

import java.util.ArrayList;
import java.util.List;
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
