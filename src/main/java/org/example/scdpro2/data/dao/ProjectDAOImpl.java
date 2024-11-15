package org.example.scdpro2.data.dao;

import org.example.scdpro2.business.models.Project;
import org.example.scdpro2.data.repository.ProjectRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ProjectDAOImpl implements ProjectRepository {
    private Map<String, Project> projectDatabase = new HashMap<>();

    @Override
    public boolean saveProject(Project project) {
        projectDatabase.put(project.getName(), project);
        return true;
    }

    @Override
    public Optional<Project> findProjectByName(String name) {
        return Optional.ofNullable(projectDatabase.get(name));
    }

    @Override
    public boolean deleteProject(Project project) {
        return projectDatabase.remove(project.getName()) != null;
    }
}
