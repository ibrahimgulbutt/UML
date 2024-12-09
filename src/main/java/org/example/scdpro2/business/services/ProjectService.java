package org.example.scdpro2.business.services;

import org.example.scdpro2.business.models.Project;
import org.example.scdpro2.data.dao.ProjectDAO;

import java.io.File;
import java.util.Optional;

public class ProjectService {
    private final ProjectDAO projectDAO;
    public Project currentProject;

    public ProjectService(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }

    public Project createProject(String name) {
        currentProject = new Project(name);
        return currentProject;
    }

    public Project getCurrentProject() {
        return currentProject;
    }
}
