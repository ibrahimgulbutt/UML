package org.example.scdpro2.business.models;

import org.example.scdpro2.ui.views.RelationshipLine.RelationshipType;

public class Relationship {
    private final Diagram source;
    private final Diagram target;
    private final RelationshipType type;

    public Relationship(Diagram source, Diagram target, RelationshipType type) {
        this.source = source;
        this.target = target;
        this.type = type;
    }

    public Diagram getSource() {
        return source;
    }

    public Diagram getTarget() {
        return target;
    }

    public RelationshipType getType() {
        return type;
    }

    public Object getSourceDiagram() {
        return source;
    }

    public Object getTargetDiagram() {
        return target;
    }
}
