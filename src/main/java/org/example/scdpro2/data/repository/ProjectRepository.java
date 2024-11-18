package org.example.scdpro2.data.repository;

import org.example.scdpro2.business.models.Project;

import java.util.Optional;

public interface ProjectRepository {
    boolean saveProject(Project project);

    Optional<Project> findProjectByName(String name);

    boolean deleteProject(Project project);
}
