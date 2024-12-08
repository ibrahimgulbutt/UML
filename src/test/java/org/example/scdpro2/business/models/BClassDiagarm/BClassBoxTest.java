package org.example.scdpro2.business.models.BClassDiagarm;

import org.example.scdpro2.business.models.DiagramType;
import org.example.scdpro2.ui.views.ClassDiagram.RelationshipLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

class BClassBoxTest {

    private BClassBox bClassBox;
    private AttributeComponent mockAttribute;
    private OperationComponent mockOperation;
    private Relationship mockRelationship;

    @BeforeEach
    void setUp() {
        bClassBox = new BClassBox("Test Class");

        // Mocking an AttributeComponent with 3 arguments (name, type, access modifier)
        mockAttribute = new AttributeComponent("attributeName", "String", "private");

        // Mocking an OperationComponent with 3 arguments (method name, return type, access modifier)
        mockOperation = new OperationComponent("methodName", "void", "public");

        // Create two BClassBox instances to be used as source and target
        BClassBox sourceClass = new BClassBox("Source Class");
        BClassBox targetClass = new BClassBox("Target Class");

        // Mocking a Relationship with all required parameters
        RelationshipLine.RelationshipType relationshipType = RelationshipLine.RelationshipType.ASSOCIATION; // Assuming you have an enum RelationshipType with this constant
        mockRelationship = new Relationship(sourceClass, targetClass, relationshipType, "1", "1..*", "associationLabel");
    }

    @Test
    void testConstructor_default() {
        BClassBox bClassBox = new BClassBox();
        assertEquals("Untitled", bClassBox.getTitle());
        assertTrue(bClassBox.getAttributes().isEmpty());
        assertTrue(bClassBox.getOperations().isEmpty());
        assertTrue(bClassBox.getRelationships().isEmpty());
        assertEquals(0, bClassBox.getX());
        assertEquals(0, bClassBox.getY());
    }

    @Test
    void testConstructor_withTitle() {
        assertEquals("Test Class", bClassBox.getTitle());
    }

    @Test
    void testSetAndGetX() {
        bClassBox.setX(100);
        assertEquals(100, bClassBox.getX());
    }

    @Test
    void testSetAndGetY() {
        bClassBox.setY(200);
        assertEquals(200, bClassBox.getY());
    }

    @Test
    void testAddAttribute() {
        bClassBox.addAttribute(mockAttribute);
        assertEquals(1, bClassBox.getAttributes().size());
        assertTrue(bClassBox.getAttributes().contains(mockAttribute));
    }

    @Test
    void testRemoveAttribute() {
        bClassBox.addAttribute(mockAttribute);
        bClassBox.removeAttribute(mockAttribute);
        assertEquals(0, bClassBox.getAttributes().size());
    }

    @Test
    void testAddOperation() {
        bClassBox.addOperation(mockOperation);
        assertEquals(1, bClassBox.getOperations().size());
        assertTrue(bClassBox.getOperations().contains(mockOperation));
    }

    @Test
    void testRemoveOperation() {
        bClassBox.addOperation(mockOperation);
        bClassBox.removeOperation(mockOperation);
        assertEquals(0, bClassBox.getOperations().size());
    }

    @Test
    void testAddRelationship() {
        bClassBox.addRelationship(mockRelationship);
        assertEquals(1, bClassBox.getRelationships().size());
        assertTrue(bClassBox.getRelationships().contains(mockRelationship));
    }

    @Test
    void testRemoveRelationship() {
        bClassBox.addRelationship(mockRelationship);
        bClassBox.getRelationships().remove(mockRelationship);
        assertEquals(0, bClassBox.getRelationships().size());
    }

    @Test
    void testToCode() {
        // Expected output format after calling toCode()
        String expectedCode = "class TestClass {\n  attributeName: String\n  methodName(): void\n}";

        // Get the actual generated code from the BClassBox
        String actualCode = bClassBox.toCode();

        // Print generatedCode for debugging if test fails
        System.out.println("Generated Code: " + actualCode);

        // Assert that the generated code matches the expected code
        assertEquals(expectedCode, actualCode);
    }

    @Test
    void testGetType() {
        assertEquals(DiagramType.CLASS, bClassBox.getType());
    }
}
