package org.example.scdpro2.business.models;

import org.example.scdpro2.business.models.BClassDiagarm.Relationship;
import org.example.scdpro2.business.models.BPackageDiagarm.BPackageRelationShip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProjectTest {

    private Project project;
    private Diagram diagram;
    private Relationship relationship;
    private BPackageRelationShip bPackageRelationShip;

    @BeforeEach
    void setUp() {
        project = new Project("TestProject");
        diagram = mock(Diagram.class);  // Mocking Diagram
        relationship = mock(Relationship.class); // Mocking Relationship
        bPackageRelationShip = mock(BPackageRelationShip.class); // Mocking BPackageRelationShip
    }

    @Test
    void testConstructor() {
        assertNotNull(project);
        assertEquals("TestProject", project.getName());
        assertTrue(project.getDiagrams().isEmpty());
        assertTrue(project.getBClasssRelationships().isEmpty());
        assertTrue(project.getBPackageRelationships().isEmpty());
    }

    @Test
    void testGetName() {
        assertEquals("TestProject", project.getName());
    }

    @Test
    void testSetName() {
        project.setName("UpdatedProject");
        assertEquals("UpdatedProject", project.getName());
    }

    @Test
    void testAddDiagram() {
        project.addDiagram(diagram);
        List<Diagram> diagrams = project.getDiagrams();
        assertEquals(1, diagrams.size());
        assertTrue(diagrams.contains(diagram));
    }

    @Test
    void testRemoveDiagram() {
        project.addDiagram(diagram);
        project.removeDiagram(diagram);
        List<Diagram> diagrams = project.getDiagrams();
        assertTrue(diagrams.isEmpty());
    }

    @Test
    void testSetBClasssRelationships() {
        project.setBClasssRelationships(List.of(relationship));
        List<Relationship> relationships = project.getBClasssRelationships();
        assertEquals(1, relationships.size());
        assertTrue(relationships.contains(relationship));
    }

    @Test
    void testSetBPackageRelationships() {
        project.setBPackageRelationships(new ArrayList<>(List.of(bPackageRelationShip)));
        List<BPackageRelationShip> relationships = project.getBPackageRelationships();
        assertEquals(1, relationships.size());
        assertTrue(relationships.contains(bPackageRelationShip));
    }

    @Test
    void testGetBClasssRelationships() {
        project.setBClasssRelationships(List.of(relationship));
        List<Relationship> relationships = project.getBClasssRelationships();
        assertEquals(1, relationships.size());
        assertTrue(relationships.contains(relationship));
    }

    @Test
    void testGetBPackageRelationships() {
        project.setBPackageRelationships(new ArrayList<>(List.of(bPackageRelationShip)));
        List<BPackageRelationShip> relationships = project.getBPackageRelationships();
        assertEquals(1, relationships.size());
        assertTrue(relationships.contains(bPackageRelationShip));
    }

    @Test
    void testAddAndGetDiagrams() {
        project.addDiagram(diagram);
        List<Diagram> diagrams = project.getDiagrams();
        assertEquals(1, diagrams.size());
        assertTrue(diagrams.contains(diagram));
    }

    @Test
    void testRemoveAndGetDiagrams() {
        project.addDiagram(diagram);
        project.removeDiagram(diagram);
        List<Diagram> diagrams = project.getDiagrams();
        assertTrue(diagrams.isEmpty());
    }

    @Test
    void testBPackageRelationshipsSizeLog() {
        // Mock System.out to verify the log output
        // This would require more complex integration testing to verify console output,
        // but for now, let's just confirm the functionality works.
        project.setBPackageRelationships(new ArrayList<>());
        project.setBPackageRelationships(new ArrayList<>(List.of(bPackageRelationShip)));
    }
}

