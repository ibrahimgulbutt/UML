package org.example.scdpro2.business.models;

import org.example.scdpro2.business.models.BClassDiagarm.Relationship;
import org.example.scdpro2.business.models.BPackageDiagarm.BPackageRelationShip;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Project implements Serializable {
    private String name;
    private List<Diagram> diagrams;
    private List<Relationship> BClasssRelationships;// for class diagram
    public List<BPackageRelationShip> bPackageRelationShips;// for package diagram

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
        this.BClasssRelationships = new ArrayList<>();
        this.bPackageRelationShips= new ArrayList<>();
    }
    public void setBClasssRelationships(List<Relationship> BClasssRelationships) {
        this.BClasssRelationships = BClasssRelationships;
    }

    public void setBPackageRelationships(List<BPackageRelationShip> BPackageRelationships) {
        this.bPackageRelationShips = bPackageRelationShips;
    }


    public void addDiagram(Diagram diagram) {
        diagrams.add(diagram);
    }

    public void removeDiagram(Diagram diagram) {
        diagrams.remove(diagram);
    }

    public List<Diagram> getDiagrams() {
        return diagrams;
    }

    public List<Relationship> getBClasssRelationships() {
        return BClasssRelationships;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BPackageRelationShip> getBPackageRelationships() {
        return bPackageRelationShips;
    }
}
