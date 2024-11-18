package org.example.scdpro2.business.models;

// Abstract base class
public abstract class ModelComponent {
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
