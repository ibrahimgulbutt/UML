package org.example.scdpro2.business.models.BClassDiagarm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AttributeComponentTest {

    private AttributeComponent attribute;

    @BeforeEach
    void setUp() {
        // Initialize the AttributeComponent with sample data
        attribute = new AttributeComponent("attributeName", "private", "String");
    }

    @Test
    void testGenerateCode() {
        // Expected generated code for the attribute
        String expectedCode = "private attributeName; String";

        // Actual generated code from the generateCode() method
        String actualCode = attribute.generateCode();

        // Assert that the generated code matches the expected code
        assertEquals(expectedCode, actualCode);
    }

    @Test
    void testToString() {
        // Expected string representation of the attribute
        String expectedString = "private' name='attributeName':";

        // Actual string representation from toString() method
        String actualString = attribute.toString();

        // Assert that the toString() output matches the expected string
        assertEquals(expectedString, actualString);
    }

    @Test
    void testSettersAndGetters() {
        // Test setters and getters for visibility, name, and data type

        // Set new values
        attribute.setVisibility("protected");
        attribute.setName("newAttributeName");
        attribute.setDataType("int");

        // Assert the new values are set correctly
        assertEquals("protected", attribute.getVisibility());
        assertEquals("newAttributeName", attribute.getName());
        assertEquals("int", attribute.getDataType());
    }

    @Test
    void testSetName() {
        // Test that the setName method correctly updates the name
        attribute.setName("updatedName");
        assertEquals("updatedName", attribute.getName());
    }

    @Test
    void testSetVisibility() {
        // Test that the setVisibility method correctly updates the visibility
        attribute.setVisibility("public");
        assertEquals("public", attribute.getVisibility());
    }

    @Test
    void testSetDataType() {
        // Test that the setDataType method correctly updates the data type
        attribute.setDataType("boolean");
        assertEquals("boolean", attribute.getDataType());
    }
}

