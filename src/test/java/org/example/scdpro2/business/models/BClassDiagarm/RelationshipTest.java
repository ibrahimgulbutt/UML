package org.example.scdpro2.business.models.BClassDiagarm;

import org.example.scdpro2.business.models.BClassDiagarm.BClassBox;
import org.example.scdpro2.business.models.BClassDiagarm.Relationship;
import org.example.scdpro2.ui.views.ClassDiagram.RelationshipLine.RelationshipType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class RelationshipTest {

    private Relationship relationship;
    private BClassBox sourceMock;
    private BClassBox targetMock;
    private RelationshipType typeMock;

    @BeforeEach
    void setUp() {
        // Create mocks for BClassBox and RelationshipType
        sourceMock = mock(BClassBox.class);
        targetMock = mock(BClassBox.class);
        typeMock = mock(RelationshipType.class);

        // Set up mocks for source and target
        when(sourceMock.getTitle()).thenReturn("SourceClass");
        when(targetMock.getTitle()).thenReturn("TargetClass");

        // Create Relationship object using the mocked objects
        relationship = new Relationship(sourceMock, targetMock, typeMock, "1", "1", "label");
    }

    @Test
    void testConstructor() {
        // Test that the constructor initializes the fields correctly
        assertEquals(sourceMock, relationship.getSource(), "The source should be initialized correctly");
        assertEquals(targetMock, relationship.getTarget(), "The target should be initialized correctly");
        assertEquals(typeMock, relationship.getType(), "The relationship type should be initialized correctly");
    }

    @Test
    void testGetSourceDiagram() {
        // Test that the getSourceDiagram method returns the correct type
        BClassBox result = relationship.getSourceDiagram();
        assertEquals(sourceMock, result, "getSourceDiagram should return the source mock");
    }

    @Test
    void testGetTargetDiagram() {
        // Test that the getTargetDiagram method returns the correct type
        BClassBox result = relationship.getTargetDiagram();
        assertEquals(targetMock, result, "getTargetDiagram should return the target mock");
    }

    @Test
    void testGetType() {
        // Test that the getType method returns the correct RelationshipType
        assertEquals(typeMock, relationship.getType(), "The getType method should return the correct type");
    }

    @Test
    void testGetTypee() {
        // Test that the getTypee method returns the string representation of RelationshipType
        when(typeMock.toString()).thenReturn("Aggregation");
        assertEquals("Aggregation", relationship.getTypee(), "The getTypee method should return the string representation of the type");
    }

    @Test
    void testToString() {
        // Test that the toString method returns the correct string format
        String expected = "Relationship{source=SourceClass, target=TargetClass, type=Aggregation}";
        when(typeMock.toString()).thenReturn("Aggregation");
        assertEquals(expected, relationship.toString(), "The toString method should format the string correctly");
    }

    @Test
    void testSourceMultiplicity() {
        // Test the sourceMultiplicity setter and getter
        relationship.sourceMultiplicity = "1..*";
        assertEquals("1..*", relationship.sourceMultiplicity, "The source multiplicity should be set correctly");
    }

    @Test
    void testTargetMultiplicity() {
        // Test the targetMultiplicity setter and getter
        relationship.targetMultiplicity = "0..1";
        assertEquals("0..1", relationship.targetMultiplicity, "The target multiplicity should be set correctly");
    }

    @Test
    void testRelationshipLabel() {
        // Test the relationshipLabel setter and getter
        relationship.relationshipLabel = "Label";
        assertEquals("Label", relationship.relationshipLabel, "The relationship label should be set correctly");
    }
}
