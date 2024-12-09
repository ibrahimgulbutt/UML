package org.example.scdpro2.data.dao;

import org.example.scdpro2.business.models.Project;

import java.io.*;

public class ProjectDAOImpl implements ProjectDAO {
    /**
     * Saves the given {@link Project} to the specified file using object serialization.
     *
     * @param project the {@link Project} to be saved.
     * @param file    the file to which the project data will be saved.
     * @throws Exception if an error occurs during the save operation, such as
     *                   an I/O error or serialization failure.
     */
    @Override
    public void saveProject(Project project, File file) throws Exception {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(project);
        }
    }
    /**
     * Loads a {@link Project} from the specified file using object deserialization.
     *
     * @param file the file from which the project data will be loaded.
     * @return the loaded {@link Project}.
     * @throws Exception if an error occurs during the load operation, such as
     *                   an I/O error, file not found, or deserialization failure.
     */
    @Override
    public Project loadProject(File file) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Project) ois.readObject();
        }
    }
}