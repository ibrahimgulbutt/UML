package org.example.scdpro2.business.models;

import org.example.scdpro2.business.models.BClassDiagarm.Relationship;
import org.example.scdpro2.business.models.BPackageDiagarm.BPackageRelationShip;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Project implements Serializable {
    private String name;
    private List<Diagram> diagrams;
    private List<Relationship> relationships;
    public List<BPackageRelationShip> bPackageRelationShips;

    public List<BPackageRelationShip> getbPackageRelationShips() {
        return bPackageRelationShips;
    }

    public void setbPackageRelationShips(List<BPackageRelationShip> bPackageRelationShips) {
        this.bPackageRelationShips = bPackageRelationShips;
    }
    public void addBPackageRelationship(BPackageRelationShip relationship) {
        bPackageRelationShips.add(relationship);
    }

    public Project(String name) {
        this.name = name;
        this.diagrams = new ArrayList<>();
        this.relationships = new ArrayList<>();
        this.bPackageRelationShips= new ArrayList<>();
    }
    public void setRelationships(List<Relationship> relationships) {
        this.relationships = relationships;
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
