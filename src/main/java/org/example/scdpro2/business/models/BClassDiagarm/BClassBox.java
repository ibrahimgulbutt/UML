package org.example.scdpro2.business.models.BClassDiagarm;

import org.example.scdpro2.business.models.Diagram;
import org.example.scdpro2.business.models.DiagramType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * Represents a class diagram box in the system.
 * The {@code BClassBox} class models a class in a UML-style class diagram. It extends {@code Diagram}
 * and includes attributes, operations, relationships, and UI positioning information.
 * It also provides methods for adding, removing, and retrieving components such as attributes and operations.
 */
public class BClassBox extends Diagram implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * A list of {@code AttributeComponent} objects that represent the attributes of the class.
     */
    private List<AttributeComponent> attributes;
    /**
     * A list of {@code OperationComponent} objects that represent the operations of the class.
     */
    private List<OperationComponent> operations;
    /**
     * A list of {@code Relationship} objects that represent the relationships between this class and other components.
     */
    private final List<Relationship> relationships;
    /**
     * The X-coordinate for the UI placement of the class diagram box.
     */
    private double x; // X-coordinate for UI placement
    /**
     * The Y-coordinate for the UI placement of the class diagram box.
     */
    private double y; // Y-coordinate for UI placement
    /**
     * The type of the class diagram box (e.g., "Class").
     */
    public String Type;
    /**
     * Constructs a new {@code BClassBox} with a default title "Untitled" and initializes empty lists
     * for attributes, operations, and relationships.
     */
    public BClassBox() {
        super("Untitled");
        this.attributes = new ArrayList<>();
        this.operations = new ArrayList<>();
        this.relationships = new ArrayList<>();
        this.x = 0;
        this.y = 0;
    }
    /**
     * Constructs a new {@code BClassBox} with the specified title and initializes empty lists
     * for attributes, operations, and relationships.
     *
     * @param title The title of the class diagram box.
     */
    public BClassBox(String title) {
        super(title);
        this.attributes = new ArrayList<>();
        this.operations = new ArrayList<>();
        this.relationships = new ArrayList<>();
        this.x = 0;
        this.y = 0;
    }
    /**
     * Sets the list of attributes for the class diagram box.
     *
     * @param attributes The list of {@code AttributeComponent} objects to set.
     */
    public void setAttributes(List<AttributeComponent> attributes) {
        this.attributes = attributes;
    }
    /**
     * Sets the list of operations for the class diagram box.
     *
     * @param operations The list of {@code OperationComponent} objects to set.
     */
    public void setOperations(List<OperationComponent> operations) {
        this.operations = operations;
    }
    /**
     * Returns the list of attributes for the class diagram box.
     *
     * @return The list of {@code AttributeComponent} objects.
     */
    public List<AttributeComponent> getAttributes() {
        return attributes;
    }
    /**
     * Returns the list of operations for the class diagram box.
     *
     * @return The list of {@code OperationComponent} objects.
     */
    public List<OperationComponent> getOperations() {
        return operations;
    }
    /**
     * Adds a {@code Relationship} to the list of relationships for the class diagram box.
     *
     * @param relationship The {@code Relationship} to add.
     */
    public void addRelationship(Relationship relationship) {
        relationships.add(relationship);
    }
    /**
     * Returns the list of relationships for the class diagram box.
     *
     * @return The list of {@code Relationship} objects.
     */
    public List<Relationship> getRelationships() {
        return relationships;
    }
    /**
     * Returns the X-coordinate for the UI placement of the class diagram box.
     *
     * @return The X-coordinate.
     */
    public double getX() {
        return x;
    }
    /**
     * Sets the X-coordinate for the UI placement of the class diagram box.
     *
     * @param x The X-coordinate to set.
     */
    public void setX(double x) {
        this.x = x;
    }
    /**
     * Returns the Y-coordinate for the UI placement of the class diagram box.
     *
     * @return The Y-coordinate.
     */
    public double getY() {
        return y;
    }
    /**
     * Sets the Y-coordinate for the UI placement of the class diagram box.
     *
     * @param y The Y-coordinate to set.
     */
    public void setY(double y) {
        this.y = y;
    }
    /**
     * Renders the class diagram box in the UI.
     * This method is a placeholder for the actual rendering logic that is specific to the UI framework.
     */
    @Override
    public void render() {
        // Rendering logic for UI
    }
    /**
     * Generates the code representation of the class diagram box.
     * The generated code includes the class name, attributes, and operations.
     *
     * @return The code representation of the class.
     */
    @Override
    public String toCode() {
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append("class ").append(title).append(" {\n");
        for (AttributeComponent attr : attributes) {
            codeBuilder.append("  ").append(attr.generateCode()).append("\n");
        }
        for (OperationComponent op : operations) {
            codeBuilder.append("  ").append(op.generateCode()).append("\n");
        }
        codeBuilder.append("}");
        return codeBuilder.toString();
    }
    /**
     * Returns the type of the diagram.
     * This method returns the class diagram type, indicating that this diagram represents a class.
     *
     * @return The diagram type, which is {@code DiagramType.CLASS}.
     */
    @Override
    public DiagramType getType() {
        return DiagramType.CLASS; // Return INTERFACE type
    }
    /**
     * Adds an {@code AttributeComponent} to the list of attributes.
     *
     * @param attribute The {@code AttributeComponent} to add.
     */
    public void addAttribute(AttributeComponent attribute) {
        if (attributes == null) {
            attributes = new ArrayList<>();
        }
        attributes.add(attribute);
        notifyChange(); // Notify listeners of the change
    }
    /**
     * Removes an {@code AttributeComponent} from the list of attributes.
     *
     * @param attribute The {@code AttributeComponent} to remove.
     */
    public void removeAttribute(AttributeComponent attribute) {
        if (attributes != null) {
            attributes.remove(attribute);
            notifyChange(); // Notify listeners of the change
        }
    }
    /**
     * Adds an {@code OperationComponent} to the list of operations.
     *
     * @param operation The {@code OperationComponent} to add.
     */
    public void addOperation(OperationComponent operation) {
        if (operations == null) {
            operations = new ArrayList<>();
        }
        operations.add(operation);
        notifyChange(); // Notify listeners of the change
    }
    /**
     * Removes an {@code OperationComponent} from the list of operations.
     *
     * @param operation The {@code OperationComponent} to remove.
     */
    public void removeOperation(OperationComponent operation) {
        if (operations != null) {
            operations.remove(operation);
            notifyChange(); // Notify listeners of the change
        }
    }
    /**
     * Notifies listeners of changes to the class diagram box.
     * This method can be used to implement the observer pattern and trigger UI updates.
     */
    // Example of notifying listeners (implement observer pattern)
    private void notifyChange() {
        // Trigger an event or listener update for UI synchronization
    }

}

