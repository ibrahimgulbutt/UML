package org.example.scdpro2.business.models.BClassDiagarm;

import java.io.Serializable;

// Abstract base class
public abstract class ModelComponent implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String name;

    public ModelComponent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract void validate();
    public abstract String generateCode();
}
