package org.example.scdpro2.business.models.BPackageDiagarm;

import org.example.scdpro2.business.models.Diagram;
import org.example.scdpro2.business.models.DiagramType;
import org.example.scdpro2.business.models.BClassDiagarm.Relationship;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
/**
 * Represents a component within a package diagram in the system.
 * A {@code PackageComponent} holds a collection of {@code PackageClassComponent} elements
 * and manages its own unique identifier, name, and dimensions (width and height).
 * It extends from the {@code Diagram} class and implements functionality specific to package diagrams.
 */
public class PackageComponent extends Diagram implements Serializable {
    private static final long serialVersionUID = 1L;

    public ArrayList<PackageClassComponent> getPackageClassComponents() {
        return packageClassComponents;
    }
    /**
     * A unique identifier for the package component, generated using UUID.
     * This ensures that each package component can be uniquely identified.
     */
    private final String id; // Unique ID
    /**
     * The name of the package component.
     * This is used to identify and label the component within the package diagram.
     */
    private String name;
    /**
     * A list of {@code PackageClassComponent} objects that belong to this package component.
     * These represent the classes contained within the package.
     */
    private ArrayList<PackageClassComponent> packageClassComponents= new ArrayList<>();
    /**
     * The X-coordinate of the package component.
     * Used for positioning the component in the UI or rendering context.
     */
    public double x;
    /**
     * The Y-coordinate of the package component.
     * Used for positioning the component in the UI or rendering context.
     */
    public double y;
    /**
     * The width of the package component.
     * Used to define the size of the component when rendered.
     */
    private double width;  // New property for width
    /**
     * The height of the package component.
     * Used to define the size of the component when rendered.
     */
    private double height; // New property for height

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
    /**
     * Constructs a new {@code PackageComponent} with the specified name.
     * A unique ID is generated for the component, and default values for width and height are set.
     *
     * @param name The name of the package component.
     */
    public PackageComponent(String name) {
        super(name);
        this.id = UUID.randomUUID().toString(); // Generate a unique ID
        this.name = name;
        this.width = 200; // Default width
        this.height = 150; // Default height
    }

    /**
     * Returns the unique ID of the package component.
     *
     * @return The unique identifier of the package component.
     */
    public String getId() {
        return id; // Return the unique ID
    }
    /**
     * Returns the name of the package component.
     *
     * @return The name of the package component.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the package component.
     *
     * @param name The new name of the package component.
     */
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
    /**
     * Adds a new {@code PackageClassComponent} to this package component.
     *
     * @param newPackage The {@code PackageClassComponent} to be added.
     */
    public void addClassBox(PackageClassComponent newPackage) {
        this.packageClassComponents.add(newPackage);
    }
    /**
     * Adds all {@code PackageClassComponent} objects to this package component.
     *
     * @param newPackage The {@code PackageClassComponent} to be added.
     */
    public void addAllClassBox(PackageClassComponent newPackage) {
        this.packageClassComponents.add(newPackage);
    }
    /**
     * Returns the width of the package component.
     *
     * @return The width of the package component.
     */
    public double getWidth() {
        return width;
    }
    /**
     * Returns the height of the package component.
     *
     * @return The height of the package component.
     */
    public double getHeight() {
        return height;
    }
    /**
     * Sets the height of the package component.
     *
     * @param height The new height to be set.
     */
    public void setHeight(double height) {
        this.height = height;
    }
    /**
     * Sets the width of the package component.
     *
     * @param newWidth The new width to be set.
     */
    public void setWidth(double newWidth) {
        this.width=newWidth;
    }
}
