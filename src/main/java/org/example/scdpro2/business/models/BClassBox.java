package org.example.scdpro2.business.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BClassBox extends Diagram implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<AttributeComponent> attributes;
    private List<OperationComponent> operations;
    private final List<Relationship> relationships;
    private double x; // X-coordinate for UI placement
    private double y; // Y-coordinate for UI placement
    public String Type;

    public BClassBox() {
        super("Untitled");
        this.attributes = new ArrayList<>();
        this.operations = new ArrayList<>();
        this.relationships = new ArrayList<>();
        this.x = 0;
        this.y = 0;
    }

    public BClassBox(String title) {
        super(title);
        this.attributes = new ArrayList<>();
        this.operations = new ArrayList<>();
        this.relationships = new ArrayList<>();
        this.x = 0;
        this.y = 0;
    }

    public void setAttributes(List<AttributeComponent> attributes) {
        this.attributes = attributes;
    }

    public void setOperations(List<OperationComponent> operations) {
        this.operations = operations;
    }

    public List<AttributeComponent> getAttributes() {
        return attributes;
    }

    public List<OperationComponent> getOperations() {
        return operations;
    }

    public void addRelationship(Relationship relationship) {
        relationships.add(relationship);
    }

    public List<Relationship> getRelationships() {
        return relationships;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public void render() {
        // Rendering logic for UI
    }

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

    @Override
    public DiagramType getType() {
        return DiagramType.CLASS; // Return INTERFACE type
    }

    public void addAttribute(AttributeComponent attribute) {
        if (attributes == null) {
            attributes = new ArrayList<>();
        }
        attributes.add(attribute);
        notifyChange(); // Notify listeners of the change
    }

    public void removeAttribute(AttributeComponent attribute) {
        if (attributes != null) {
            attributes.remove(attribute);
            notifyChange(); // Notify listeners of the change
        }
    }

    public void addOperation(OperationComponent operation) {
        if (operations == null) {
            operations = new ArrayList<>();
        }
        operations.add(operation);
        notifyChange(); // Notify listeners of the change
    }

    public void removeOperation(OperationComponent operation) {
        if (operations != null) {
            operations.remove(operation);
            notifyChange(); // Notify listeners of the change
        }
    }

    // Example of notifying listeners (implement observer pattern)
    private void notifyChange() {
        // Trigger an event or listener update for UI synchronization
    }

}

