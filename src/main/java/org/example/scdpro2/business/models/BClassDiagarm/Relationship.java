package org.example.scdpro2.business.models.BClassDiagarm;

import org.example.scdpro2.ui.views.ClassDiagram.RelationshipLine.RelationshipType;

import java.io.Serializable;

public class Relationship implements Serializable {
    private static final long serialVersionUID = 1L;
    public BClassBox source;
    public BClassBox target;
    public final RelationshipType type;
    public String sourceMultiplicity;
    public String targetMultiplicity;
    public String relationshipLabel;

    public Relationship(BClassBox source, BClassBox target, RelationshipType type,String sourceMultiplicity,String targetMultiplicity,String relationshipLabel) {
        this.source = source;
        this.target = target;
        this.type = type;
        this.sourceMultiplicity=sourceMultiplicity;
        this.targetMultiplicity=targetMultiplicity;
    }

    public BClassBox getSource() {
        return source;
    }

    public BClassBox getTarget() {
        return target;
    }

    public RelationshipType getType() {
        return type;
    }
    public String getTypee() {
        return type.toString();

    }

    public BClassBox getSourceDiagram() {
        return (BClassBox) source;
    }

    public BClassBox getTargetDiagram() {
        return (BClassBox) target;
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
