package org.example.scdpro2.business.models.BClassDiagarm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OperationComponentTest {

    private OperationComponent operation;

    @BeforeEach
    void setUp() {
        // Initialize the OperationComponent with sample data
        operation = new OperationComponent("methodName", "public", "void");
    }

    @Test
    void testGenerateCode() {
        // Expected generated code for the operation
        String expectedCode = "public void methodName() {}";

        // Actual generated code from the generateCode() method
        String actualCode = operation.generateCode();

        // Assert that the generated code matches the expected code
        assertEquals(expectedCode, actualCode);
    }

    @Test
    void testToString() {
        // Expected string representation of the operation
        String expectedString = "visibility='public', name='methodName'void";

        // Actual string representation from toString() method
        String actualString = operation.toString();

        // Assert that the toString() output matches the expected string
        assertEquals(expectedString, actualString);
    }

    @Test
    void testSettersAndGetters() {
        // Test setters and getters for visibility, name, and return type

        // Set new values
        operation.setVisibility("private");
        operation.setName("newMethodName");
        operation.setReturnType("int");

        // Assert the new values are set correctly
        assertEquals("private", operation.getVisibility());
        assertEquals("newMethodName", operation.getName());
        assertEquals("int", operation.getReturnType());
    }

    @Test
    void testSetName() {
        // Test that the setName method correctly updates the name
        operation.setName("updatedName");
        assertEquals("updatedName", operation.getName());
    }

    @Test
    void testSetVisibility() {
        // Test that the setVisibility method correctly updates the visibility
        operation.setVisibility("protected");
        assertEquals("protected", operation.getVisibility());
    }

    @Test
    void testSetReturnType() {
        // Test that the setReturnType method correctly updates the return type
        operation.setReturnType("boolean");
        assertEquals("boolean", operation.getReturnType());
    }

}

