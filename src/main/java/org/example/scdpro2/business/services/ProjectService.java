package org.example.scdpro2.business.services;

import org.example.scdpro2.business.models.Project;
import org.example.scdpro2.data.repository.ProjectRepository;

import java.util.Optional;

public class ProjectService {
    private ProjectRepository projectRepository;
    private Project currentProject;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project createProject(String name) {
        currentProject = new Project(name);
        projectRepository.saveProject(currentProject);
        return currentProject;
    }

    public boolean saveProject(Project project) {
        return projectRepository.saveProject(project);
    }

    public Optional<Project> loadProject(String projectName) {
        currentProject = projectRepository.findProjectByName(projectName).orElse(null);
        return Optional.ofNullable(currentProject);
    }

    public Project getCurrentProject() {
        return currentProject;
    }
}
