package org.example.scdpro2.business.services;

import org.example.scdpro2.business.models.BClassDiagarm.BClassBox;
import org.example.scdpro2.business.models.BClassDiagarm.Relationship;
import org.example.scdpro2.business.models.Diagram;
import org.example.scdpro2.business.models.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DiagramServiceTest {

    private DiagramService diagramService;
    private Project mockProject;
    private Diagram mockDiagram;
    private Relationship mockRelationship;

    @BeforeEach
    void setUp() {
        diagramService = new DiagramService();
        mockProject = mock(Project.class);
        mockDiagram = mock(Diagram.class);
        mockRelationship = mock(Relationship.class);
    }

    @Test
    void testSetAndGetCurrentProject() {
        diagramService.setCurrentProject(mockProject);
        assertEquals(mockProject, diagramService.getCurrentProject());
    }

    @Test
    void testAddDiagramWhenProjectIsNullThrowsException() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            diagramService.addDiagram(mockDiagram);
        });
        assertEquals("No project is loaded. Please create or load a project first.", exception.getMessage());
    }

    @Test
    void testAddDiagramToCurrentProject() {
        diagramService.setCurrentProject(mockProject);
        diagramService.addDiagram(mockDiagram);

        verify(mockProject).addDiagram(mockDiagram);
    }

    @Test
    void testRemoveDiagramFromCurrentProject() {
        diagramService.setCurrentProject(mockProject);
        diagramService.removeDiagram(mockDiagram);

        verify(mockProject).removeDiagram(mockDiagram);
    }

    @Test
    void testRemoveDiagramWhenNoCurrentProject() {
        diagramService.removeDiagram(mockDiagram);
        // No exceptions should be thrown, and no interactions with the project occur
    }

    @Test
    void testRemoveRelationshipBetweenDiagrams() {
        BClassBox sourceDiagram = mock(BClassBox.class);
        BClassBox targetDiagram = mock(BClassBox.class);

        // Use mutable lists to simulate relationships
        List<Relationship> sourceRelationships = new ArrayList<>();
        List<Relationship> targetRelationships = new ArrayList<>();

        // Add the mock relationship to the lists
        sourceRelationships.add(mockRelationship);
        targetRelationships.add(mockRelationship);

        // Mock the getRelationships method to return these lists
        when(sourceDiagram.getRelationships()).thenReturn(sourceRelationships);
        when(targetDiagram.getRelationships()).thenReturn(targetRelationships);

        // Mock the source and target diagrams in the relationship
        when(mockRelationship.getSourceDiagram()).thenReturn(sourceDiagram);
        when(mockRelationship.getTargetDiagram()).thenReturn(targetDiagram);

        // Call the method under test
        diagramService.removeRelationship(sourceDiagram, targetDiagram);

        // Verify that the relationship was removed from both source and target
        assertFalse(sourceRelationships.contains(mockRelationship));
        assertFalse(targetRelationships.contains(mockRelationship));
    }

}
