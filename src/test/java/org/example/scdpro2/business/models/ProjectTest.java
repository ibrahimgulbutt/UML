package org.example.scdpro2.business.models;

import org.example.scdpro2.business.models.BClassDiagarm.Relationship;
import org.example.scdpro2.business.models.Diagram;
import org.example.scdpro2.business.models.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ProjectTest {

    private Project project;
    private Diagram diagramMock;
    private Relationship relationshipMock;

    @BeforeEach
    void setUp() {
        project = new Project("Test Project");
        diagramMock = new Diagram("") {
            @Override
            public void render() {
            }

            @Override
            public String toCode() {
                return "";
            }

            @Override
            public DiagramType getType() {
                return null;
            }
        };// Using an anonymous class as Diagram is abstract

        relationshipMock = new Relationship(null, null, null, null, null, null);
    }

    @Test
    void testGetName() {
        assertEquals("Test Project", project.getName());
    }

    @Test
    void testSetName() {
        project.setName("New Project Name");
        assertEquals("New Project Name", project.getName());
    }

    @Test
    void testAddDiagram() {
        project.addDiagram(diagramMock);
        assertTrue(project.getDiagrams().contains(diagramMock));
    }

    @Test
    void testRemoveDiagram() {
        project.addDiagram(diagramMock);
        project.removeDiagram(diagramMock);
        assertFalse(project.getDiagrams().contains(diagramMock));
    }

    @Test
    void testSetDiagrams() {
        List<Diagram> diagrams = new ArrayList<>();
        diagrams.add(diagramMock);
        project.setDiagrams(diagrams);
        assertEquals(1, project.getDiagrams().size());
        assertTrue(project.getDiagrams().contains(diagramMock));
    }

    @Test
    void testGetDiagrams() {
        assertTrue(project.getDiagrams().isEmpty());
        project.addDiagram(diagramMock);
        assertEquals(1, project.getDiagrams().size());
        assertEquals(diagramMock, project.getDiagrams().get(0));
    }

    @Test
    void testAddRelationship() {
        project.addRelationship(relationshipMock);
        assertTrue(project.getRelationships().contains(relationshipMock));
    }

    @Test
    void testAddRelationshipList() {
        List<Relationship> relationships = new ArrayList<>();
        relationships.add(relationshipMock);
        project.addRelationship(relationships);
        assertEquals(1, project.getRelationships().size());
        assertTrue(project.getRelationships().contains(relationshipMock));
    }

    @Test
    void testRemoveRelationship() {
        project.addRelationship(relationshipMock);
        project.removeRelationship(relationshipMock);
        assertFalse(project.getRelationships().contains(relationshipMock));
    }

    @Test
    void testSetRelationships() {
        List<Relationship> relationships = new ArrayList<>();
        relationships.add(relationshipMock);
        project.setRelationships(relationships);
        assertEquals(1, project.getRelationships().size());
        assertTrue(project.getRelationships().contains(relationshipMock));
    }

    @Test
    void testGetRelationships() {
        assertTrue(project.getRelationships().isEmpty());
        project.addRelationship(relationshipMock);
        assertEquals(1, project.getRelationships().size());
        assertEquals(relationshipMock, project.getRelationships().get(0));
    }
}
