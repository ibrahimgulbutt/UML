package org.example.scdpro2.business.models.BClassDiagarm;

import org.example.scdpro2.business.models.BClassDiagarm.AttributeComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AttributeComponentTest {

    private AttributeComponent attributeComponent;

    @BeforeEach
    void setUp() {
        // Initialize the AttributeComponent before each test
        attributeComponent = new AttributeComponent("name", "private", "String");
    }

    @Test
    void testConstructor() {
        // Test that the constructor initializes the values correctly
        assertEquals("name", attributeComponent.getName(), "The name should be initialized correctly");
        assertEquals("private", attributeComponent.getVisibility(), "The visibility should be initialized correctly");
        assertEquals("String", attributeComponent.getDataType(), "The datatype should be initialized correctly");
    }

    @Test
    void testGenerateCode() {
        // Test that the generateCode method formats the code correctly
        String expectedCode = "private name; String";
        assertEquals(expectedCode, attributeComponent.generateCode(), "The generateCode method should return the correct code format");
    }

    @Test
    void testSetVisibility() {
        // Test that the visibility can be updated correctly
        attributeComponent.setVisibility("public");
        assertEquals("public", attributeComponent.getVisibility(), "The visibility should be updated correctly");
    }

    @Test
    void testSetName() {
        // Test that the name can be updated correctly
        attributeComponent.setName("newName");
        assertEquals("newName", attributeComponent.getName(), "The name should be updated correctly");
    }

    @Test
    void testSetDataType() {
        // Test that the datatype can be updated correctly
        attributeComponent.setDataType("int");
        assertEquals("int", attributeComponent.getDataType(), "The datatype should be updated correctly");
    }

    @Test
    void testValidate() {
        // Test that the validate method doesn't throw any exceptions (assuming it's just a placeholder for now)
        // In a real-world scenario, we would test attribute-specific validation rules here.
        assertDoesNotThrow(() -> attributeComponent.validate(), "The validate method should not throw an exception");
    }
}
