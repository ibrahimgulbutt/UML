package org.example.scdpro2.business.models.BClassDiagarm;

import org.example.scdpro2.ui.views.ClassDiagram.RelationshipLine.RelationshipType;

import java.io.Serializable;
/**
 * Represents a relationship between two {@code BClassBox} objects in a class diagram.
 * The {@code Relationship} class defines the connection between a source and a target class box, including details such as the relationship type, multiplicities, and a label for the relationship.
 * This class supports serialization for persistence.
 */
public class Relationship implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * The source {@code BClassBox} in the relationship.
     * This is the class that the relationship originates from.
     */
    public BClassBox source;
    /**
     * The target {@code BClassBox} in the relationship.
     * This is the class that the relationship points to.
     */
    public BClassBox target;
    /**
     * The type of the relationship (e.g., association, inheritance).
     * This field indicates the kind of connection between the source and target classes.
     */
    public final RelationshipType type;
    /**
     * The multiplicity of the source class in the relationship (e.g., "1", "0..*", "1..*").
     * This field defines how many instances of the source class can participate in the relationship.
     */
    public String sourceMultiplicity;
    /**
     * The multiplicity of the target class in the relationship (e.g., "1", "0..*", "1..*").
     * This field defines how many instances of the target class can participate in the relationship.
     */
    public String targetMultiplicity;
    /**
     * The label associated with the relationship.
     * This field is used to store additional information or a name for the relationship.
     */
    public String relationshipLabel;
    /**
     * Constructs a new {@code Relationship} object with the specified source, target, relationship type, multiplicities, and label.
     * It initializes the relationship between two {@code BClassBox} objects with the given details.
     *
     * @param source The source class box of the relationship.
     * @param target The target class box of the relationship.
     * @param type The type of the relationship (e.g., "association", "inheritance").
     * @param sourceMultiplicity The multiplicity of the source class.
     * @param targetMultiplicity The multiplicity of the target class.
     * @param relationshipLabel The label for the relationship.
     */
    public Relationship(BClassBox source, BClassBox target, RelationshipType type,String sourceMultiplicity,String targetMultiplicity,String relationshipLabel) {
        this.source = source;
        this.target = target;
        this.type = type;
        this.sourceMultiplicity=sourceMultiplicity;
        this.targetMultiplicity=targetMultiplicity;
    }
    /**
     * Returns the source {@code BClassBox} of the relationship.
     *
     * @return The source {@code BClassBox}.
     */
    public BClassBox getSource() {
        return source;
    }
    /**
     * Returns the target {@code BClassBox} of the relationship.
     *
     * @return The target {@code BClassBox}.
     */
    public BClassBox getTarget() {
        return target;
    }
    /**
     * Returns the type of the relationship (e.g., association, inheritance).
     *
     * @return The type of the relationship.
     */
    public RelationshipType getType() {
        return type;
    }
    public String getTypee() {
        return type.toString();

    }
    /**
     * Returns the type of the relationship as a string.
     *
     * @return The type of the relationship as a string.
     */
    public BClassBox getSourceDiagram() {
        return (BClassBox) source;
    }
    /**
     * Returns the source class box as a {@code BClassBox}.
     *
     * @return The source {@code BClassBox}.
     */
    public BClassBox getTargetDiagram() {
        return (BClassBox) target;
    }
    /**
     * Returns a string representation of the relationship.
     * The string includes the source class title, target class title, and the type of the relationship.
     *
     * @return A string representing the relationship.
     */
    @Override
    public String toString() {
        return "Relationship{" +
                "source=" + source.getTitle() +
                ", target=" + target.getTitle() +
                ", type=" + type +
                '}';
    }
}
