package org.example.scdpro2.business.models.BPackageDiagarm;

import java.io.Serializable;
/**
 * Represents a relationship between two package components in a package diagram.
 * The {@code BPackageRelationShip} class defines the connection between a start and end package, including details such as the relationship type and coordinates for rendering.
 * This class supports serialization for persistence.
 */
public class BPackageRelationShip<T extends javafx.scene.layout.BorderPane> implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * The name of the start package in the relationship.
     * This is used to identify the origin of the relationship between two package components.
     */
    public String startPackagename;
    /**
     * The name of the end package in the relationship.
     * This is used to identify the destination of the relationship between two package components.
     */
    public String endPackagename;
    /**
     * The type of relationship between the two packages, such as "dependency", "association", etc.
     * This field describes how the start package is related to the end package.
     */
    public String relationshipType;
    /**
     * The X and Y coordinate of the starting and ending points of the relationship in the diagram.
     * This is used for rendering the connection line between the two package components.
     */
    public double startX, startY, endX, endY;

    /**
     * Constructs a new {@code BPackageRelationShip} object with the specified start and end package names.
     * It initializes the relationship with the provided package names and sets the coordinates to default values.
     *
     * @param startPackagename The name of the starting package in the relationship.
     * @param endPackagename The name of the ending package in the relationship.
     */
    public BPackageRelationShip(String startPackagename, String endPackagename) {
        this.startPackagename = startPackagename;
        this.endPackagename = endPackagename;
    }
    /**
     * Returns the name of the start package in the relationship.
     *
     * @return The name of the start package.
     */
    public String getStartPackageid() {
        return startPackagename;
    }
    /**
     * Sets the name of the start package in the relationship.
     *
     * @param startPackagename The new name of the start package.
     */
    public void setStartPackagename(String startPackagename) {
        this.startPackagename = startPackagename;
    }
    /**
     * Returns the name of the end package in the relationship.
     *
     * @return The name of the end package.
     */
    public String getEndPackageid() {
        return endPackagename;
    }
    /**
     * Sets the name of the end package in the relationship.
     *
     * @param endPackagename The new name of the end package.
     */
    public void setEndPackagename(String endPackagename) {
        this.endPackagename = endPackagename;
    }
    /**
     * Returns the type of relationship between the start and end packages.
     *
     * @return The type of relationship, such as "dependency" or "association".
     */
    public String getRelationshipType() {
        return relationshipType;
    }
    /**
     * Sets the type of relationship between the start and end packages.
     *
     * @param relationshipType The new relationship type.
     */
    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }
    /**
     * Returns the X-coordinate of the starting point of the relationship.
     *
     * @return The X-coordinate of the starting point.
     */
    public double getStartX() {
        return startX;
    }
    /**
     * Sets the X-coordinate of the starting point of the relationship.
     *
     * @param startX The new X-coordinate for the starting point.
     */
    public void setStartX(double startX) {
        this.startX = startX;
    }
    /**
     * Returns the Y-coordinate of the starting point of the relationship.
     *
     * @return The Y-coordinate of the starting point.
     */
    public double getStartY() {
        return startY;
    }
    /**
     * Sets the Y-coordinate of the starting point of the relationship.
     *
     * @param startY The new Y-coordinate for the starting point.
     */
    public void setStartY(double startY) {
        this.startY = startY;
    }
    /**
     * Returns the X-coordinate of the ending point of the relationship.
     *
     * @return The X-coordinate of the ending point.
     */
    public double getEndX() {
        return endX;
    }
    /**
     * Sets the X-coordinate of the ending point of the relationship.
     *
     * @param endX The new X-coordinate for the ending point.
     */
    public void setEndX(double endX) {
        this.endX = endX;
    }
    /**
     * Returns the Y-coordinate of the ending point of the relationship.
     *
     * @return The Y-coordinate of the ending point.
     */
    public double getEndY() {
        return endY;
    }
    /**
     * Sets the Y-coordinate of the ending point of the relationship.
     *
     * @param endY The new Y-coordinate for the ending point.
     */
    public void setEndY(double endY) {
        this.endY = endY;
    }
}
