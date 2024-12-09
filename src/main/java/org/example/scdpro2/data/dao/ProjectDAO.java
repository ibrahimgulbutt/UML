/**
 * The ProjectDAO interface provides methods for saving and loading project data.
 * It defines operations to persist a project to a file and retrieve a project from a file.
 */

package org.example.scdpro2.data.dao;

import org.example.scdpro2.business.models.Project;

import java.io.File;

public interface ProjectDAO {
    /**
     * Saves the given project data to the specified file.
     *
     * @param project the project to be saved.
     * @param file    the file to which the project data will be saved.
     * @throws Exception if an error occurs during the save operation.
     */
    void saveProject(Project project, File file) throws Exception;
    /**
     * Loads a project from the specified file.
     *
     * @param file the file from which the project data will be loaded.
     * @return the loaded project.
     * @throws Exception if an error occurs during the load operation.
     */
    Project loadProject(File file) throws Exception;
}
