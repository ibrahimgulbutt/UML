package org.example.scdpro2.business.services;

import org.example.scdpro2.business.models.Project;
import org.example.scdpro2.data.dao.ProjectDAO;

import java.io.File;
import java.util.Optional;

public class ProjectService {
    private final ProjectDAO projectDAO;
    private Project currentProject;

    public ProjectService(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }

    public Project createProject(String name) {
        currentProject = new Project(name);
        return currentProject;
    }

    public void saveCurrentProject(File file) throws Exception {
        if (currentProject == null) {
            throw new IllegalStateException("No project to save.");
        }
        projectDAO.saveProject(currentProject, file);
    }

    public Project loadProject(File file) throws Exception {
        currentProject = projectDAO.loadProject(file);
        return currentProject;
    }

    public Optional<Project> loadProjectFromFile(File file) throws Exception {
        currentProject = projectDAO.loadProject(file);
        return Optional.ofNullable(currentProject);
    }
    public Project getCurrentProject() {
        return currentProject;
    }
}
