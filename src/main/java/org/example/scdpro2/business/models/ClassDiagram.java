package org.example.scdpro2.business.models;

import java.util.ArrayList;
import java.util.List;

public class ClassDiagram extends Diagram {
    private List<AttributeComponent> attributes;
    private List<OperationComponent> operations;
    private final List<Relationship> relationships;

    public ClassDiagram(String title) {
        super(title);
        this.attributes = new ArrayList<>();
        this.operations = new ArrayList<>();
        this.relationships = new ArrayList<>();
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

    public void addAttribute(AttributeComponent attribute) {
        attributes.add(attribute);
    }

    public void addOperation(OperationComponent operation) {
        operations.add(operation);
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
}
