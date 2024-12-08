package org.example.scdpro2.business.services;

import org.example.scdpro2.business.models.Project;
import org.example.scdpro2.data.dao.ProjectDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectServiceTest {
    private ProjectDAO mockProjectDAO;
    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        mockProjectDAO = mock(ProjectDAO.class);
        projectService = new ProjectService(mockProjectDAO);
    }

    @Test
    void testCreateProject() {
        String projectName = "New Project";
        Project project = projectService.createProject(projectName);

        assertNotNull(project);
        assertEquals(projectName, project.getName());
        assertEquals(project, projectService.getCurrentProject());
    }

    @Test
    void testSaveCurrentProjectSuccess() throws Exception {
        File file = mock(File.class);
        Project mockProject = mock(Project.class);

        projectService.currentProject = mockProject;

        projectService.saveCurrentProject(file);

        verify(mockProjectDAO).saveProject(mockProject, file);
    }

    @Test
    void testSaveCurrentProjectThrowsWhenNoProject() {
        File file = mock(File.class);

        projectService.currentProject = null;

        Exception exception = assertThrows(IllegalStateException.class, () -> projectService.saveCurrentProject(file));
        assertEquals("No project to save.", exception.getMessage());
    }

    @Test
    void testLoadProjectSuccess() throws Exception {
        File file = mock(File.class);
        Project mockProject = mock(Project.class);

        when(mockProjectDAO.loadProject(file)).thenReturn(mockProject);

        Project loadedProject = projectService.loadProject(file);

        assertNotNull(loadedProject);
        assertEquals(mockProject, loadedProject);
        assertEquals(mockProject, projectService.getCurrentProject());
        verify(mockProjectDAO).loadProject(file);
    }

    @Test
    void testLoadProjectFromFileReturnsOptionalOfProject() throws Exception {
        File file = mock(File.class);
        Project mockProject = mock(Project.class);

        when(mockProjectDAO.loadProject(file)).thenReturn(mockProject);

        Optional<Project> loadedProject = projectService.loadProjectFromFile(file);

        assertTrue(loadedProject.isPresent());
        assertEquals(mockProject, loadedProject.get());
        assertEquals(mockProject, projectService.getCurrentProject());
        verify(mockProjectDAO).loadProject(file);
    }

    @Test
    void testLoadProjectFromFileReturnsEmptyOptional() throws Exception {
        File file = mock(File.class);

        when(mockProjectDAO.loadProject(file)).thenReturn(null);

        Optional<Project> loadedProject = projectService.loadProjectFromFile(file);

        assertFalse(loadedProject.isPresent());
        assertNull(projectService.getCurrentProject());
        verify(mockProjectDAO).loadProject(file);
    }

    @Test
    void testGetCurrentProject() {
        Project mockProject = mock(Project.class);
        projectService.currentProject = mockProject;

        assertEquals(mockProject, projectService.getCurrentProject());
    }

    @Test
    void testGetCurrentProjectWhenNoProject() {
        projectService.currentProject = null;

        assertNull(projectService.getCurrentProject());
    }
}
