package org.example.scdpro2.business.models;

import org.example.scdpro2.ui.views.RelationshipLine.RelationshipType;

import java.io.Serializable;

public class Relationship implements Serializable {
    private static final long serialVersionUID = 1L;
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

    public ClassDiagram getSourceDiagram() {
        return (ClassDiagram) source;
    }

    public ClassDiagram getTargetDiagram() {
        return (ClassDiagram) target;
    }
    @Override
    public String toString() {
        return "Relationship{" +
                "source=" + source.getTitle() +
                ", target=" + target.getTitle() +
                ", type=" + type +
                '}';
    }
}
