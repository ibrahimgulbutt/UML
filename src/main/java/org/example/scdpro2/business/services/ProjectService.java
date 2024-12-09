package org.example.scdpro2.business.services;

import org.example.scdpro2.business.models.Project;
import org.example.scdpro2.data.dao.ProjectDAO;

import java.io.File;
import java.util.Optional;
/**
 * The ProjectService class manages the creation and retrieval of {@link Project} objects.
 * It acts as an intermediary between the business logic and data access layers, ensuring
 * proper handling of project operations.
 */
public class ProjectService {
    /**
     * The {@link ProjectDAO} instance used for project persistence operations.
     */
    private final ProjectDAO projectDAO;
    /**
     * The currently active {@link Project} in the application.
     */
    public Project currentProject;

    /**
     * Constructs a new ProjectService instance with the specified {@link ProjectDAO}.
     *
     * @param projectDAO the {@link ProjectDAO} implementation used for project persistence.
     */
    public ProjectService(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }
    /**
     * Creates a new {@link Project} with the specified name and sets it as the current project.
     *
     * @param name the name of the new project.
     * @return the newly created {@link Project}.
     */
    public Project createProject(String name) {
        currentProject = new Project(name);
        return currentProject;
    }

    /**
     * Retrieves the currently active {@link Project}.
     *
     * @return the current {@link Project}, or {@code null} if no project is active.
     */
    public Project getCurrentProject() {
        return currentProject;
    }
}
