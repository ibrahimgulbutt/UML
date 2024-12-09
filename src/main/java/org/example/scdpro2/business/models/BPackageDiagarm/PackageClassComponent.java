package org.example.scdpro2.business.models.BPackageDiagarm;

import org.example.scdpro2.business.models.Diagram;
import org.example.scdpro2.business.models.DiagramType;

import java.util.ArrayList;
/**
 * Represents a class component within a package diagram.
 * Each {@code PackageClassComponent} is associated with a {@code PackageComponent} (its parent) and contains properties such as name, visibility, coordinates, and relationships to other package class components.
 * It provides validation for name and visibility and allows rendering and code generation.
 */
public class PackageClassComponent extends Diagram {
    /**
     * A reference to the parent {@code PackageComponent} of this class component.
     * This establishes the hierarchy between package components and their contained class components.
     */
    private PackageComponent parent; // Parent package component reference
    /**
     * The name of the class component.
     * This is a required field and should be validated to ensure it is not null or empty.
     */
    private String name; // Class name
    /**
     * The visibility of the class component, represented by the standard UML visibility notation.
     * Allowed values are "+" for public, "-" for private, and "#" for protected.
     */
    private String visibility; // Visibility (+, -, #)
    /**
     * The X-coordinate position of the class component in the diagram.
     * This is used for rendering the class component at the correct position.
     */
    public double xCoordinates; // X-coordinate position
    /**
     * The Y-coordinate position of the class component in the diagram.
     * This is used for rendering the class component at the correct position.
     */
    public double yCoordinates; // Y-coordinate position
    /**
     * The width of the class component, used for rendering and layout purposes.
     */
    public double width;
    /**
     * A list of {@code BPackageRelationShip} objects that represent the relationships this class component has with other components.
     */
    private ArrayList<BPackageRelationShip> bPackageRelationShips;


    // Constructor
    /**
     * Constructs a new {@code PackageClassComponent} with the specified parent, name, and visibility.
     * It validates the name and visibility, and initializes the class with default coordinates.
     *
     * @param parent The parent package component containing this class component.
     * @param name The name of the class component.
     * @param visibility The visibility of the class component, either "+", "-", or "#".
     */
    public PackageClassComponent(PackageComponent parent, String name, String visibility) {
        super(name);
        this.parent = parent;
        this.name = validateName(name);
        this.visibility = validateVisibility(visibility);
        this.xCoordinates = 0; // Default initial position
        this.yCoordinates = 0; // Default initial position
        this.bPackageRelationShips=new ArrayList<>();
    }

    // Name getter and setter with validation
    public String getName() {
        return name;
    }
    /**
     * Sets the name of the class component, validating that it is not null or empty.
     *
     * @param newValue The new name of the class component.
     */
    public void setName(String newValue) {
        this.name = validateName(newValue);
    }

    // Visibility getter and setter with validation
    /**
     * Returns the visibility of the class component.
     *
     * @return The visibility of the class component, either "+", "-", or "#".
     */
    public String getVisibility() {
        return visibility;
    }
    /**
     * Sets the visibility of the class component, validating that it is one of the allowed values: "+", "-", or "#".
     *
     * @param newValue The new visibility value for the class component.
     */
    public void setVisibility(String newValue) {
        this.visibility = validateVisibility(newValue);
    }

    // Coordinates getters and setters
    /**
     * Returns the X-coordinate position of the class component.
     *
     * @return The X-coordinate of the class component.
     */
    public double getXCoordinates() {
        return xCoordinates;
    }
    /**
     * Sets the X-coordinate position of the class component.
     *
     * @param xCoordinates The new X-coordinate to set.
     */
    public void setXCoordinates(int xCoordinates) {
        this.xCoordinates = xCoordinates;
    }
    /**
     * Returns the Y-coordinate position of the class component.
     *
     * @return The Y-coordinate of the class component.
     */
    public double getYCoordinates() {
        return yCoordinates;
    }
    /**
     * Sets the Y-coordinate position of the class component.
     *
     * @param yCoordinates The new Y-coordinate to set.
     */
    public void setYCoordinates(int yCoordinates) {
        this.yCoordinates = yCoordinates;
    }

    // Parent package getter and setter
    /**
     * Returns the parent {@code PackageComponent} of this class component.
     *
     * @return The parent package component.
     */
    public PackageComponent getParent() {
        return parent;
    }
    /**
     * Sets the parent {@code PackageComponent} for this class component.
     *
     * @param parent The new parent package component.
     */
    public void setParent(PackageComponent parent) {
        this.parent = parent;
    }

    // Validation methods for input
    /**
     * Validates the name of the class component to ensure it is not null or empty.
     *
     * @param name The name of the class component.
     * @return The validated name.
     * @throws IllegalArgumentException if the name is null or empty.
     */
    private String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Class name cannot be null or empty.");
        }
        return name.trim();
    }
    /**
     * Validates the visibility of the class component to ensure it is one of the allowed values: "+", "-", or "#".
     *
     * @param visibility The visibility of the class component.
     * @return The validated visibility.
     * @throws IllegalArgumentException if the visibility is not one of the allowed values.
     */
    private String validateVisibility(String visibility) {
        if (!"+".equals(visibility) && !"-".equals(visibility) && !"#".equals(visibility)) {
            throw new IllegalArgumentException("Invalid visibility. Allowed values are: +, -, #.");
        }
        return visibility;
    }

    // Debugging and logging support
    /**
     * Returns a string representation of the class component, useful for debugging and logging.
     *
     * @return A string describing the class component.
     */
    @Override
    public String toString() {
        return "PackageClassComponent{" +
                "name='" + name + '\'' +
                ", visibility='" + visibility + '\'' +
                ", xCoordinates=" + xCoordinates +
                ", yCoordinates=" + yCoordinates +
                '}';
    }
    /**
     * Renders the class component in the UI.
     * This method is abstract and should be implemented with UI-specific rendering logic.
     */
    @Override
    public void render() {

    }
    /**
     * Returns the code representation of the class component.
     * This method is a placeholder and should be implemented with actual code generation logic for the class component.
     *
     * @return A string representing the code for the class component.
     */
    @Override
    public String toCode() {
        return "";
    }
    /**
     * Returns the type of the diagram, which is {@code null} for this class.
     * This method can be implemented to return a specific type if needed.
     *
     * @return The type of the diagram.
     */
    @Override
    public DiagramType getType() {
        return null;
    }
}
