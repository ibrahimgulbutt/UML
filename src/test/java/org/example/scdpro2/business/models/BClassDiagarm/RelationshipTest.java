package org.example.scdpro2.business.models.BClassDiagarm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.example.scdpro2.business.models.BClassDiagarm.BClassBox;
import org.example.scdpro2.ui.views.ClassDiagram.RelationshipLine.RelationshipType;

class RelationshipTest {

    private Relationship relationship;
    private BClassBox source;
    private BClassBox target;
    private RelationshipType relationshipType;

    @BeforeEach
    void setUp() {
        // Initialize the source and target BClassBox objects
        source = new BClassBox("ClassA");
        target = new BClassBox("ClassB");

        // Initialize RelationshipType (you should replace this with a valid value of the RelationshipType enum)
        relationshipType = RelationshipType.ASSOCIATION;

        // Create a Relationship instance
        relationship = new Relationship(source, target, relationshipType, "1", "1", "associationLabel");
    }

    @Test
    void testConstructor() {
        // Assert that the source, target, type, and multiplicity are correctly initialized
        assertEquals("ClassA", relationship.getSource().getTitle());
        assertEquals("ClassB", relationship.getTarget().getTitle());
        assertEquals(relationshipType, relationship.getType());
        assertEquals("1", relationship.sourceMultiplicity);
        assertEquals("1", relationship.targetMultiplicity);
    }

    @Test
    void testGetSource() {
        // Test the getter for source
        assertEquals("ClassA", relationship.getSource().getTitle());
    }

    @Test
    void testGetTarget() {
        // Test the getter for target
        assertEquals("ClassB", relationship.getTarget().getTitle());
    }

    @Test
    void testGetType() {
        // Test the getter for type
        assertEquals(relationshipType, relationship.getType());
    }

    @Test
    void testGetTypee() {
        // Test the getter for type as String representation
        assertEquals(relationshipType.toString(), relationship.getTypee());
    }

    @Test
    void testToString() {
        // Expected output for toString() method
        String expectedString = "Relationship{source=ClassA, target=ClassB, type=ASSOCIATION}";

        // Assert that the toString() method works as expected
        assertEquals(expectedString, relationship.toString());
    }

    @Test
    void testGetSourceDiagram() {
        // Test that getSourceDiagram() returns the correct source
        assertEquals(source, relationship.getSourceDiagram());
    }

    @Test
    void testGetTargetDiagram() {
        // Test that getTargetDiagram() returns the correct target
        assertEquals(target, relationship.getTargetDiagram());
    }
}
