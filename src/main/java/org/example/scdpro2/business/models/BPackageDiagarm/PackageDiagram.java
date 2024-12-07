package org.example.scdpro2.business.models.BPackageDiagarm;

import org.example.scdpro2.business.models.Diagram;
import org.example.scdpro2.business.models.DiagramType;
import org.example.scdpro2.business.models.BClassDiagarm.Relationship;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PackageDiagram extends Diagram implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<PackageComponent> packages;
    private List<BPackageRelationShip> relationships;

    public PackageDiagram(String title) {
        super(title);
        this.packages = new ArrayList<>();
        this.relationships = new ArrayList<>();
    }

    public void addPackage(PackageComponent packageComponent) {
        System.out.println("Adding package: " + packageComponent.getName());
        packages.add(packageComponent);
    }

    public void removePackage(PackageComponent packageComponent) {
        packages.remove(packageComponent);
        //relationships.removeIf(rel -> rel.getSource() == packageComponent || rel.getTarget() == packageComponent);
    }

    public void addRelationship(BPackageRelationShip relationship) {
        relationships.add(relationship);
    }

    public void removeRelationship(BPackageRelationShip relationship) {
        relationships.remove(relationship);
    }

    public List<PackageComponent> getPackages() {
        return packages;
    }

    public List<BPackageRelationShip> getRelationships() {
        return relationships;
    }

    @Override
    public void render() {
        // Rendering logic for UI
    }

    @Override
    public String toCode() {
        StringBuilder codeBuilder = new StringBuilder();
        for (PackageComponent pkg : packages) {
            codeBuilder.append(pkg.toCode()).append("\n");
        }
        for (BPackageRelationShip rel : relationships) {
            codeBuilder.append("// Relationship: ").append(rel).append("\n");
        }
        return codeBuilder.toString();
    }

    @Override
    public DiagramType getType() {
        return DiagramType.PACKAGE_DIAGRAM;
    }
}
