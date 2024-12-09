package org.example.scdpro2.business.models.BPackageDiagram;

import org.example.scdpro2.business.models.BPackageDiagarm.BPackageRelationShip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BPackageRelationShipTest {

    private BPackageRelationShip<?> packageRelationShip;

    @BeforeEach
    void setUp() {
        // Initialize a BPackageRelationShip with example package names
        packageRelationShip = new BPackageRelationShip<>("StartPackage", "EndPackage");
    }

    @Test
    void testConstructor() {
        // Test the constructor initializes the start and end package names correctly
        assertEquals("StartPackage", packageRelationShip.getStartPackageid(), "The start package should be initialized correctly.");
        assertEquals("EndPackage", packageRelationShip.getEndPackageid(), "The end package should be initialized correctly.");
    }

    @Test
    void testStartPackagenameSetter() {
        // Test setting and getting start package name
        packageRelationShip.setStartPackagename("NewStartPackage");
        assertEquals("NewStartPackage", packageRelationShip.getStartPackageid(), "The start package name should be updated.");
    }

    @Test
    void testEndPackagenameSetter() {
        // Test setting and getting end package name
        packageRelationShip.setEndPackagename("NewEndPackage");
        assertEquals("NewEndPackage", packageRelationShip.getEndPackageid(), "The end package name should be updated.");
    }

    @Test
    void testRelationshipTypeSetter() {
        // Test setting and getting relationship type
        packageRelationShip.setRelationshipType("Association");
        assertEquals("Association", packageRelationShip.getRelationshipType(), "The relationship type should be updated.");
    }

    @Test
    void testStartXSetter() {
        // Test setting and getting startX value
        packageRelationShip.setStartX(10.5);
        assertEquals(10.5, packageRelationShip.getStartX(), "The start X position should be updated.");
    }

    @Test
    void testStartYSetter() {
        // Test setting and getting startY value
        packageRelationShip.setStartY(20.5);
        assertEquals(20.5, packageRelationShip.getStartY(), "The start Y position should be updated.");
    }

    @Test
    void testEndXSetter() {
        // Test setting and getting endX value
        packageRelationShip.setEndX(30.5);
        assertEquals(30.5, packageRelationShip.getEndX(), "The end X position should be updated.");
    }

    @Test
    void testEndYSetter() {
        // Test setting and getting endY value
        packageRelationShip.setEndY(40.5);
        assertEquals(40.5, packageRelationShip.getEndY(), "The end Y position should be updated.");
    }

    @Test
    void testSetStartPackagenameNull() {
        // Test setting null value to start package name
        packageRelationShip.setStartPackagename(null);
        assertNull(packageRelationShip.getStartPackageid(), "The start package name should be set to null.");
    }

    @Test
    void testSetEndPackagenameNull() {
        // Test setting null value to end package name
        packageRelationShip.setEndPackagename(null);
        assertNull(packageRelationShip.getEndPackageid(), "The end package name should be set to null.");
    }

    @Test
    void testSetRelationshipTypeNull() {
        // Test setting null value to relationship type
        packageRelationShip.setRelationshipType(null);
        assertNull(packageRelationShip.getRelationshipType(), "The relationship type should be set to null.");
    }

    @Test
    void testSetCoordinates() {
        // Test setting coordinates for start and end points
        packageRelationShip.setStartX(50.0);
        packageRelationShip.setStartY(60.0);
        packageRelationShip.setEndX(70.0);
        packageRelationShip.setEndY(80.0);

        assertEquals(50.0, packageRelationShip.getStartX(), "Start X should be set correctly.");
        assertEquals(60.0, packageRelationShip.getStartY(), "Start Y should be set correctly.");
        assertEquals(70.0, packageRelationShip.getEndX(), "End X should be set correctly.");
        assertEquals(80.0, packageRelationShip.getEndY(), "End Y should be set correctly.");
    }
}
