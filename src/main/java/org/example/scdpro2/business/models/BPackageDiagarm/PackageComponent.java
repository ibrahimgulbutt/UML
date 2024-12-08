package org.example.scdpro2.business.models.BPackageDiagarm;

import org.example.scdpro2.business.models.Diagram;
import org.example.scdpro2.business.models.DiagramType;
import org.example.scdpro2.business.models.BClassDiagarm.Relationship;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PackageComponent extends Diagram implements Serializable {
    private static final long serialVersionUID = 1L;

    public ArrayList<PackageClassComponent> getPackageClassComponents() {
        return packageClassComponents;
    }

    private final String id; // Unique ID
    private String name;
    private ArrayList<PackageClassComponent> packageClassComponents= new ArrayList<>();
    public double x;
    public double y;
    private double width;  // New property for width
    private double height; // New property for height

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public PackageComponent(String name) {
        super(name);
        this.id = UUID.randomUUID().toString(); // Generate a unique ID
        this.name = name;
        this.width = 200; // Default width
        this.height = 150; // Default height
    }


    public String getId() {
        return id; // Return the unique ID
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public void render() {
        // UI rendering logic, if needed
    }

    @Override
    public String toCode() {
        return " ";
    }

    @Override
    public DiagramType getType() {
        return DiagramType.PACKAGE;
    }

    public void addClassBox(PackageClassComponent newPackage) {
        this.packageClassComponents.add(newPackage);
    }
    public void addAllClassBox(PackageClassComponent newPackage) {
        this.packageClassComponents.add(newPackage);
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setWidth(double newWidth) {
        this.width=newWidth;
    }
}
