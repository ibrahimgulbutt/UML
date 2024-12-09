package org.example.scdpro2.business.models.BPackageDiagram;

import org.example.scdpro2.business.models.BPackageDiagarm.PackageClassComponent;
import org.example.scdpro2.business.models.BPackageDiagarm.PackageComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PackageClassComponentTest {

    private PackageClassComponent packageClassComponent;

    // Dummy PackageComponent to use in tests, as PackageComponent is required by the constructor
    private PackageComponent dummyParent;

    @BeforeEach
    void setUp() {
        // Initialize the dummy parent and PackageClassComponent with sample data
        dummyParent = new PackageComponent("");
        packageClassComponent = new PackageClassComponent(dummyParent, "TestClass", "+");
    }

    @Test
    void testConstructor() {
        // Verify that the constructor properly initializes the object
        assertNotNull(packageClassComponent, "PackageClassComponent should be initialized.");
        assertEquals("TestClass", packageClassComponent.getName(), "The class name should be set correctly.");
        assertEquals("+", packageClassComponent.getVisibility(), "The visibility should be set correctly.");
        assertEquals(0, packageClassComponent.getXCoordinates(), "The X coordinate should be initialized to 0.");
        assertEquals(0, packageClassComponent.getYCoordinates(), "The Y coordinate should be initialized to 0.");
        assertEquals(dummyParent, packageClassComponent.getParent(), "The parent package component should be set correctly.");
    }

    @Test
    void testSetName() {
        // Test setter for class name
        packageClassComponent.setName("NewClassName");
        assertEquals("NewClassName", packageClassComponent.getName(), "The class name should be updated correctly.");
    }

    @Test
    void testSetNameWithInvalidValue() {
        // Test setter for class name with invalid value (empty string)
        assertThrows(IllegalArgumentException.class, () -> packageClassComponent.setName(""), "Empty class name should throw IllegalArgumentException.");
        assertThrows(IllegalArgumentException.class, () -> packageClassComponent.setName(null), "Null class name should throw IllegalArgumentException.");
    }

    @Test
    void testSetVisibility() {
        // Test setter for visibility
        packageClassComponent.setVisibility("-");
        assertEquals("-", packageClassComponent.getVisibility(), "The visibility should be updated correctly.");
    }

    @Test
    void testSetVisibilityWithInvalidValue() {
        // Test setter for visibility with invalid value
        assertThrows(IllegalArgumentException.class, () -> packageClassComponent.setVisibility("Invalid"), "Invalid visibility should throw IllegalArgumentException.");
    }

    @Test
    void testSetCoordinates() {
        // Test setter for X and Y coordinates
        packageClassComponent.setXCoordinates(100);
        packageClassComponent.setYCoordinates(200);

        assertEquals(100, packageClassComponent.getXCoordinates(), "The X coordinate should be updated correctly.");
        assertEquals(200, packageClassComponent.getYCoordinates(), "The Y coordinate should be updated correctly.");
    }

    @Test
    void testSetParent() {
        // Test setter for parent
        PackageComponent newParent = new PackageComponent("");
        packageClassComponent.setParent(newParent);
        assertEquals(newParent, packageClassComponent.getParent(), "The parent should be updated correctly.");
    }

    @Test
    void testValidateNameWithNull() {
        // Test validateName method with null value
        assertThrows(IllegalArgumentException.class, () -> packageClassComponent.setName(null), "Null name should throw IllegalArgumentException.");
    }

    @Test
    void testValidateNameWithEmptyString() {
        // Test validateName method with empty string
        assertThrows(IllegalArgumentException.class, () -> packageClassComponent.setName(""), "Empty name should throw IllegalArgumentException.");
    }

    @Test
    void testValidateNameWithValidValue() {
        // Test validateName with valid name
        String validName = "ValidClass";
        packageClassComponent.setName(validName);
        assertEquals(validName, packageClassComponent.getName(), "Valid name should be accepted.");
    }

    @Test
    void testValidateVisibilityWithInvalidVisibility() {
        // Test validateVisibility method with invalid visibility value
        assertThrows(IllegalArgumentException.class, () -> packageClassComponent.setVisibility("InvalidVisibility"), "Invalid visibility should throw IllegalArgumentException.");
    }

    @Test
    void testValidateVisibilityWithValidVisibility() {
        // Test validateVisibility with valid visibility values
        packageClassComponent.setVisibility("+");
        assertEquals("+", packageClassComponent.getVisibility(), "Valid visibility (+) should be accepted.");

        packageClassComponent.setVisibility("-");
        assertEquals("-", packageClassComponent.getVisibility(), "Valid visibility (-) should be accepted.");

        packageClassComponent.setVisibility("#");
        assertEquals("#", packageClassComponent.getVisibility(), "Valid visibility (#) should be accepted.");
    }

    @Test
    void testToString() {
        // Test toString method for debugging output
        String expectedString = "PackageClassComponent{name='TestClass', visibility='+', xCoordinates=0.0, yCoordinates=0.0}";
        assertEquals(expectedString, packageClassComponent.toString(), "toString should return the correct string representation.");
    }
}
