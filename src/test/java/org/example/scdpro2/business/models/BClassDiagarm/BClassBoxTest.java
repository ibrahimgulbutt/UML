package org.example.scdpro2.business.models.BClassDiagarm;

import org.example.scdpro2.business.models.BClassDiagarm.AttributeComponent;
import org.example.scdpro2.business.models.BClassDiagarm.OperationComponent;
import org.example.scdpro2.business.models.BClassDiagarm.Relationship;
import org.example.scdpro2.business.models.BClassDiagarm.BClassBox;
import org.example.scdpro2.business.models.DiagramType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BClassBoxTest {

    private BClassBox bClassBox;
    private AttributeComponent attribute;
    private OperationComponent operation;
    private Relationship relationship;

    @BeforeEach
    void setUp() {
        // Initialize the BClassBox and mock dependencies before each test
        bClassBox = new BClassBox("Test Class Box");
        attribute = Mockito.mock(AttributeComponent.class);
        operation = Mockito.mock(OperationComponent.class);
        relationship = Mockito.mock(Relationship.class);
    }

    @Test
    void testAddAttribute() {
        // Test that an attribute can be added to the BClassBox
        bClassBox.addAttribute(attribute);
        List<AttributeComponent> attributes = bClassBox.getAttributes();
        assertEquals(1, attributes.size(), "Attribute should be added");
        assertTrue(attributes.contains(attribute), "The added attribute should be in the list");
    }

    @Test
    void testRemoveAttribute() {
        // Test that an attribute can be removed from the BClassBox
        bClassBox.addAttribute(attribute);
        bClassBox.removeAttribute(attribute);
        List<AttributeComponent> attributes = bClassBox.getAttributes();
        assertTrue(attributes.isEmpty(), "Attribute should be removed");
    }

    @Test
    void testAddOperation() {
        // Test that an operation can be added to the BClassBox
        bClassBox.addOperation(operation);
        List<OperationComponent> operations = bClassBox.getOperations();
        assertEquals(1, operations.size(), "Operation should be added");
        assertTrue(operations.contains(operation), "The added operation should be in the list");
    }

    @Test
    void testRemoveOperation() {
        // Test that an operation can be removed from the BClassBox
        bClassBox.addOperation(operation);
        bClassBox.removeOperation(operation);
        List<OperationComponent> operations = bClassBox.getOperations();
        assertTrue(operations.isEmpty(), "Operation should be removed");
    }

    @Test
    void testAddRelationship() {
        // Test that a relationship can be added to the BClassBox
        bClassBox.addRelationship(relationship);
        List<Relationship> relationships = bClassBox.getRelationships();
        assertEquals(1, relationships.size(), "Relationship should be added");
        assertTrue(relationships.contains(relationship), "The added relationship should be in the list");
    }

    @Test
    void testGetXAndYCoordinates() {
        // Test that the X and Y coordinates can be set and retrieved correctly
        bClassBox.setX(100.0);
        bClassBox.setY(200.0);

        assertEquals(100.0, bClassBox.getX(), "X-coordinate should be set to 100.0");
        assertEquals(200.0, bClassBox.getY(), "Y-coordinate should be set to 200.0");
    }

    @Test
    void testToCode() {
        // Test the toCode method to ensure it generates the expected code
        bClassBox.addAttribute(attribute);
        bClassBox.addOperation(operation);

        Mockito.when(attribute.generateCode()).thenReturn("private String name;");
        Mockito.when(operation.generateCode()).thenReturn("public void setName(String name);");

        String expectedCode = "class Test Class Box {\n  private String name;\n  public void setName(String name);\n}";
        String generatedCode = bClassBox.toCode();

        assertEquals(expectedCode, generatedCode, "The generated code should match the expected code");
    }

    @Test
    void testGetType() {
        // Test that the getType method returns the correct type
        assertEquals(DiagramType.CLASS, bClassBox.getType(), "The diagram type should be CLASS");
    }

    @Test
    void testInitialState() {
        // Test that the BClassBox is initialized with default values
        assertNotNull(bClassBox.getAttributes(), "Attributes list should be initialized");
        assertNotNull(bClassBox.getOperations(), "Operations list should be initialized");
        assertNotNull(bClassBox.getRelationships(), "Relationships list should be initialized");
        assertEquals(0.0, bClassBox.getX(), "Initial X-coordinate should be 0.0");
        assertEquals(0.0, bClassBox.getY(), "Initial Y-coordinate should be 0.0");
    }
}
