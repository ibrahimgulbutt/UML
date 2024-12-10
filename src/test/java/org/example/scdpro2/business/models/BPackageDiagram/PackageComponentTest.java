package org.example.scdpro2.business.models.BPackageDiagram;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.example.scdpro2.business.models.BPackageDiagarm.PackageClassComponent;
import org.example.scdpro2.business.models.BPackageDiagarm.PackageComponent;
import org.example.scdpro2.business.models.DiagramType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class PackageComponentTest {

    private PackageComponent packageComponent;
    private PackageClassComponent packageClassComponent;

    @BeforeEach
    void setUp() {
        packageComponent = new PackageComponent("TestPackage");
        packageClassComponent = mock(PackageClassComponent.class); // Mock the PackageClassComponent
    }

    @Test
    void testConstructor() {
        assertNotNull(packageComponent);
        assertEquals("TestPackage", packageComponent.getName());
        assertNotNull(packageComponent.getId()); // ID should be generated and not null
    }

    @Test
    void testGetId() {
        assertNotNull(packageComponent.getId());
        assertTrue(packageComponent.getId().length() > 0);
    }

    @Test
    void testGetName() {
        assertEquals("TestPackage", packageComponent.getName());
    }

    @Test
    void testSetName() {
        packageComponent.setName("NewTestPackage");
        assertEquals("NewTestPackage", packageComponent.getName());
    }

    @Test
    void testAddClassBox() {
        packageComponent.addClassBox(packageClassComponent);
        List<PackageClassComponent> components = packageComponent.getPackageClassComponents();
        assertEquals(1, components.size());
        assertTrue(components.contains(packageClassComponent));
    }

    @Test
    void testAddAllClassBox() {
        packageComponent.addAllClassBox(packageClassComponent);
        List<PackageClassComponent> components = packageComponent.getPackageClassComponents();
        assertEquals(1, components.size());
        assertTrue(components.contains(packageClassComponent));
    }

    @Test
    void testGetWidth() {
        assertEquals(200, packageComponent.getWidth());
    }

    @Test
    void testGetHeight() {
        assertEquals(150, packageComponent.getHeight());
    }

    @Test
    void testSetWidth() {
        packageComponent.setWidth(250);
        assertEquals(250, packageComponent.getWidth());
    }

    @Test
    void testSetHeight() {
        packageComponent.setHeight(300);
        assertEquals(300, packageComponent.getHeight());
    }

    @Test
    void testGetPackageClassComponents() {
        packageComponent.addClassBox(packageClassComponent);
        List<PackageClassComponent> components = packageComponent.getPackageClassComponents();
        assertNotNull(components);
        assertTrue(components.size() > 0);
    }

}
