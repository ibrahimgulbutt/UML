package org.example.scdpro2.data.dao;

import org.example.scdpro2.business.models.Project;

import java.io.File;

public interface ProjectDAO {
    void saveProject(Project project, File file) throws Exception;
    Project loadProject(File file) throws Exception;
}
