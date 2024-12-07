package org.example.scdpro2.business.services;

import org.example.scdpro2.business.models.*;
import org.example.scdpro2.business.models.BClassDiagarm.BClassBox;
import org.example.scdpro2.business.models.BClassDiagarm.Relationship;
import org.example.scdpro2.business.models.BPackageDiagarm.PackageComponent;
import org.example.scdpro2.business.models.BPackageDiagarm.PackageDiagram;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DiagramService {
    private Project currentProject;
    private List<PackageDiagram> packageDiagrams = new ArrayList<>();

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

        //if (relationship.getSource() instanceof PackageComponent sourcePackage) {
        //    sourcePackage.addRelationship(relationship);
        //}

        //if (relationship.getTarget() instanceof PackageComponent targetPackage) {
        //    targetPackage.addRelationship(relationship);
        //}
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

    public void addPackageDiagram(PackageDiagram diagram) {
        if (!packageDiagrams.contains(diagram)) {
            packageDiagrams.add(diagram);
        }
    }

    public List<PackageDiagram> getPackageDiagrams() {
        return packageDiagrams;
    }

    public PackageDiagram getOrCreateActivePackageDiagram() {
        if (getPackageDiagrams().isEmpty()) {
            PackageDiagram newDiagram = new PackageDiagram("Default Package Diagram");
            addPackageDiagram(newDiagram);
        }
        return getPackageDiagrams().get(0); // Return the first package diagram
    }
}
