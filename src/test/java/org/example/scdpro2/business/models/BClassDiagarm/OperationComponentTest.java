package org.example.scdpro2.business.models.BClassDiagarm;

import org.example.scdpro2.business.models.BClassDiagarm.OperationComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OperationComponentTest {

    private OperationComponent operationComponent;

    @BeforeEach
    void setUp() {
        // Initialize the OperationComponent before each test
        operationComponent = new OperationComponent("doSomething", "public", "void");
    }

    @Test
    void testConstructor() {
        // Test that the constructor initializes the values correctly
        assertEquals("doSomething", operationComponent.getName(), "The name should be initialized correctly");
        assertEquals("public", operationComponent.getVisibility(), "The visibility should be initialized correctly");
        assertEquals("void", operationComponent.getReturnType(), "The return type should be initialized correctly");
    }

    @Test
    void testToString() {
        // Test that the toString method formats the string correctly
        String expected = "visibility='public', name='doSomething'void";
        assertEquals(expected, operationComponent.toString(), "The toString method should return the correct string format");
    }

    @Test
    void testGenerateCode() {
        // Test that the generateCode method formats the code correctly
        String expectedCode = "public void doSomething() {}";
        assertEquals(expectedCode, operationComponent.generateCode(), "The generateCode method should return the correct code format");
    }

    @Test
    void testSetVisibility() {
        // Test that the visibility can be updated correctly
        operationComponent.setVisibility("private");
        assertEquals("private", operationComponent.getVisibility(), "The visibility should be updated correctly");
    }

    @Test
    void testSetName() {
        // Test that the name can be updated correctly
        operationComponent.setName("newMethod");
        assertEquals("newMethod", operationComponent.getName(), "The name should be updated correctly");
    }

    @Test
    void testSetReturnType() {
        // Test that the return type can be updated correctly
        operationComponent.setReturnType("int");
        assertEquals("int", operationComponent.getReturnType(), "The return type should be updated correctly");
    }
}
