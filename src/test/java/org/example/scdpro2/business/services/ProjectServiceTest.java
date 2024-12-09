package org.example.scdpro2.business.services;

import org.example.scdpro2.business.models.Project;
import org.example.scdpro2.data.dao.ProjectDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProjectServiceTest {

    @Mock
    private ProjectDAO projectDAO;  // Mock the ProjectDAO dependency

    @InjectMocks
    private ProjectService projectService;  // Inject the mock into ProjectService

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize the mock objects
    }

    // Test case for creating a new project
    @Test
    void testCreateProject() {
        String projectName = "Test Project";

        // Call createProject() method
        Project project = projectService.createProject(projectName);

        // Assert that the returned project is not null
        assertNotNull(project);

        // Assert that the project's name is correctly set
        assertEquals(projectName, project.getName());

        // Assert that the currentProject is also set correctly
        assertEquals(project, projectService.getCurrentProject());
    }

    // Test case for getting the current project
    @Test
    void testGetCurrentProject() {
        String projectName = "Test Project";

        // Create a project and set it as the current project
        projectService.createProject(projectName);

        // Retrieve the current project using getCurrentProject()
        Project currentProject = projectService.getCurrentProject();

        // Assert that the current project is the same as the one created
        assertNotNull(currentProject);
        assertEquals(projectName, currentProject.getName());
    }
}

