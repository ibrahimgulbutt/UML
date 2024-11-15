package org.example.scdpro2.business.models;

public class Relationship {
    private String startClassId;
    private String endClassId;
    private RelationshipType type;

    public enum RelationshipType {
        ASSOCIATION, AGGREGATION, COMPOSITION, INHERITANCE
    }

    public Relationship(String startClassId, String endClassId, RelationshipType type) {
        this.startClassId = startClassId;
        this.endClassId = endClassId;
        this.type = type;
    }

    public String getStartClassId() {
        return startClassId;
    }

    public String getEndClassId() {
        return endClassId;
    }

    public RelationshipType getType() {
        return type;
    }
}
