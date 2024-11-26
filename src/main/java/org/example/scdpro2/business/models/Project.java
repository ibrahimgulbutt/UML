package org.example.scdpro2.business.models;

import org.example.scdpro2.business.models.Diagram;
import org.example.scdpro2.business.models.Relationship;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Project implements Serializable {
    private String name;
    private List<Diagram> diagrams;
    private List<Relationship> relationships;

    public Project(String name) {
        this.name = name;
        this.diagrams = new ArrayList<>();
        this.relationships = new ArrayList<>();
    }

    public void addDiagram(Diagram diagram) {
        diagrams.add(diagram);
    }

    public void removeDiagram(Diagram diagram) {
        diagrams.remove(diagram);
    }

    public void addRelationship(Relationship relationship) {
        relationships.add(relationship);
    }

    public void removeRelationship(Relationship relationship) {
        relationships.remove(relationship);
    }

    public List<Diagram> getDiagrams() {
        return diagrams;
    }

    public List<Relationship> getRelationships() {
        return relationships;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
