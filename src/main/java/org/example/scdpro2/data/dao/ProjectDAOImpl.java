package org.example.scdpro2.data.dao;

import org.example.scdpro2.business.models.Project;

import java.io.*;

public class ProjectDAOImpl implements ProjectDAO {

    @Override
    public void saveProject(Project project, File file) throws Exception {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(project);
        }
    }

    @Override
    public Project loadProject(File file) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Project) ois.readObject();
        }
    }
}