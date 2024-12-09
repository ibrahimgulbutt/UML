package org.example.scdpro2.business.models;

import org.example.scdpro2.business.models.BClassDiagarm.Relationship;
import org.example.scdpro2.business.models.BPackageDiagarm.BPackageRelationShip;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * Represents a project that contains multiple diagrams and relationships,
 * such as class diagrams and package diagrams, in the system.
 * The {@code Project} class serves as the primary model for storing and
 * managing diagrams and their relationships.
 */
public class Project implements Serializable {
    /**
     * The name of the project.
     */
    private String name;
    /**
     * A list of diagrams associated with the project.
     */
    private List<Diagram> diagrams;
    /**
     * A list of relationships between classes for class diagrams.
     */
    private List<Relationship> BClasssRelationships;// for class diagram
    /**
     * A list of relationships between packages for package diagrams.
     */
    public ArrayList<BPackageRelationShip> bPackageRelationShips;// for package diagram
    /**
     * Constructs a new {@code Project} with the specified name.
     *
     * @param name the name of the project.
     */
    public Project(String name) {
        this.name = name;
        this.diagrams = new ArrayList<>();
        this.BClasssRelationships = new ArrayList<>();
        this.bPackageRelationShips= new ArrayList<>();
    }
    /**
     * Sets the list of relationships for class diagrams.
     *
     * @param BClasssRelationships the list of {@link Relationship} objects to set.
     */
    public void setBClasssRelationships(List<Relationship> BClasssRelationships) {
        this.BClasssRelationships = BClasssRelationships;

    }
    /**
     * Sets the list of relationships for package diagrams and logs the size of the relationships list.
     *
     * @param BPackageRelationships the list of {@link BPackageRelationShip} objects to set.
     */
    public void setBPackageRelationships(ArrayList<BPackageRelationShip> BPackageRelationships) {
        this.bPackageRelationShips = BPackageRelationships;
        System.out.println("Project mein relationship ka size save karte hoe : " + bPackageRelationShips.size());
    }

    /**
     * Adds a new diagram to the project.
     *
     * @param diagram the {@link Diagram} to add.
     */
    public void addDiagram(Diagram diagram) {
        diagrams.add(diagram);
    }

    /**
     * Removes a diagram from the project.
     *
     * @param diagram the {@link Diagram} to remove.
     */
    public void removeDiagram(Diagram diagram) {
        diagrams.remove(diagram);
    }
    /**
     * Retrieves the list of diagrams in the project.
     *
     * @return a list of {@link Diagram} objects.
     */
    public List<Diagram> getDiagrams() {
        return diagrams;
    }
    /**
     * Retrieves the list of relationships for class diagrams.
     *
     * @return a list of {@link Relationship} objects.
     */
    public List<Relationship> getBClasssRelationships() {
        return BClasssRelationships;
    }
    /**
     * Retrieves the name of the project.
     *
     * @return the name of the project.
     */
    public String getName() {
        return name;
    }
    /**
     * Sets the name of the project.
     *
     * @param name the new name of the project.
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * Retrieves the list of relationships for package diagrams and logs the size of the relationships list.
     *
     * @return a list of {@link BPackageRelationShip} objects.
     */
    public List<BPackageRelationShip> getBPackageRelationships() {
        System.out.println("Project mein relationship ka size : " + bPackageRelationShips.size());
        return bPackageRelationShips;
    }
}
